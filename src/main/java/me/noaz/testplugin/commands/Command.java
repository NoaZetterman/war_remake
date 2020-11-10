package me.noaz.testplugin.commands;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.GameLoop;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.commands.admin.*;

import java.sql.Connection;

/**
 * Initialize all commands and tab completers
 */
public class Command {
    public Command(TestPlugin plugin, GameLoop gameLoop, GameData data, Connection connection) {
        new ChooseGameAndGamemode(plugin, gameLoop);
        new GameCommands(plugin, gameLoop);
        new GetMapNames(plugin, data);
        new UpdateCommands(plugin, data, connection);
        new GunCommands(plugin, data);
        new Resource(plugin, data);
        new Loadout(plugin, data);

        plugin.getServer().getPluginCommand("update").setTabCompleter(new TabCompleteForUpdateCommands(data));
        plugin.getServer().getPluginCommand("gun").setTabCompleter(new TabCompleteForGunCommands(data));
        plugin.getServer().getPluginCommand("resource").setTabCompleter(new TabCompleteForResource());

        new Profile(plugin);
        new Ping(plugin);
    }
}
