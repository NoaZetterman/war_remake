package me.noaz.testplugin.messages;

import me.noaz.testplugin.maps.Gamemode;
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
        if(leader == null) {
            player.setPlayerListHeader("Free for all" + "\n" +
                    "Kill anyone - First to 25 kills wins!" + "\n" +
                    "─────────────────────────────────" + "\n" +
                    "Leader: -");
        } else {
            player.setPlayerListHeader("Free for all" + "\n" +
                    "Kill anyone - First to 25 kills wins!" + "\n" +
                    "─────────────────────────────────" + "\n" +
                    "Leader: " + leader + " with " + kills);
        }
    }

    public static void setInfectHeader(Player player, int survivors) {
        player.setPlayerListHeader("Infection" + "\n" +
                "Zombies vs Humans - Stay alive" + "\n" +
                "─────────────────────────────────" + "\n" +
                "Survivors left: " + survivors);

    }

    /**
     * @param player The player to set the header on
     * @param nextGamemode The (short) name of the next gamemode
     * @param nextMap Name of the next map
     * @param mapCreator The name of the map creator/creators
     */
    public static void setLobbyHeader(Player player, Gamemode nextGamemode, String nextMap, String mapCreator) {
        player.setPlayerListHeader("Next Game: " + nextGamemode.getGamemodeString() + "\n" +
                "Next Map: " + nextMap + "\n" +
                "Map by: " + mapCreator);
    }

    public static void setFooter(Player player) {
        player.setPlayerListFooter("Some info");
    }
}
