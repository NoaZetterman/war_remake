package me.noaz.testplugin.commands;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.Resourcepack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Resource implements CommandExecutor {
    GameData data;
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
                player.setResourcePack(Resourcepack.PACK_2D_16X16.getUrl(), Resourcepack.PACK_2D_16X16.getSha1());
                sender.sendMessage("Applying 2D textures");
                data.getPlayerExtension(player).setSelectedResourcepack(Resourcepack.PACK_2D_16X16);
            } else if (args[0].toLowerCase().equals("3d")) {
                player.setResourcePack(Resourcepack.PACK_3D_128X128.getUrl(), Resourcepack.PACK_3D_128X128.getSha1());
                sender.sendMessage("Applying 3D textures");
                data.getPlayerExtension(player).setSelectedResourcepack(Resourcepack.PACK_3D_128X128);
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