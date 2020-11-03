package me.noaz.testplugin.commands.admin;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.dao.GunDao;
import me.noaz.testplugin.weapons.guns.GunConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateCommands implements CommandExecutor {
    private final GameData data;
    private final Connection connection;

    public UpdateCommands(TestPlugin plugin, GameData data,
                          Connection connection) {
        this.data = data;
        this.connection = connection;

        plugin.getCommand("update").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(args[0] != null && args[1] != null && args[2] != null) {
            switch(args[0].toLowerCase()) {
                case "map":
                    if(data.getMapNames().contains(args[1])) {
                        //TODO: Redo this with dao
                        try {
                            PreparedStatement updateMap;
                            updateMap = connection.prepareStatement("UPDATE map SET " +
                                    "creator=? WHERE name=?");

                            StringBuilder message = new StringBuilder();
                            for(int i = 2; i < args.length-1; i++) {
                                message.append(args[i]).append(" ");
                            }

                            message.append(args[args.length - 1]);

                            updateMap.setString(1, message.toString());
                            updateMap.setString(2, args[1]);
                            updateMap.execute();
                            updateMap.closeOnCompletion();

                        } catch(SQLException e) {
                            e.printStackTrace();
                            sender.sendMessage("Failed to set creator");
                        }
                    }
                    break;
                case "gun":
                    if(args[3] == null) {
                        return false;
                    }

                    String gunName = args[1];
                    String field =  args[2];

                    //Support spaces, for display names
                    StringBuilder val = new StringBuilder();
                    for(int i = 3; i < args.length-1; i++) {
                        val.append(args[i]).append(" ");
                    }

                    val.append(args[args.length - 1]);
                    String value = val.toString();

                    try {
                        data.updateGunConfiguration(gunName, field, value);
                        sender.sendMessage("Set " + field + " to: " + value + " for gun " + gunName);
                    } catch(NumberFormatException e) {
                        sender.sendMessage("Invalid data format, failed to set value");
                    }
            }
        } else {
            //Useless comments?
            sender.sendMessage("Not enough parameters use: [map/gun] [map/gun name] [set<variable>] [new value]");
            sender.sendMessage("For more information type /help update");
            return false;
        }

        return true;

    }
}