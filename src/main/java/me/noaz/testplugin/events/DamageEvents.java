package me.noaz.testplugin.events;

import me.noaz.testplugin.Messages.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.tasks.GameController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DamageEvents implements Listener {
    private GameController gameController;

    public DamageEvents(GameController gameController) {
        this.gameController = gameController;
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
            PlayerExtension hitPlayerExtension = gameController.getPlayerExtension(hitPlayer);
            PlayerExtension shooterExtension = gameController.getPlayerExtension(shooter);

            if(!gameController.getGame().playersOnSameTeam(hitPlayerExtension, shooterExtension) && hitPlayer.getHealth() != 0) {

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
                    if(isHeadshot) {
                        ChatMessage.playerWasHeadshotToDeath(hitPlayer, shooter);
                        ChatMessage.playerHeadshotKilled(shooter, hitPlayer);
                    } else {
                        ChatMessage.playerWasShotToDeath(hitPlayer, shooter);
                        ChatMessage.playerShotKilled(shooter, hitPlayer);
                    }

                    hitPlayerExtension.addDeath();
                    hitPlayerExtension.respawn(shooter);
                    //Print death messages before adding the kills to print
                    //eventual killstreaks after player gets killed,
                    //And add death before addKill to prevent doublekill with nuke

                    if(isHeadshot) {
                        shooterExtension.addHeadshotKill();
                        shooterExtension.addXp(35);
                        shooterExtension.changeCredits(2);
                    } else {
                        shooterExtension.addXp(25);
                        shooterExtension.changeCredits(1);
                        shooterExtension.addKill();
                    }

                } else {

                    hitPlayer.damage(0.1, shooter); //To get the damage animation and correct player hit

                    hitPlayer.setHealth(healthLeft);

                    Vector knockback = event.getEntity().getVelocity().normalize();
                    knockback.setY(knockback.getY() * 0.05);
                    hitPlayer.setVelocity(knockback);
                }

                shooterExtension.getPlayerStatistics().addBulletHit();
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

            ChatMessage.playerWasShotToDeath(deadPlayer, killer);
            ChatMessage.playerShotKilled(killer, deadPlayer);

            gameController.getPlayerExtension(deadPlayer).addDeath();

            PlayerExtension killerExtension = gameController.getPlayerExtension(killer);

            killerExtension.addXp(25);
            killerExtension.changeCredits(1);
            killerExtension.addKill();
        }
    }

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent event) {
        PlayerExtension player = gameController.getPlayerExtension(event.getPlayer());
        if(player.isPlayingGame()) {
            player.respawn(event.getPlayer().getKiller());
        }
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if((event.getEntity() instanceof Player) &&
                event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            PlayerExtension damagedPlayerExtension = gameController.getPlayerExtension(damagedPlayer);
            PlayerExtension damagerExtension = gameController.getPlayerExtension(damager);

            if(gameController.getGame().playersOnSameTeam(damagedPlayerExtension, damagerExtension)) {
                event.setCancelled(true);
            } else if(((Player) event.getEntity()).getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                damagedPlayerExtension.respawn(damager);
                damagedPlayerExtension.addDeath();

                ChatMessage.playerWasKnifedToDeath(damagedPlayer, damager);
                ChatMessage.playerKnifeKilled(damagedPlayer, damager);

                damagerExtension.changeCredits(1);
                damagerExtension.addKill();
                damagerExtension.addXp(25);

            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();
            PlayerExtension damagedPlayerExtension = gameController.getPlayerExtension(damagedPlayer);


            switch(event.getCause()) {
                case VOID:
                    if (damagedPlayerExtension.isPlayingGame()) {
                        damagedPlayerExtension.addDeath();
                        damagedPlayerExtension.respawn(null);
                    } else {
                        damagedPlayer.teleport(damagedPlayer.getWorld().getSpawnLocation());
                    }

                    event.setCancelled(true);


                    //TODO: Get last damage that wasn't void
                    break;
                //Add more cases(?)
                default:
                    break;
            }

        }
    }
}
