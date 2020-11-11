package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.Buyable;
import me.noaz.testplugin.GameData;
import me.noaz.testplugin.dao.GunDao;
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
                list.add("map");
                StringUtil.copyPartialMatches(args[0], list, listWithOnlyMatching);
                break;
            case 2:
                if(args[0].toLowerCase().equals("map")) {
                    list = data.getMapNames();
                }
                StringUtil.copyPartialMatches(args[1], list, listWithOnlyMatching);
                break;
        }
        return listWithOnlyMatching;
    }
        return null;
    }

}