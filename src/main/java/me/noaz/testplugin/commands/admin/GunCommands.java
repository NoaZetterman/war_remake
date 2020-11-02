package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.TestPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GunCommands implements CommandExecutor {
    private final GameData data;

    public GunCommands(TestPlugin plugin, GameData data) {
        this.data = data;

        plugin.getCommand("gun").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args[0] != null && args[1] != null) {
            StringBuilder gunNameAsStringBuilder = new StringBuilder();
            for(int i = 1; i < args.length-1; i++) {
                gunNameAsStringBuilder.append(args[i]).append(" ");
            }

            gunNameAsStringBuilder.append(args[args.length - 1]);
            String gunName = gunNameAsStringBuilder.toString();
            switch (args[0].toLowerCase()) {
                case "save":
                    data.saveGunConfiguration(gunName);
                    break;
                case "add":
                    data.createNewGunConfiguration(gunName);
                    break;
                case "delete":
                    if(data.deleteGunConfiguration(gunName)) {
                        sender.sendMessage("The gun " + gunName + " was deleted from the database " +
                                "but will still be accessible in game until restart");
                    } else {
                        sender.sendMessage("Failed, maybe the gun is already deleted? ");
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }

        return false;
    }
}
