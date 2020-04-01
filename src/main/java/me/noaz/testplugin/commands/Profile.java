package me.noaz.testplugin.commands;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Profile implements CommandExecutor {
    private TestPlugin plugin;

    public Profile(TestPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("profile").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        } else if(args == null || args.length > 1) {
            sender.sendMessage("Usage: /profile [playername]");
        }


        //TODO: Fix below better with new PlayerExtension
        List<Player> players = plugin.getServer().getBossBar(NamespacedKey.minecraft("timer")).getPlayers();
        for(Player player : players) {
            if(player.getName().equals(args[0])) {
                sender.sendMessage("Currently disabled");
                //((PlayerExtension) player.getMetadata("handler").get(0).value()).getPlayerStatistics().printStatistics((Player)sender);
            }
        }

        return true;
    }
}