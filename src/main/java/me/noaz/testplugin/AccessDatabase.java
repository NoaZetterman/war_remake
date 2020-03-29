package me.noaz.testplugin;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccessDatabase {

    public static ResultSet getQueryResult(Statement statement, String sqlQuestion) throws SQLException {
        return statement.executeQuery(sqlQuestion);
    }

    public static void update(Statement statement, String sqlQuestion) throws SQLException {
        statement.executeUpdate(sqlQuestion);

    }

    public static void execute(Statement statement, String sqlQuestion) throws SQLException {
        statement.execute(sqlQuestion);
    }


    /*
    String createPlayerIfNotExist = "INSERT INTO test.player (player_uuid) " +
                "SELECT \"" + event.getPlayer().getUniqueId() + "\"" +
                " WHERE NOT EXISTS (SELECT * FROM test.player WHERE player_uuid = \"" + event.getPlayer().getUniqueId() + "\");";
        String getPlayerData = "SELECT * FROM test.player WHERE player_uuid=\"" + event.getPlayer().getUniqueId() + "\";";

        statement.execute(createPlayerIfNotExist);
        System.out.println("Created player");

        ResultSet result = statement.executeQuery(getPlayerData);

        int kills = 100;
        while(result.next()) {
            kills = result.getInt("kills");
        }
        System.out.println(kills + " Player has kills");
     */
}
