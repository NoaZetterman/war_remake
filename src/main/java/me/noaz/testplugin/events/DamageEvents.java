package me.noaz.testplugin.events;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.tasks.GameController;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DamageEvents implements Listener {
    private GameController gameController;
    private TestPlugin plugin;

    public DamageEvents(GameController gameController, TestPlugin plugin) {
        this.gameController = gameController;
        this.plugin = plugin;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if(event.getHitEntity() instanceof Player && event.getEntity().getShooter() instanceof Player) {
            Player hitPlayer = (Player) event.getHitEntity();
            Player shooter = (Player) event.getEntity().getShooter();
            PlayerExtension hitPlayerExtension = gameController.getPlayerExtension(hitPlayer);
            PlayerExtension shooterExtension = gameController.getPlayerExtension(shooter);

            if(!gameController.getGame().playersOnSameTeam(hitPlayerExtension, shooterExtension) && hitPlayer.getHealth() != 0) {

                double damage;
                double eyeToNeckLength = 0.25;

                //Check if bullet was a headshot or not
                if (hitPlayer.getEyeLocation().getY() - eyeToNeckLength <= event.getEntity().getLocation().getY()) {
                    damage = (double) event.getEntity().getMetadata("headDamage").get(0).value();
                } else {
                    damage = (double) event.getEntity().getMetadata("bodyDamage").get(0).value();
                }

                double healthLeft = (hitPlayer.getHealth() - damage) <= 0 ? 0 : hitPlayer.getHealth() - damage;

                if(healthLeft <= 0) {
                    hitPlayer.sendMessage(hitPlayer.getName() + " was shot by " + shooter.getName());
                    shooter.sendMessage(shooter.getName() + " shot " + hitPlayer.getName());

                    hitPlayerExtension.getPlayerStatistics().addDeath();

                    PlayerStatistic killerStatistic = shooterExtension.getPlayerStatistics();

                    //Order on the two below matters. (or does it?)
                    killerStatistic.addXP(25);
                    shooterExtension.addKill();
                    respawn(hitPlayer, shooter);
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

            deadPlayer.sendMessage(deadPlayer.getName() + " was shot by " + killer.getName());
            killer.sendMessage(killer.getName() + " shot " + deadPlayer.getName());

            gameController.getPlayerExtension(deadPlayer).getPlayerStatistics().addDeath();

            PlayerExtension killerExtesion = gameController.getPlayerExtension(killer);
            PlayerStatistic killerStatistic = killerExtesion.getPlayerStatistics();

            //Order on the two below matters. (or does it?)
            killerStatistic.addXP(25);
            killerExtesion.addKill();
        }
    }

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent event) {
        //TODO: Fix so that players cant kill teammates with sword
        //Fix cactus pickuup
        PlayerExtension player = gameController.getPlayerExtension(event.getPlayer());
        if(player.isPlayingGame()) {
            Location loc = player.respawn();
            event.setRespawnLocation(loc);
        }
    }

    private void respawn(Player player, Player killer) {

        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(killer);
        //TODO: Fix when game ends and player still in spec DONE?
        BukkitRunnable runnable = new BukkitRunnable() {

            int i = 3;
            @Override
            public void run() {
                player.sendTitle("Respawning in " + i, "", 1,20,1);
                i--;
                //Add spawnres
                if(i < 0) {
                    player.teleport(gameController.getPlayerExtension(player).respawn());
                    player.setGameMode(GameMode.ADVENTURE);
                    this.cancel();
                } else if(gameController.getGame() == null) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6, 10));
                    player.setGameMode(GameMode.ADVENTURE);
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(plugin, 0, 20L);
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if((event.getEntity() instanceof Player) &&
                event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            event.setCancelled(true);
        }
    }
}
