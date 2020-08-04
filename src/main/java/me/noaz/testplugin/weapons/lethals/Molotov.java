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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Molotov extends ThrowableItem implements Lethal {
    private int noDamageTicksInMolotovArea = 10;
    private double damageWhenInMolotovArea = 1;
    List<Player> playersAlreadyInFire = new ArrayList<>();

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
                    map.getWorld().spawnParticle(Particle.FLAME, itemLocation, 40, 2, 0.30, 2, 0);

                    Collection<Entity> nearbyEntities = map.getWorld().getNearbyEntities(itemLocation, 2.5, 1, 2.5);
                    for(Entity entity : nearbyEntities) {
                        if(entity instanceof Player && !playersAlreadyInFire.contains(entity)) {
                            Player player = (Player) entity;

                            playersAlreadyInFire.add(player);
                            damagePlayer(player, itemLocation);
                        }
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0,1);
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

    private void damagePlayer(Player player, Location molotovCenter) {
        player.setFireTicks(1000); //Enough for the player to not lose the effect while standing in burning area

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if(player.getGameMode() == GameMode.SPECTATOR) {
                    playersAlreadyInFire.remove(player);
                    this.cancel();
                    //Possibly just x/z plane?
                } else if(molotovCenter.distance(player.getLocation()) > 3) {
                    player.setFireTicks(100);
                    playersAlreadyInFire.remove(player);
                    this.cancel();
                } else if(i % noDamageTicksInMolotovArea == 0){
                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(playerExtension.getPlayer(),
                            player, EntityDamageEvent.DamageCause.FIRE, damageWhenInMolotovArea);
                    player.setLastDamageCause(event);
                    plugin.getServer().getPluginManager().callEvent(event);
                }
                i++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public Material getMaterial() {
        return material;
    }
}
