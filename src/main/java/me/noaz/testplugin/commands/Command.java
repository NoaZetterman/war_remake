package me.noaz.testplugin.commands;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.GameLoop;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.commands.admin.ChooseGameAndGamemode;
import me.noaz.testplugin.commands.admin.GetMapNames;
import me.noaz.testplugin.commands.admin.TabCompleteForUpdateCommands;
import me.noaz.testplugin.commands.admin.UpdateCommands;

import java.sql.Connection;

/**
 * Initialize all commands
 */
public class Command {
    public Command(TestPlugin plugin, GameLoop gameLoop, GameData data, Connection connection) {
        new ChooseGameAndGamemode(plugin, gameLoop);
        new GameCommands(plugin, gameLoop);
        new GetMapNames(plugin, data);
        new UpdateCommands(plugin, data, connection);
        new Resource(plugin, data);

        plugin.getServer().getPluginCommand("update").setTabCompleter(new TabCompleteForUpdateCommands(data));
        plugin.getServer().getPluginCommand("resource").setTabCompleter(new TabCompleteForResource());

        new Profile(plugin);
        new Ping(plugin);
    }
}
