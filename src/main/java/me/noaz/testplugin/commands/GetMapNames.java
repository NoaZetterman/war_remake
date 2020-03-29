package me.noaz.testplugin.commands;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.tasks.GameController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
        String[] maps = gameController.getMaps();
        sender.sendMessage(maps);

        return true;
    }
}
