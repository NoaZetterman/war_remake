package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.GameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteForUpdateCommands implements TabCompleter {
    GameData data;

    public TabCompleteForUpdateCommands(GameData data) {
        this.data = data;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("update")){
            if(sender instanceof Player){
                List<String> list = new ArrayList<>();
                switch(args.length) {
                    case 1:
                        list.add("gun");
                        list.add("map");
                        break;
                    case 2:
                        if(args[0].toLowerCase().equals("map")) {
                            list = data.getMapNames();
                        } else {
                            list = data.getGunNames();
                        }
                        break;
                    case 3:
                        if(args[0].toLowerCase().equals("map")) {
                            list.add("creator");
                        } else {
                            list.add("notimplemented");
                        }
                        break;
                }

                return list;
            }
        }
        return null;
    }
}