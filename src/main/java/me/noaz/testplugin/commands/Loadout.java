package me.noaz.testplugin.commands;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.inventories.LoadoutMenu;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Loadout  implements CommandExecutor {
    private final GameData gameData;

    public Loadout(TestPlugin plugin, GameData gameData) {
        this.gameData = gameData;
        plugin.getCommand("loadout").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }

        PlayerExtension playerExtension = gameData.getPlayerExtension((Player) sender);

        LoadoutMenu.loadoutStartScreen(playerExtension);

        return true;
    }
}