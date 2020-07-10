package me.noaz.testplugin.events;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.GameLoop;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.messages.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.Reward;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DamageEvents implements Listener {
    private GameData data;
    private GameLoop gameLoop;

    public DamageEvents(GameData data, GameLoop gameLoop) {
        this.data = data;
        this.gameLoop = gameLoop;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        //TODO: Reduce hitbox size (maybe)
/*        Location snowballLocation = event.getEntity().getLocation();
        Player hitPlayer = (Player)event.getHitEntity();
        hitPlayer.getLocation().getY*/

        if(event.getHitEntity() instanceof Player && event.getEntity().getShooter() instanceof Player) {
            Player hitPlayer = (Player) event.getHitEntity();
            Player shooter = (Player) event.getEntity().getShooter();
            PlayerExtension hitPlayerExtension = data.getPlayerExtension(hitPlayer);
            PlayerExtension shooterExtension = data.getPlayerExtension(shooter);

            if(!gameLoop.getCurrentGame().playersOnSameTeam(hitPlayerExtension, shooterExtension) && hitPlayer.getHealth() != 0) {

                double damage;
                double eyeToNeckLength = 0.25;

                //Check if bullet was a headshot or not
                //Maybe not hit when its too far away from body?
                boolean isHeadshot;
                if (hitPlayer.getEyeLocation().getY() - eyeToNeckLength <= event.getEntity().getLocation().getY()) {
                    damage = (double) event.getEntity().getMetadata("headDamage").get(0).value();
                    //Headshot
                    isHeadshot = true;
                } else {
                    damage = (double) event.getEntity().getMetadata("bodyDamage").get(0).value();
                    isHeadshot = false;
                }

                double healthLeft = 20.0;
                if(!hitPlayer.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                    healthLeft = (hitPlayer.getHealth() - damage) <= 0 ? 0 : hitPlayer.getHealth() - damage;
                }

                if(healthLeft <= 0) {
                    hitPlayerExtension.addDeath();
                    hitPlayerExtension.respawn(shooter);

                    if(isHeadshot) {
                        ChatMessage.playerWasHeadshotToDeath(hitPlayer, shooter, shooterExtension.getTeamChatColor());
                        ChatMessage.playerHeadshotKilled(shooter, hitPlayer,
                                hitPlayerExtension.getTeamChatColor(), gameLoop.getCurrentGamemode());

                        shooterExtension.addKill(Reward.HEADSHOT_KILL);
                    } else {
                        ChatMessage.playerWasShotToDeath(hitPlayer, shooter, shooterExtension.getTeamChatColor());
                        ChatMessage.playerShotKilled(shooter, hitPlayer,
                                hitPlayerExtension.getTeamChatColor(), gameLoop.getCurrentGamemode());

                        shooterExtension.addKill(Reward.BODYSHOT_KILL);
                    }

                    //Print death messages before adding the kills to print
                    //eventual killstreaks after player gets killed,
                    //And add death before addKill to prevent doublekill with nuke

                } else {

                    hitPlayer.damage(0.1, shooter); //To get the damage animation and correct player hit

                    hitPlayer.setHealth(healthLeft);

                    Vector knockback = event.getEntity().getVelocity().normalize();
                    knockback.setY(knockback.getY() * 0.05);
                    hitPlayer.setVelocity(knockback);
                }

                shooterExtension.getPlayerInformation().addBulletHit();
                shooterExtension.updateGameScoreboard();
            }
        } else if(event.getHitBlock() != null && event.getHitBlock().getType().equals(Material.GLASS_PANE)) {
            event.getHitBlock().setType(Material.AIR);
            //Maybe if block is invisible then remove the block and not the projectile?
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        //Maybe "simulate" death instead and tp player to spawn when it should die. => Not this event
        event.setKeepLevel(true);
        event.setDroppedExp(0);
        event.setDeathMessage(null);
        event.getDrops().clear();

        if(event.getEntity().getKiller() != null) {

            Player deadPlayer = event.getEntity();
            Player killer = event.getEntity().getKiller();

            PlayerExtension killerExtension = data.getPlayerExtension(killer);
            PlayerExtension deadPlayerExtension = data.getPlayerExtension(deadPlayer);

            if(gameLoop.getCurrentGamemode() == Gamemode.INFECT) {
                //Puts the player on the other team if alive
                gameLoop.getCurrentGame().assignTeam(deadPlayerExtension);

            } else {

                ChatMessage.playerWasShotToDeath(deadPlayer, killer, killerExtension.getTeamChatColor());
                ChatMessage.playerShotKilled(killer, deadPlayer,
                        deadPlayerExtension.getTeamChatColor(), gameLoop.getCurrentGamemode());

                killerExtension.addKill(Reward.KNIFE_KILL);
            }
        }
    }

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent event) {
        PlayerExtension player = data.getPlayerExtension(event.getPlayer());
        if(player.isPlayingGame()) {
            player.respawn(event.getPlayer().getKiller());
        }
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player &&
                event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Player damagedPlayer = (Player) event.getEntity();
                Player damager = (Player) event.getDamager();
                PlayerExtension damagedPlayerExtension = data.getPlayerExtension(damagedPlayer);
                PlayerExtension damagerExtension = data.getPlayerExtension(damager);

                if (gameLoop.getCurrentGame().playersOnSameTeam(damagedPlayerExtension, damagerExtension)) {
                    event.setCancelled(true);
                } else {
                    switch (event.getCause()) {
                        case ENTITY_ATTACK:
                            if (gameLoop.getCurrentGamemode() == Gamemode.INFECT && damagedPlayerExtension.getTeamColor() != Color.GREEN) {
                                //Put the player on the zombie team if human
                                event.setCancelled(true);

                                ChatMessage.playerWasInfectedDeath(damagedPlayer, damager, damagerExtension.getTeamChatColor());
                                ChatMessage.playerInfectedKilled(damager, damagedPlayer, damagedPlayerExtension.getTeamChatColor());

                                damagerExtension.addKill(Reward.ZOMBIE_KILL_HUMAN);

                                damagedPlayerExtension.respawn(damager);
                                damagedPlayerExtension.addDeath();
                            } else if (damagedPlayer.getHealth() - event.getDamage() <= 0) {
                                event.setCancelled(true);

                                ChatMessage.playerWasKnifedToDeath(damagedPlayer, damager, damagerExtension.getTeamChatColor());
                                ChatMessage.playerKnifeKilled(damager, damagedPlayer,
                                        damagedPlayerExtension.getTeamChatColor(), gameLoop.getCurrentGamemode());

                                damagerExtension.addKill(Reward.KNIFE_KILL);

                                damagedPlayerExtension.respawn(damager);
                                damagedPlayerExtension.addDeath();
                            }
                            break;
                        case ENTITY_EXPLOSION:
                            if(false/*Infect stuff)*/) {

                            } else if (damagedPlayer.getHealth() - event.getDamage() <= 0) {
                                event.setCancelled(true);

                                ChatMessage.playerWasGrenadedToDeath(damagedPlayer, damager, damagerExtension.getTeamChatColor());
                                ChatMessage.playerGrenadeKilled(damager, damagedPlayer,
                                        damagedPlayerExtension.getTeamChatColor(), gameLoop.getCurrentGamemode());

                                damagerExtension.addKill(Reward.KNIFE_KILL);

                                damagedPlayerExtension.respawn(damager);
                                damagedPlayerExtension.addDeath();
                            }
                            break;
                        default:
                            break;

                    }
                }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();
            PlayerExtension damagedPlayerExtension = data.getPlayerExtension(damagedPlayer);

            switch(event.getCause()) {
                case VOID:
                    if(!damagedPlayerExtension.isDead()) {
                        if (damagedPlayerExtension.isPlayingGame()) {
                            damagedPlayerExtension.addDeath();
                            damagedPlayerExtension.respawn(null);
                            ChatMessage.playerOutOfMapKilled(damagedPlayer);
                        } else {
                            damagedPlayer.teleport(damagedPlayer.getWorld().getSpawnLocation());
                        }
                    }

                    event.setCancelled(true);


                    //TODO: Get last damage that wasn't void
                    break;
                //Add more cases(?)
                case FALL:
                    if(event.getDamage() > damagedPlayer.getHealth()) {
                        if(damagedPlayerExtension.isPlayingGame()) {
                            damagedPlayerExtension.addDeath();
                            damagedPlayerExtension.respawn(null);
                        } else {
                            damagedPlayer.teleport(damagedPlayer.getWorld().getSpawnLocation());
                        }
                        event.setCancelled(true);
                    }
                    break;
                default:
                    break;
            }

        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        System.out.println("EntityExplodeEvent: " + event.getEntity());
    }

    @EventHandler
    public void onBarrierBlockWalk(PlayerMoveEvent event) {
        PlayerExtension player = data.getPlayerExtension(event.getPlayer());
        if(event.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BARRIER &&
           !player.isDead()) {
            player.addDeath();
            player.respawn(null);
            ChatMessage.playerOutOfMapKilled(event.getPlayer());
        }
    }
}
