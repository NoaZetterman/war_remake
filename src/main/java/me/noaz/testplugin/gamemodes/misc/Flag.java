package me.noaz.testplugin.gamemodes.misc;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Represents a flag in Capture the Flag, takes care of everything regarding the flag, such as captures
 *
 * @author Noa Zetterman
 * @version 2020-03-06
 */
public class Flag {
    private TestPlugin plugin;
    private Color color;
    private Location flagPoleLocation;
    private Location enemyFlagPoleLocation;
    private BukkitRunnable task;
    private ArmorStand flagPole;
    private Player flagHolder = null;

    private String worldName;
    private int captures = 0;

    /**
     *
     * @param color The color of the team
     * @param flagPoleLocation The location of the teams flag
     * @param enemyFlagPoleLocation The location of the enemy teams flag
     * @param worldName The name of the map
     * @param plugin This plugin.
     */
    public Flag(Color color, Location flagPoleLocation, Location enemyFlagPoleLocation, String worldName, TestPlugin plugin) {
        this.color = color;
        this.flagPoleLocation = flagPoleLocation;
        this.enemyFlagPoleLocation = enemyFlagPoleLocation;
        this.worldName = worldName;
        this.plugin = plugin;
        //Spawn in the flag

        createBannerFlag();
    }

    /**
     * @return The amount of times this flag has been captured
     */
    public int getCaptures() {
        return captures;
    }

    private void createBannerFlag() {

        ItemStack banner;
        if(color == Color.RED) {
            banner = new ItemStack(Material.RED_BANNER);
        } else {
            banner = new ItemStack(Material.BLUE_BANNER);
        }

        Location adjustedFlagPoleLocation = flagPoleLocation.add(0.5,-1.85,0.75);

        flagPole = (ArmorStand) plugin.getServer().getWorld(worldName).spawnEntity(adjustedFlagPoleLocation, EntityType.ARMOR_STAND);

        flagPole.setGravity(false);
        flagPole.setInvulnerable(true);
        flagPole.setVisible(false);
        flagPole.setSmall(false);
        flagPole.setCollidable(false);
        flagPole.setHelmet(banner);
        flagPole.setArms(false);
        flagPole.setBasePlate(false);
        flagPole.setMarker(false);




        task = new BukkitRunnable() {
            ItemStack playerHelmet;

            @Override
            public void run() {

                if(flagHolder == null) {
                    List<Entity> entities = flagPole.getNearbyEntities(1, 2, 1);
                    for (Entity e : entities) {
                        if (e.getType() == EntityType.PLAYER) {
                            Player player = (Player) e;

                            PlayerHandler handler = (PlayerHandler) player.getMetadata("handler").get(0).value();
                            if (handler.getTeamColor() != color) {

                                //Make player pick up flag
                                flagPole.setHelmet(null);

                                playerHelmet = player.getInventory().getHelmet(); //Fix color
                                player.getInventory().setHelmet(banner);
                                flagHolder = player;

                                plugin.getServer().broadcastMessage(flagHolder.getName() + " picket up the flag");
                            }
                        }
                    }
                } else {

                    if(enemyFlagPoleLocation.distance(flagHolder.getLocation()) < 1) {
                        //Cap the flag
                        plugin.getServer().broadcastMessage(flagHolder.getName() + " captured the flag");
                        captures++;

                        flagHolder.getInventory().setHelmet(playerHelmet);
                        flagPole.setHelmet(banner);
                        flagHolder = null;
                    } else if(flagHolder.isDead()) {
                        plugin.getServer().broadcastMessage(flagHolder.getName() + " dropped flag");
                        flagPole.setHelmet(banner);
                        flagHolder = null;
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 0, 20L);
    }

    //TODO: Fix helmet situation
    //private void createWoolFlag for later

    /**
     * Safely removes the the flag.
     */
    public void stop() {
        task.cancel();
        flagPole.remove();
    }
}
