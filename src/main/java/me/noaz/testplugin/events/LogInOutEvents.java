package me.noaz.testplugin.events;

import me.noaz.testplugin.GameData;
import me.noaz.testplugin.ScoreManager;
import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.dao.PlayerDao;
import me.noaz.testplugin.messages.PlayerListMessage;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Listens to player events where player logs in and quits.
 * This class does necessary setup and shutdown actions such as saving stats when a player leaves
 *
 * @author Noa Zetterman
 * @version 2019-12-10
 */
public class LogInOutEvents implements Listener {
    private TestPlugin plugin;
    private GameData data;
    private ScoreManager scoreManager;
    private Connection connection;

    public LogInOutEvents(TestPlugin plugin, GameData data, ScoreManager scoreManager, Connection connection) {
        this.plugin = plugin;
        this.data = data;
        this.scoreManager = scoreManager;
        this.connection = connection;
    }

    /**
     * Lets the player join the server correctly
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Temp
        PlayerListMessage.setFooter(event.getPlayer());

        event.getPlayer().setGameMode(GameMode.ADVENTURE);

        data.addPlayer(plugin, event.getPlayer(), scoreManager);
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

        for(PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
            event.getPlayer().removePotionEffect(effect.getType());
        }

        data.removePlayer(event.getPlayer());
    }
}
