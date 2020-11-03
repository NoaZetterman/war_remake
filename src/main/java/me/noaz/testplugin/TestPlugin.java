package me.noaz.testplugin;

import me.noaz.testplugin.commands.Command;
import me.noaz.testplugin.dao.GameMapDao;
import me.noaz.testplugin.dao.GunDao;
import me.noaz.testplugin.dao.PlayerDao;
import me.noaz.testplugin.events.DamageEvents;
import me.noaz.testplugin.events.Events;
import me.noaz.testplugin.events.LogInOutEvents;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class TestPlugin extends JavaPlugin {
    private Connection connection;
    private String host, databaseName, username, password;
    private int port;

    private GameLoop gameLoop;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        FileConfiguration fileConfiguration = this.getConfig();
        ConfigurationSection database = fileConfiguration.getConfigurationSection("database");
        if(database != null) {
            host = database.getString("hostIP");
            port = database.getInt("port");
            databaseName = database.getString("name");
            username = database.getString("username");
            password = database.getString("password");

            try {
                openConnection();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }


            //TODO: Fix so that next game shows right after startup
            new PlayerDao(connection);
            ConfigurationSection mapPaths = fileConfiguration.getConfigurationSection("mapPaths");
            if(mapPaths == null) {
                getLogger().severe("Found no map paths, configure in config.yml. Disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(this);
            } else {
                new GameMapDao(connection, mapPaths);
            }
            new GunDao(connection);

            GameData data = new GameData(this);

            gameLoop = new GameLoop(data, this);

            ScoreManager scoreManager = new ScoreManager(this);

            getServer().setDefaultGameMode(GameMode.ADVENTURE);
            getServer().getWorld("world").setPVP(false);

            getServer().getPluginManager().registerEvents(new LogInOutEvents(this, data, scoreManager, connection), this);
            getServer().getPluginManager().registerEvents(new Events(data), this);
            getServer().getPluginManager().registerEvents(new DamageEvents(data, gameLoop), this);

            new Command(this, gameLoop, data, connection);
        } else {
            getLogger().severe("Found no database, configure in config.yml. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if(gameLoop != null) {
            gameLoop.stop();
        }
    }

    /**
     * Open connection to sql server
     */
    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.databaseName + "?verifyServerCertificate=false&useSSL=true"
                    , this.username, this.password);
        }
    }
}
