package me.noaz.testplugin.commands;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.tasks.GameController;

/**
 * Initialize all commands
 */
public class Command {
    public Command(TestPlugin plugin, GameController gameController) {
        new ChooseGameAndGamemode(plugin, gameController);
        new GameCommands(plugin, gameController);
        new GetMapNames(plugin, gameController);
        new Profile(plugin);
        new Ping(plugin);
    }
}
