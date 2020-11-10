package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GunCommands implements CommandExecutor {
    private final GameData data;

    public GunCommands(TestPlugin plugin, GameData data) {
        this.data = data;

        plugin.getCommand("gun").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args[0] != null && args[1] != null) {
            StringBuilder gunNameAsStringBuilder = new StringBuilder();
            for(int i = 1; i < args.length-1; i++) {
                gunNameAsStringBuilder.append(args[i]).append("_");
            }

            gunNameAsStringBuilder.append(args[args.length - 1]);
            String gunNameRemoveThis = gunNameAsStringBuilder.toString();
            switch (args[0].toLowerCase()) {
                case "save":
                    if(gunNameRemoveThis.equals("ALL")) {
                        for(GunConfiguration gunConfiguration : data.getGunConfigurations()) {
                            if (data.saveGunConfiguration(gunConfiguration.getName())) {
                                sender.sendMessage("Saving gun: " + gunConfiguration.getName());
                            } else {
                                sender.sendMessage("Failed saving gun:" + gunConfiguration.getName());
                            }
                        }
                    } else {
                        if (data.saveGunConfiguration(gunNameRemoveThis)) {
                            sender.sendMessage("Saving gun: " + gunNameRemoveThis);
                        } else {
                            sender.sendMessage("Failed saving gun");
                        }
                    }
                    break;
                case "add":
                    if(data.createNewGunConfiguration(gunNameRemoveThis)) {
                        sender.sendMessage("Added gun: " + gunNameRemoveThis);
                    } else {
                        sender.sendMessage("Failed to add gun");
                    }
                    break;
                case "delete":
                    if(data.deleteGunConfiguration(gunNameRemoveThis)) {
                        sender.sendMessage("The gun " + gunNameRemoveThis + " was deleted from the database " +
                                "but will still be accessible in game until restart");
                    } else {
                        sender.sendMessage("Failed, maybe the gun is already deleted? ");
                    }
                    break;
                case "update":
                    if(args[3] == null) {
                        return false;
                    }

                    String gunName = args[1];
                    String field =  args[2];

                    //Support spaces, for display names
                    StringBuilder val = new StringBuilder();
                    for(int i = 3; i < args.length-1; i++) {
                        val.append(args[i]).append(" ");
                    }

                    val.append(args[args.length - 1]);
                    String value = val.toString();

                    try {
                        data.updateGunConfiguration(gunName, field, value);
                        sender.sendMessage("Set " + field + " to: " + value + " for gun " + gunName);
                    } catch(NumberFormatException e) {
                        sender.sendMessage("Invalid data format, failed to set value");
                    }
                    break;
                case "info":
                    List<GunConfiguration> gunConfigurations = data.getGunConfigurations();

                    for(GunConfiguration gunConfiguration : gunConfigurations) {
                        if(gunConfiguration.getName().equals(args[1])) {
                            String[] message = new String[28];
                            message[0] =  "Fire while reloading sound: " + ChatColor.YELLOW + gunConfiguration.getFireWhileReloadingSound();
                            message[1] =  "Fire sound: " + ChatColor.YELLOW + gunConfiguration.getFireBulletSound();
                            message[2] =  "Fire without ammo sound: " + ChatColor.YELLOW + gunConfiguration.getFireWithoutAmmoSound();
                            message[3] =  "Material: " + ChatColor.YELLOW + gunConfiguration.getMaterial();
                            message[4] =  "Weapon lore:" + ChatColor.YELLOW + gunConfiguration.getWeaponLore();
                            message[5] =  "Cost To Buy: " + ChatColor.YELLOW + gunConfiguration.getCostToBuy();
                            message[6] =  "Unlock level: " + ChatColor.YELLOW + gunConfiguration.getUnlockLevel();
                            message[7] =  "ClipSize: " + ChatColor.YELLOW + gunConfiguration.getClipSize();
                            message[8] =  "Name: " + ChatColor.YELLOW + gunConfiguration.getName();
                            message[9] =  "Display Name: " + ChatColor.YELLOW + gunConfiguration.getDisplayName();
                            message[10] = "Loadout menu slot: " + ChatColor.YELLOW + gunConfiguration.getLoadoutMenuSlot();
                            message[11] = "Starting bullets: " + ChatColor.YELLOW + gunConfiguration.getStartingBullets();
                            message[12] = "Scav ammuniiton: " + ChatColor.YELLOW + gunConfiguration.getScavengerAmmunition();
                            message[13] = "Resupply ammunition: " + ChatColor.YELLOW + gunConfiguration.getMaxResupplyAmmunition();
                            message[14] = "Gun range in blocks: " + ChatColor.YELLOW + gunConfiguration.getRange();
                            message[15] = "Bullet speed: " + ChatColor.YELLOW + gunConfiguration.getBulletSpeed();
                            message[16] = "Bullets per burst: " + ChatColor.YELLOW + gunConfiguration.getBulletsPerBurst();
                            message[17] = "Bullets per click: " + ChatColor.YELLOW + gunConfiguration.getBulletsPerClick();
                            message[18] = "Burst delay in ticks: " + ChatColor.YELLOW + gunConfiguration.getBurstDelayInTicks() + " - also used for non-bursts as delay between bullets";
                            message[19] = "FireType (only function): " + ChatColor.YELLOW + gunConfiguration.getFireType();
                            message[20] = "GunType (visual): " + ChatColor.YELLOW + gunConfiguration.getGunType();
                            message[21] = "Reload time in ticks: " + ChatColor.YELLOW + gunConfiguration.getReloadTimeInTicks();
                            message[22] = "Damage dropoff (per tick): " + ChatColor.YELLOW + gunConfiguration.getDamageDropoffPerTick();
                            message[23] = "Damage dropoff start after: " + ChatColor.YELLOW + gunConfiguration.getDamageDropoffStartAfterTick();
                            message[24] = "Accuracy not Scoped: " + ChatColor.YELLOW + gunConfiguration.getAccuracyNotScoped();
                            message[25] = "Accuracy scoped: " + ChatColor.YELLOW + gunConfiguration.getAccuracyScoped();
                            message[26] = "Body damage: " + ChatColor.YELLOW + gunConfiguration.getBodyDamage();
                            message[27] = "Head damage: " + ChatColor.YELLOW + gunConfiguration.getHeadDamage();

                            sender.sendMessage(message);
                        }
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }

        return false;
    }
}
