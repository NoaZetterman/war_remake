package me.noaz.testplugin.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteForGunCommands implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("gun")) {
            if (sender instanceof Player) {
                switch(args.length) {
                    case 1:
                    List<String> list = new ArrayList<>();
                    List<String> listWithOnlyMatching = new ArrayList<>();
                    list.add("save");
                    list.add("add");
                    list.add("delete");

                    StringUtil.copyPartialMatches(args[0], list, listWithOnlyMatching);
                    return listWithOnlyMatching;
                }
            }
        }

        return null;
    }
}
