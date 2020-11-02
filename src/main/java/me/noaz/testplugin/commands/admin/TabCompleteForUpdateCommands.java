package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.Buyable;
import me.noaz.testplugin.GameData;
import me.noaz.testplugin.dao.GunDao;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TabCompleteForUpdateCommands implements TabCompleter {
    private final GameData data;

    public TabCompleteForUpdateCommands(GameData data) {
        this.data = data;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player){
            List<String> list = new ArrayList<>();
            List<String> listWithOnlyMatching = new ArrayList<>();
            switch(args.length) {
                case 1:
                    list.add("gun");
                    list.add("map");
                    StringUtil.copyPartialMatches(args[0], list, listWithOnlyMatching);
                    break;
                case 2:
                    if(args[0].toLowerCase().equals("map")) {
                        list = data.getMapNames();
                    } else if(args[0].toLowerCase().equals("gun")){
                        list = data.getGunNames();
                    }
                    StringUtil.copyPartialMatches(args[1], list, listWithOnlyMatching);
                    break;
                case 3:
                    if(args[0].toLowerCase().equals("gun")) {
                        for(Field field : GunConfiguration.class.getDeclaredFields()) {
                            list.add(field.getName());
                        }
                        for(Field field: Buyable.class.getDeclaredFields()) {
                            list.add(field.getName());
                        }
                    }
                    StringUtil.copyPartialMatches(args[2], list, listWithOnlyMatching);
                    break;
                case 4:
                    try {
                            list.add(GunConfiguration.class.getDeclaredField(args[2]).getType().getSimpleName());
                    } catch (NoSuchFieldException ignored) {}
                    try {
                        list.add(Buyable.class.getDeclaredField(args[2]).getType().getSimpleName());
                    } catch (NoSuchFieldException ignored) {}

                    if(list.size() == 0) {
                        list.add("Unknown, " + args[2] + " is not a valid value");
                    }
                    //This is information and should not match to certain values
                    listWithOnlyMatching = list;
                    break;
            }

            return listWithOnlyMatching;
        }
        return null;
    }
}