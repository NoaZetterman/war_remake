package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.GameController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChooseGameAndGamemode implements CommandExecutor {
    private GameController gameController;

    public ChooseGameAndGamemode(TestPlugin plugin, GameController gameController) {
        this.gameController = gameController;
        plugin.getCommand("cg").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 2 && gameController.pickNextMapAndGamemode(args[0], args[1])) {
            sender.sendMessage("Changed map to " + args[0] + " " + args[1]);
        } else {
            sender.sendMessage("Invalid arguments, remember to use correct CaSiNg");
        }

        return true;
    }
}
