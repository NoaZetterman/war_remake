package me.noaz.testplugin.commands;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.Resourcepack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Resource implements CommandExecutor {
    private final GameData data;

    public Resource(TestPlugin plugin, GameData data) {
        this.data = data;
        plugin.getCommand("resource").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }
        Player player = (Player) sender;

        if(args.length >= 1) {
            if (args[0].toLowerCase().equals("2d")) {
                data.getPlayerExtension(player).setSelectedResourcepack(Resourcepack.PACK_2D_16X16);
                sender.sendMessage("Applying 2D textures");
            } else if (args[0].toLowerCase().equals("3d")) {
                data.getPlayerExtension(player).setSelectedResourcepack(Resourcepack.PACK_3D_DEFAULT);
                sender.sendMessage("Applying 3D textures");
            } else {
                sender.sendMessage("Invalid input");
                return false;
            }

            return true;
        } else {
            sender.sendMessage("Invalid input put 2d or 3d");
            return false;
        }

    }
}