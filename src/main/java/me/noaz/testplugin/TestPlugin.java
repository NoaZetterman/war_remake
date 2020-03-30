package me.noaz.testplugin;

import me.noaz.testplugin.commands.Command;
import me.noaz.testplugin.events.DamageEvents;
import me.noaz.testplugin.events.Events;
import me.noaz.testplugin.events.LogInOutEvents;
import me.noaz.testplugin.tasks.GameController;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestPlugin extends JavaPlugin {
    private Connection connection;
    private String host, database, username, password;
    private int port;

    private GameController gameController;

    @Override
    public void onEnable() {
        host = "localhost";
        port = 3306;
        database = "MYSQL";
        username = "root";
        password = "38r947grunoi8/&Fg8hnuby8v";
        Statement statement = null;

        try {
            openConnection();
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        this.saveDefaultConfig();

        gameController = new GameController(this);
        ScoreManager scoreManager = new ScoreManager(this);
        getServer().setDefaultGameMode(GameMode.ADVENTURE);
        getServer().getWorld("world").setPVP(false);

        getServer().getPluginManager().registerEvents(new LogInOutEvents(this, gameController, scoreManager, statement), this);
        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getPluginManager().registerEvents(new DamageEvents(gameController, this), this);

        new Command(this, gameController);
    }

    @Override
    public void onDisable() {
        gameController.stop();
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
