package me.noaz.testplugin.commands;

import me.noaz.testplugin.TestPlugin;
import de.Herbystar.TTA.TTA_Methods;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ping implements CommandExecutor {
    public Ping(TestPlugin plugin) {
        plugin.getCommand("ping").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            sender.sendMessage("Ping: " + TTA_Methods.getPing((Player) sender));
            return true;
        }

        sender.sendMessage("Only players can execute this command");
        return false;
    }
}
