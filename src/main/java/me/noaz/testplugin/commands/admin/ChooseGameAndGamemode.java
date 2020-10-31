package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.GameLoop;
import me.noaz.testplugin.TestPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChooseGameAndGamemode implements CommandExecutor {
    private final GameLoop gameLoop;

    public ChooseGameAndGamemode(TestPlugin plugin, GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        plugin.getCommand("cg").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 2 && gameLoop.pickNextGame(args[0], args[1])) {
            sender.sendMessage("Changed map to " + args[0] + " " + args[1]);
        } else {
            sender.sendMessage("Invalid arguments, remember to use correct casing");
        }

        return true;
    }
}
