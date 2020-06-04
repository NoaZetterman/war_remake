package me.noaz.testplugin.gamemodes.misc;

import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

/**
 * Represents a flag in Capture the Flag, takes care of everything regarding the flag, such as captures
 *
 * @author Noa Zetterman
 * @version 2020-03-06
 */
public class Flag {
    private TestPlugin plugin;
    private Color flagColor;
    private Location flagPoleLocation;
    private Location enemyFlagPoleLocation;
    private BukkitRunnable woolFlagTask;
    private PlayerExtension flagHolder = null;
    private HashMap<Player, PlayerExtension> players;

    private Item flag;
    private ItemStack flagItemStack;

    private GameMap map;
    private int captures = 0;

    /**
     *
     * @param color The color of the team
     * @param flagPoleLocation The location of the teams flag
     * @param enemyFlagPoleLocation The location of the enemy teams flag
     * @param map The map
     * @param plugin This plugin.
     */
    public Flag(Color color, Location flagPoleLocation, Location enemyFlagPoleLocation, GameMap map, TestPlugin plugin, HashMap<Player, PlayerExtension> players) {
        this.flagColor = color;
        this.flagPoleLocation = flagPoleLocation.clone();
        this.enemyFlagPoleLocation = enemyFlagPoleLocation.clone();
        this.map = map;
        this.plugin = plugin;
        this.players = players;

        createWoolFlag();
    }

    /*
    private void createBannerFlag() {

        ItemStack banner;
        if(flagColor == Color.RED) {
            banner = new ItemStack(Material.RED_BANNER);
        } else {
            banner = new ItemStack(Material.BLUE_BANNER);
        }

        Location adjustedFlagPoleLocation = flagPoleLocation.add(0.5,-1.85,0.75);

        flagPole = (ArmorStand) plugin.getServer().getWorld(worldName).spawnEntity(adjustedFlagPoleLocation, EntityType.ARMOR_STAND);

        flagPole.setGravity(false);
        flagPole.setInvulnerable(true);
        flagPole.setVisible(true);
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
                        if (entity.getType() == EntityType.PLAYER && ((Player)entity).getGameMode() == GameMode.ADVENTURE) {
                            PlayerExtension player = players.get(entity);

                            if(player.getTeamColor() != flagColor) {

                                //Make player pick up flag
                                flagPole.setHelmet(null);
                                player.setHelmet(banner);
                                flagHolder = player;

                                plugin.getServer().broadcastMessage(flagHolder.getName() + " picked up the flag");
                            }
                        }
                    }
                } else {

                    //Improve this
                    //Fix Height.
                    double lengthFromPlayerToFlag = Math.sqrt(Math.pow(enemyFlagPoleLocation.getX()-flagHolder.getLocation().getX(),2)
                            + Math.pow(enemyFlagPoleLocation.getZ()-flagHolder.getLocation().getZ(),2));
                    if(Math.abs(lengthFromPlayerToFlag) < 0.75 && Math.sqrt(Math.pow(enemyFlagPoleLocation.getY() - flagHolder.getLocation().getY(),2)) < 2) {
                        //enemyFlagPoleLocation.getX()^2 + enemyFlagPoleLocation.getZ()^2 - (flagHolder.getX()^2 + flagHolder.getZ()^2)
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

        task.runTaskTimer(plugin, 0, 1L);
    }
    */

    public void createWoolFlag() {
        if(flagColor == Color.RED) {
            flagItemStack = new ItemStack(Material.RED_WOOL);
        } else {
            flagItemStack = new ItemStack(Material.BLUE_WOOL);
        }

        flag = map.getWorld().dropItem(flagPoleLocation.add(0,0.5,0), flagItemStack);
        flag.setGravity(false);
        flag.setVelocity(new Vector(0,0,0));
        flag.setPickupDelay(10000);

        woolFlagTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(flagHolder == null) {
                    flag.setTicksLived(1);
                    for(PlayerExtension player : players.values()) {

                        double lengthFromPlayerToFlag = Math.sqrt(Math.pow(flagPoleLocation.getX()-player.getLocation().getX(),2)
                                + Math.pow(flagPoleLocation.getZ()-player.getLocation().getZ(),2));
                        if(lengthFromPlayerToFlag < 0.75 && Math.sqrt(Math.pow(flagPoleLocation.getY() - player.getLocation().getY(),2)) < 2
                                && player.getPlayer().getGameMode() == GameMode.ADVENTURE) {

                            if(player.getTeamColor() != flagColor) {
                                //Make player pick up flag
                                flagHolder = player;

                                spawnFlag(flagHolder.getLocation().add(0,1.5,0));

                                BroadcastMessage.pickedUpFlag(flagHolder);
                            }
                        }
                    }
                } else {
                    double lengthFromPlayerToFlag = Math.sqrt(Math.pow(enemyFlagPoleLocation.getX()-flagHolder.getLocation().getX(),2)
                            + Math.pow(enemyFlagPoleLocation.getZ()-flagHolder.getLocation().getZ(),2));
                    if(Math.abs(lengthFromPlayerToFlag) < 0.75 && Math.sqrt(Math.pow(enemyFlagPoleLocation.getY() - flagHolder.getLocation().getY(),2)) < 2) {
                        //Cap the flag
                        BroadcastMessage.capturedFlag(flagHolder);
                        captures++;

                        flagHolder.captureFlag();

                        flagHolder = null;
                        spawnFlag(flagPoleLocation);
                    } else if(flagHolder.isDead()){
                        BroadcastMessage.droppedFlag(flagHolder);

                        flagHolder = null;

                        spawnFlag(flagPoleLocation);
                    } else {
                        spawnFlag(flagHolder.getLocation().add(0,2,0));

                    }
                }
            }
        };

        woolFlagTask.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnFlag(Location flagLocation) {
        flag.remove();
        flag = map.getWorld().dropItem(flagLocation, flagItemStack);
        flag.setGravity(false);
        flag.setVelocity(new Vector(0,0,0));
        flag.setPickupDelay(10000);
        flag.setTicksLived(1);
    }

    //TODO: Fix helmet situation (maybe)
    //Consider having a player tick thing that checks if player is close to flag rather than the other way around.
    //Less efficient? No - Still gotta check once for every player per time period.
    //private void createWoolFlag for later

    /**
     * Safely removes the the flag.
     */
    public void stop() {
        woolFlagTask.cancel();
        //task.cancel();
        //flagPole.remove();
    }
}
