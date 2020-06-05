package me.noaz.testplugin.commands;

import de.Herbystar.TTA.TTA_Methods;
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

        if(args[0].toLowerCase().equals("2d")) {
            player.setResourcePack(Resourcepack.PACK_2D_16X16.getUrl());
            sender.sendMessage("Applying 2D textures");
            data.getPlayerExtension(player).setSelectedResourcepack(Resourcepack.PACK_2D_16X16);
        } else if(args[0].toLowerCase().equals("3d")) {
            player.setResourcePack(Resourcepack.PACK_3D_128X128.getUrl());
            sender.sendMessage("Applying 3D textures");
            data.getPlayerExtension(player).setSelectedResourcepack(Resourcepack.PACK_3D_128X128);
        } else {
            sender.sendMessage("Invalid input");
        }

        return true;
    }
}