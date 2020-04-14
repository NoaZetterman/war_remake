package me.noaz.testplugin.Messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerListMessage {
    public static void setTeamDeathMatchHeader(Player player, int redTeamKills, int blueTeamKills) {

        String leadTeam = "Tie";

        if(redTeamKills > blueTeamKills) {
            leadTeam = "Red by " + (redTeamKills-blueTeamKills);
        } else if(redTeamKills < blueTeamKills) {
            leadTeam = "Blue by " + (blueTeamKills-redTeamKills);
        }

        //TODO: Add nice colors
        player.setPlayerListHeader("Team Death Match" + "\n" +
                "Kill the other teams players" + "\n" +
                "─────────────────────────────────" + "\n" +
                ChatColor.RED + "Red: " + ChatColor.RESET + redTeamKills + "    |    " +  ChatColor.BLUE + "Blue: " + ChatColor.RESET + blueTeamKills + "\n" +
                "Leader: " + leadTeam);
    }

    public static void setCaptureTheFlagHeader(Player player, int redCaptures, int blueCaptures) {

        String leadTeam = "Tie";

        if(redCaptures > blueCaptures) {
            leadTeam = "Red by " + (redCaptures-blueCaptures);
        } else if(redCaptures < blueCaptures) {
            leadTeam = "Blue by " + (blueCaptures-redCaptures);
        }

        //TODO: Add nice colors
        player.setPlayerListHeader("Capture the flag" + "\n" +
                "Capture the enemy flag to get points" + "\n" +
                "─────────────────────────────────" + "\n" +
                ChatColor.RED + "Red: " + ChatColor.RESET + redCaptures + "    |    " +  ChatColor.BLUE + "Blue: " + ChatColor.RESET + blueCaptures + "\n" +
                "Leader: " + leadTeam);
    }

    public static void setFreeForAllHeader(Player player, String leader, int kills) {

        //TODO: Add nice colors
        player.setPlayerListHeader("Free for all" + "\n" +
                "Kill anyone - First to ??? kills wins!" + "\n" +
                "─────────────────────────────────" + "\n" +
                "Leader: " + leader + " with " + kills);
    }

    public static void setLobbyHeader(Player player, String nextGamemode, String nextMap) {
        player.setPlayerListHeader("Next Game: " + nextGamemode + "\n" +
                "Next Map: " + nextMap + "\n" +
                "─────────────────────────────────" + "\n" +
                "Map creator: {name or alias of mapcreator} (YT/Twitter/whatever to map creator)");
    }

    public static void setFooter(Player player) {
        player.setPlayerListFooter("Some info");
    }
}
