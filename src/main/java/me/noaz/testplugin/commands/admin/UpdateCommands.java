package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.Maps.GameMap;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.tasks.GameController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpdateCommands implements CommandExecutor {
    GameController gameController;
    Connection connection;

    public UpdateCommands(TestPlugin plugin, GameController gameController,
                          Connection connection) {
        this.gameController = gameController;
        this.connection = connection;

        plugin.getCommand("update").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(args[0] != null && args[1] != null && args[2] != null) {
            switch(args[0].toLowerCase()) {
                case "map":
                case "maps":
                    if(gameController.getMapNames().contains(args[1])) {
                        try {
                            PreparedStatement updateMap;
                            switch (args[2].toLowerCase()) {
                                case "creator":
                                    updateMap = connection.prepareStatement("UPDATE test.map SET " +
                                            "creator=? WHERE name=?");
                                    break;
                                case "information":
                                case "creatorinformation":
                                    updateMap = connection.prepareStatement("UPDATE test.map SET " +
                                            "creator_information=? WHERE name=?");
                                    break;
                                default:
                                    sender.sendMessage("The parameters were wrong, use creator or information as last parameter");
                                    throw new IllegalStateException("Unexpected value: " + args[2].toLowerCase());
                            }

                            String message = "";
                            for(int i = 3; i < args.length-1; i++) {
                                message += args[i] + " ";
                            }

                            message += args[args.length-1];

                            updateMap.setString(1, message);
                            updateMap.setString(2, args[1]);
                            updateMap.execute();

                        } catch(SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        } else {
            sender.sendMessage("Not enough parameters use: [map/gun] [map/gun name] [set<variable>] [new value]");
            sender.sendMessage("For more information type /help update");
        }


        return true;
    }
}
