package me.noaz.testplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteForResource implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("resource")) {
            if (sender instanceof Player) {
                List<String> list = new ArrayList<>();
                list.add("3D");
                list.add("2D");

                return list;
            }
        }

        return null;
    }
}
