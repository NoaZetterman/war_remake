package me.noaz.testplugin.events;

import me.noaz.testplugin.AccessDatabase;
import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.tasks.GameController;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Listens to player events where player logs in and quits.
 * This class does necessary setup and shutdown actions such as saving stats when a player leaves
 *
 * @author Noa Zetterman
 * @version 2019-12-10
 */
public class LogInOutEvents implements Listener {
    private TestPlugin plugin;
    private GameController gameController;
    private ScoreManager scoreManager;
    private Statement statement;

    public LogInOutEvents(TestPlugin plugin, GameController gameController, ScoreManager scoreManager, Statement statement) {
        this.plugin = plugin;
        this.gameController = gameController;
        this.scoreManager = scoreManager;
        this.statement = statement;
    }

    /**
     * Lets the player join the server correctly
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String createPlayerIfNotExist = "INSERT INTO test.player (player_uuid) " +
                "SELECT \"" + event.getPlayer().getUniqueId() + "\"" +
                " WHERE NOT EXISTS (SELECT * FROM test.player WHERE player_uuid = \"" + event.getPlayer().getUniqueId() + "\");";

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    AccessDatabase.execute(statement, createPlayerIfNotExist);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        runnable.runTaskAsynchronously(plugin);

        gameController.addPlayer(plugin, event.getPlayer(), scoreManager, statement);
        plugin.getServer().getBossBar(NamespacedKey.minecraft("timer")).addPlayer(event.getPlayer());
    }

    /**
     * Makes the player leave the server correctly.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        //TODO: Put row below into player extension
        plugin.getServer().getBossBar(NamespacedKey.minecraft("timer")).removePlayer(event.getPlayer());
        event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
        gameController.removePlayer(event.getPlayer());
    }
}
