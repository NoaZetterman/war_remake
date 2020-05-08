package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.TestPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GetMapNames implements CommandExecutor {
    GameData data;

    public GetMapNames(TestPlugin plugin, GameData data) {
        this.data = data;
        plugin.getCommand("maps").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<GameMap> maps = data.getMaps();
        for(GameMap map : maps) {
            sender.sendMessage(map.getName());
        }

        return true;
    }
}
