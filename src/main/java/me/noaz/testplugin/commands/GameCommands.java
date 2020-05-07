package me.noaz.testplugin.commands;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.GameLoop;
import me.noaz.testplugin.TestPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor {
    private GameLoop gameLoop;

    public GameCommands(TestPlugin plugin, GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        plugin.getCommand("game").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }

        if(args[0].toLowerCase().equals("join") || args[0].toLowerCase().equals("j")) {
            if(gameLoop.joinGame((Player)sender)) {
                sender.sendMessage("Joining game");
            } else {
                sender.sendMessage("Game has not yet started");
            }

        } else if(args[0].toLowerCase().equals("leave") || args[0].toLowerCase().equals("l")) {
            if(gameLoop.leaveGame((Player)sender)) {
                sender.sendMessage("Leaving game");
            } else {
                sender.sendMessage("No game to leave, do /war join to join game");
            }
        } else if(sender.hasPermission("op")) {
            if(args[0].toLowerCase().equals("start")) {
                gameLoop.startGame();
                sender.sendMessage("Starting game");
            } else if(args[0].toLowerCase().equals("end")) {
                gameLoop.endGame();
                sender.sendMessage("Ending game");
            }
        } else {
            sender.sendMessage("Invalid arguments, use /game join or /game leave");
        }

        return true;
    }
}
