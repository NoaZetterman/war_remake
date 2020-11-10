package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.Buyable;
import me.noaz.testplugin.GameData;
import me.noaz.testplugin.weapons.guns.FireType;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import me.noaz.testplugin.weapons.guns.GunType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TabCompleteForGunCommands implements TabCompleter {
    private final GameData data;

    public TabCompleteForGunCommands(GameData data) {
        this.data = data;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("gun")) {
            List<String> listWithOnlyMatching = new ArrayList<>();
            List<String> list = new ArrayList<>();
            switch(args.length) {
                case 1:
                    list.add("info");
                    list.add("update");
                    list.add("save");
                    list.add("add");
                    list.add("delete");

                    StringUtil.copyPartialMatches(args[0], list, listWithOnlyMatching);

                    break;
                case 2:
                    list = data.getGunNames();
                    if(args[0].toLowerCase().equals("save")) {
                        list.add("ALL");
                    }
                    StringUtil.copyPartialMatches(args[1], list, listWithOnlyMatching);
                    break;
                case 3:
                    if (args[0].toLowerCase().equals("update")) {
                        for (Field field : GunConfiguration.class.getDeclaredFields()) {
                            list.add(field.getName());
                        }
                        for (Field field : Buyable.class.getDeclaredFields()) {
                            list.add(field.getName());
                        }
                        StringUtil.copyPartialMatches(args[2], list, listWithOnlyMatching);
                    }
                    break;
                case 4:
                    if (args[0].toLowerCase().equals("update")) {
                        try {
                            list.add(GunConfiguration.class.getDeclaredField(args[2]).getType().getSimpleName());
                        } catch (NoSuchFieldException ignored) {
                        }
                        try {
                            list.add(Buyable.class.getDeclaredField(args[2]).getType().getSimpleName());
                        } catch (NoSuchFieldException ignored) {
                        }

                        if (list.get(0).equals("GunType")) {
                            list.remove(0);
                            for (GunType gunType : GunType.values()) {
                                list.add(gunType.name());
                            }
                        }

                        if (list.get(0).equals("FireType")) {
                            list.remove(0);
                            for (FireType fireType : FireType.values()) {
                                list.add(fireType.name());
                            }
                        }

                        if (list.get(0).equals("Sound")) {
                            list.remove(0);
                            for (Sound sound : Sound.values()) {
                                list.add(sound.name());
                            }
                        }

                        if (list.get(0).equals("Material")) {
                            list.remove(0);
                            for (Material material : Material.values()) {
                                list.add(material.name());
                            }
                        }

                        if (list.size() == 0) {
                            list.add("Unknown, " + args[2] + " is not a valid value");
                        }
                        //This is information and should not match to certain values
                        StringUtil.copyPartialMatches(args[3], list, listWithOnlyMatching);
                    }
                    break;
            }

            return listWithOnlyMatching;
        }

        return null;
    }
}
