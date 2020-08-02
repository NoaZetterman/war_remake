package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.ThrowableItem;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class Molotov extends ThrowableItem implements Lethal {
    public Molotov(PlayerExtension playerExtension, GameMap map, TestPlugin plugin) {
        super(playerExtension, map, plugin, Material.APPLE, 5, 1.3f, 3, 2, 20);
    }

    @Override
    protected void activateItem(Location itemLocation) {

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                i++;

                if(i < 200) {
                    map.getWorld().spawnParticle(Particle.FLAME, itemLocation, 25, 2, 0, 2, 0);
                    Collection<Entity> nearbyEntities = map.getWorld().getNearbyEntities(itemLocation, 2.5, 1, 2.5);
                    for(Entity entity : nearbyEntities) {
                        if(entity instanceof Player) {
                            Player player = (Player) entity;

                            if(player.getGameMode() != GameMode.SPECTATOR) {

                                EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(playerExtension.getPlayer(),
                                        player, EntityDamageEvent.DamageCause.FIRE, 1);
                                player.setLastDamageCause(event); //Use or not`?
                                player.setNoDamageTicks(0);
                                plugin.getServer().getPluginManager().callEvent(event);
                                player.setFireTicks(200);

                            }

                        }
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0,5);
        //Since I need to spawn this each and every tick, then I might as well do other thing each tick?
    }

    @Override
    public void flyUntilRemove(Item item) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if(item.isOnGround()) {
                    activateItem(item.getLocation());
                    item.remove();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0,1);
    }

    @Override
    public Material getMaterial() {
        return material;
    }
}
