package me.noaz.testplugin;

import me.noaz.testplugin.commands.Command;
import me.noaz.testplugin.dao.GameMapDao;
import me.noaz.testplugin.dao.GunDao;
import me.noaz.testplugin.dao.PlayerDao;
import me.noaz.testplugin.events.DamageEvents;
import me.noaz.testplugin.events.Events;
import me.noaz.testplugin.events.LogInOutEvents;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class TestPlugin extends JavaPlugin {
    private Connection connection;
    private String host, database, username, password;
    private int port;

    private GameLoop gameLoop;

    @Override
    public void onEnable() {
        FileConfiguration databaseConfiguration = this.getConfig();
        host = databaseConfiguration.getString("hostIP");
        port = databaseConfiguration.getInt("port");
        database = databaseConfiguration.getString("database");
        username = databaseConfiguration.getString("username");
        password = databaseConfiguration.getString("password");

        try {
            openConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        this.saveDefaultConfig();

        //TODO: Fix so that next game shows right after startup
        new PlayerDao(connection);
        new GameMapDao(connection);
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
    }

    @Override
    public void onDisable() {
        gameLoop.stop();
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
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database + "?verifyServerCertificate=false&useSSL=true"
                    , this.username, this.password);
        }
    }
}
