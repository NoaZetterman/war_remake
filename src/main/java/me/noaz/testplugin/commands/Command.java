package me.noaz.testplugin.commands;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.commands.admin.ChooseGameAndGamemode;
import me.noaz.testplugin.commands.admin.GetMapNames;
import me.noaz.testplugin.commands.admin.TabCompleteForUpdateCommands;
import me.noaz.testplugin.commands.admin.UpdateCommands;
import me.noaz.testplugin.GameController;

import java.sql.Connection;

/**
 * Initialize all commands
 */
public class Command {
    public Command(TestPlugin plugin, GameController gameController, Connection connection) {
        new ChooseGameAndGamemode(plugin, gameController);
        new GameCommands(plugin, gameController);
        new GetMapNames(plugin, gameController);
        new UpdateCommands(plugin, gameController, connection);

        plugin.getServer().getPluginCommand("update").setTabCompleter(new TabCompleteForUpdateCommands(gameController));

        new Profile(plugin);
        new Ping(plugin);
    }
}
