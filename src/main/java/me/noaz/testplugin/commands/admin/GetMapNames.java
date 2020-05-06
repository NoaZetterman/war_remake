package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.Maps.GameMap;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.GameController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GetMapNames implements CommandExecutor {
    private TestPlugin plugin;
    GameController gameController;

    public GetMapNames(TestPlugin plugin, GameController gameController) {
        this.plugin = plugin;
        this.gameController = gameController;
        plugin.getCommand("maps").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<GameMap> maps = gameController.getMaps();
        for(GameMap map : maps) {
            sender.sendMessage(map.getName());
        }

        return true;
    }
}
