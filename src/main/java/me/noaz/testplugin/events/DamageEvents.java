package me.noaz.testplugin.events;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerHandler;
import me.noaz.testplugin.player.PlayerStatistic;
import me.noaz.testplugin.tasks.GameController;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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

            if(!gameController.getGame().playersOnSameTeam(hitPlayer, shooter) && hitPlayer.getHealth() != 0) {

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

                    ((PlayerHandler) hitPlayer.getMetadata("handler").get(0).value()).getPlayerStatistics().addDeath();

                    PlayerHandler killerHandler = (PlayerHandler) shooter.getMetadata("handler").get(0).value();
                    PlayerStatistic killerStatistic = killerHandler.getPlayerStatistics();

                    //Order on the two below matters. (or does it?)
                    killerStatistic.addXP(25);
                    killerHandler.addKill();
                    respawn(hitPlayer, shooter);
                } else {

                    hitPlayer.damage(0.1, shooter); //To get the damage animation and correct player hit

                    hitPlayer.setHealth(healthLeft);

                    Vector knockback = event.getEntity().getVelocity().normalize();
                    knockback.setY(knockback.getY() * 0.05);
                    hitPlayer.setVelocity(knockback);
                }

                ((PlayerHandler) shooter.getMetadata("handler").get(0).value()).getPlayerStatistics().addBulletHit();
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

            ((PlayerHandler) deadPlayer.getMetadata("handler").get(0).value()).getPlayerStatistics().addDeath();

            PlayerHandler killerHandler = (PlayerHandler) killer.getMetadata("handler").get(0).value();
            PlayerStatistic killerStatistic = killerHandler.getPlayerStatistics();

            //Order on the two below matters. (or does it?)
            killerStatistic.addXP(25);
            killerHandler.addKill();
        }
    }

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent event) {
        //TODO: Fix so that players cant kill teammates with sword
        //Fix cactus pickuup
        PlayerHandler handler = ((PlayerHandler) event.getPlayer().getMetadata("handler").get(0).value());
        if(handler.isPlayingGame()) {
            Location loc = handler.respawn();
            event.setRespawnLocation(loc);
        }
    }

    private void respawn(Player player, Player killer) {

        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(killer);
        //TODO: Fix when game ends and player still in spec
        BukkitRunnable runnable = new BukkitRunnable() {

            int i = 3;
            @Override
            public void run() {
                player.sendTitle("Respawning in " + i, "", 1,20,1);
                i--;
                //Add spawnres
                if(i < 0) {
                    player.teleport(((PlayerHandler) player.getMetadata("handler").get(0).value()).respawn());
                    player.setGameMode(GameMode.ADVENTURE);
                    this.cancel();
                } else if(gameController.getGame() == null) {
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

    /*@EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntityType() == EntityType.PLAYER && event.)
    }*/
}
