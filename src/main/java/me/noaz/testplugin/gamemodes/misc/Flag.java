package me.noaz.testplugin.gamemodes.misc;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
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
    private PlayerExtension flagHolder = null;
    private HashMap<Player, PlayerExtension> players;

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
    public Flag(Color color, Location flagPoleLocation, Location enemyFlagPoleLocation, String worldName, TestPlugin plugin, HashMap<Player, PlayerExtension> players) {
        this.color = color;
        this.flagPoleLocation = flagPoleLocation;
        this.enemyFlagPoleLocation = enemyFlagPoleLocation;
        this.worldName = worldName;
        this.plugin = plugin;
        this.players = players;
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

            @Override
            public void run() {

                if(flagHolder == null) {
                    List<Entity> entities = flagPole.getNearbyEntities(1, 2, 1);
                    for (Entity entity : entities) {
                        if (entity.getType() == EntityType.PLAYER) {
                            PlayerExtension player = players.get(entity);

                            if(player.getTeamColor() != color) {

                                //Make player pick up flag
                                flagPole.setHelmet(null);
                                player.setHelmet(banner);
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

                        //Dismount the flag first? cus player helemt is not there.

                        flagHolder.setHelmet();
                        flagPole.setHelmet(banner);
                        flagHolder = null;
                    } else if(flagHolder.isDead()){
                        plugin.getServer().broadcastMessage(flagHolder.getName() + " dropped flag");
                        flagPole.setHelmet(banner);
                        flagHolder = null;
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 0, 5L);
    }

    //TODO: Fix helmet situation (maybe)
    //Consider having a player tick thing that checks if player is close to flag rather than the other way around.
    //Less efficient? No - Still gotta check once for every player per time period.
    //private void createWoolFlag for later

    /**
     * Safely removes the the flag.
     */
    public void stop() {
        System.out.println("Removing task");
        task.cancel();
        System.out.println("Taskk cancelled");
        flagPole.remove();
    }
}
