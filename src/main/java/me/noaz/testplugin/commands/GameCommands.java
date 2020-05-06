package me.noaz.testplugin.commands;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.GameController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor {
    private GameController gameController;

    public GameCommands(TestPlugin plugin, GameController gameController) {
        this.gameController = gameController;
        plugin.getCommand("game").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }

        if(args[0].toLowerCase().equals("join") || args[0].toLowerCase().equals("j")) {
            if(gameController.joinGame((Player)sender)) {
                sender.sendMessage("Joining game");
            } else {
                sender.sendMessage("Game has not yet started");
            }

        } else if(args[0].toLowerCase().equals("leave") || args[0].toLowerCase().equals("l")) {
            if(gameController.leaveGame((Player)sender)) {
                sender.sendMessage("Leaving game");
            } else {
                sender.sendMessage("No game to leave, do /war join to join game");
            }
        } else if(sender.hasPermission("op")) {
            if(args[0].toLowerCase().equals("start")) {
                gameController.startGame();
                sender.sendMessage("Starting game");
            } else if(args[0].toLowerCase().equals("end")) {
                gameController.endGame();
                sender.sendMessage("Ending game");
            }
        } else {
            sender.sendMessage("Invalid arguments, use /game join or /game leave");
        }

        return true;
    }
}
