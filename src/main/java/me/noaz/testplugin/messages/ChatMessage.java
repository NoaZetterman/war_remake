package me.noaz.testplugin.messages;

import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * A class containing static methods to print messages to one player in the chat.
 *
 * @author Noa Zetterman
 * @version 2020-04-09
 */
public class ChatMessage {

    public static void outOfAmmo(PlayerExtension player) {
        player.getPlayer().sendMessage("Out of ammo!");
    }

    public static void playerWasShotToDeath(Player killedPlayer, Player shooter, ChatColor shooterColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Shot by " +
                shooterColor + shooter.getName());
    }

    public static void playerShotKilled(Player shooter, int xp, int credits, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        String message;
        if(gamemode == Gamemode.TEAM_DEATHMATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += ChatColor.GRAY + "Shot " + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";
        shooter.sendMessage(message);
    }

    public static void playerWasHeadshotToDeath(Player killedPlayer, Player shooter, ChatColor shooterColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Headshot by " +
                shooterColor + shooter.getName());
    }

    public static void playerHeadshotKilled(Player shooter, int xp, int credits, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        String message;
        if(gamemode == Gamemode.TEAM_DEATHMATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += ChatColor.GRAY + "Headshot " + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";
        shooter.sendMessage(message);
    }

    public static void playerWasKnifedToDeath(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Knifed by " +
                killerColor + killer.getName());
    }

    public static void playerKnifeKilled(Player killer, int xp, int credits, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        String message;
        if(gamemode == Gamemode.TEAM_DEATHMATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += ChatColor.GRAY + "Knifed " + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";
        killer.sendMessage(message);
    }

    public static void playerWasInfectedDeath(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Infected by " + killerColor + killer.getName());
    }
    public static void playerInfectedKill(Player killer, int xp, int credits, Player killedPlayer, ChatColor killedColor) {
        killer.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY + "Infected "
                + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$");
    }

    public static void playerWasNukeKilled(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[+] " + ChatColor.GRAY + "Nuked by " +
                killerColor + killer.getName());
    }

    public static void playerNukeKilled(Player killer, int xp, int credits, Player killedPlayer, ChatColor killedColor) {
        killer.sendMessage(ChatColor.GRAY + "[+] " + ChatColor.GRAY + "Nuked " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$");
    }

    public static void playerCapturedFlag(Player playerWhoCaptured, int xp, int credits, ChatColor flagColor) {
        if(flagColor == ChatColor.BLUE) {

        }
        playerWhoCaptured.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY +
                "Captured the " + flagColor + "red/blue" + ChatColor.GRAY + " flag" + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$");
    }


    public static void displayFreeForAllEndGame(String winner, int winnerKills, Player player) {
        player.sendMessage(ChatColor.GOLD + "Winner: " + ChatColor.LIGHT_PURPLE + winner + ChatColor.GRAY
                + " (" + ChatColor.LIGHT_PURPLE + winnerKills + ChatColor.GRAY + ")");
    }

    public static void displayTeamDeathmatchEndGame(String winner, Team winnerTeam, Team loserTeam, Player player) {
        if(winner.equals("None")) {
            player.sendMessage(ChatColor.GOLD + "Winner: " + ChatColor.GRAY + winner + ChatColor.GRAY +
                    " (" + winnerTeam.getTeamColorAsChatColor() + loserTeam.getKills() + ChatColor.GRAY + " - " +
                    loserTeam.getTeamColorAsChatColor() + winnerTeam.getKills() + ChatColor.GRAY + ")");
        } else {
            player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + ChatColor.GRAY +
                    " (" + winnerTeam.getTeamColorAsChatColor() + winnerTeam.getKills() + ChatColor.GRAY + " - " +
                    loserTeam.getTeamColorAsChatColor() + loserTeam.getKills() + ChatColor.GRAY + ")");
        }
    }

    public static void displayCaptureTheFlagEndGame(String winner, Team winnerTeam, Team loserTeam, Player player) {
        if(winner.equals("None")) {
            player.sendMessage(ChatColor.GOLD + "Winner: " + ChatColor.GRAY + winner + ChatColor.GRAY +
                    " (" + winnerTeam.getTeamColorAsChatColor() + loserTeam.getCaptures() + ChatColor.GRAY + " - " +
                    loserTeam.getTeamColorAsChatColor() + winnerTeam.getCaptures() + ChatColor.GRAY + ")");
        } else {
            player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + ChatColor.GRAY +
                    " (" + winnerTeam.getTeamColorAsChatColor() + loserTeam.getCaptures() + ChatColor.GRAY + " - " +
                    loserTeam.getTeamColorAsChatColor() + winnerTeam.getCaptures() + ChatColor.GRAY + ")");
        }
    }

    public static void displayInfectEndGame(String winner, Team winnerTeam, Player player) {
        player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + ChatColor.GOLD + " won!");
    }

    public static void displayStatisticsAtEndOfGame(Gamemode gamemode, String winner, Team winnerTeam, Team loserTeam,
            Player player, int kills, int deaths, int totalKills, int totalDeaths,int xpGained, int creditsGained) {

        switch(gamemode) {
            case FREE_FOR_ALL:
                player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + ChatColor.GRAY
                        + " (" + ChatColor.LIGHT_PURPLE + "" + ChatColor.GRAY + ")\n"
                );
                break;
            case INFECT:
                player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + " won");
                break;
            case TEAM_DEATHMATCH:
                player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + ChatColor.GRAY +
                        " (" + winnerTeam.getTeamColorAsChatColor() + winnerTeam.getKills() + ChatColor.GRAY + " - " +
                        loserTeam.getTeamColorAsChatColor() + loserTeam.getKills() + ChatColor.GRAY + ")");
                break;
            case CAPTURE_THE_FLAG:
                player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + ChatColor.GRAY +
                        " (" + winnerTeam.getTeamColorAsChatColor() + winnerTeam.getCaptures() + ChatColor.GRAY + " - " +
                        loserTeam.getTeamColorAsChatColor() + loserTeam.getCaptures() + ChatColor.GRAY + ")");
                break;

        }

        displayPersonalStats(player,kills,deaths,totalKills,totalDeaths,xpGained,creditsGained);
    }

    public static void displayPersonalStats(Player player, int kills, int deaths, int totalKills, int totalDeaths,int xpGained, int creditsGained) {

        player.sendMessage(ChatColor.GOLD + "Kills: " + ChatColor.GREEN +  kills + ChatColor.GOLD + "   Deaths: " + ChatColor.RED + deaths + ChatColor.GOLD +
                "   K/D Ratio: " + TextUtils.getRatioAsRedOrGreenString(kills, deaths, totalKills, totalDeaths) + "\n" + ChatColor.RESET +
                ChatColor.GOLD + "Earned xp: " + ChatColor.GREEN + xpGained + ChatColor.GOLD + "   Earned credits: " + ChatColor.GREEN + creditsGained
        );

        /*Maybe
        player.sendMessage(
                "----------------------------------------------------------------" + "\n" +
                        ChatColor.DARK_AQUA + "Kills: " + ChatColor.GREEN +  kills + ChatColor.DARK_AQUA + "   Deaths: " + ChatColor.RED + deaths + ChatColor.DARK_AQUA +
                        "   K/D Ratio: " + getRatioAsString(kills, deaths, totalKills, totalDeaths) + "\n" + ChatColor.RESET +
                        ChatColor.DARK_AQUA + "Earned xp: " + ChatColor.GREEN + xpGained + ChatColor.DARK_AQUA + "   Earned credits: " + ChatColor.GREEN + creditsGained
        );*/

        player.sendMessage("Colors:" + ChatColor.DARK_GRAY + "DARKGRAY" + ChatColor.GRAY + "GRAY" + ChatColor.GOLD + "GOLD" +
                ChatColor.YELLOW + "YELLOW" + ChatColor.DARK_BLUE + "DARKBLUE" + ChatColor.BLACK + "BLACK" + ChatColor.GREEN +
                "GREEN" + ChatColor.LIGHT_PURPLE + "LIGHTPURPLE" + ChatColor.DARK_RED + "DARKRED" + ChatColor.DARK_GREEN + "DARKGREEN" +
                ChatColor.AQUA + "AQUA" + ChatColor.RED + "RED" + ChatColor.DARK_AQUA + "DARKAQUA" + ChatColor.WHITE + "WHITE" +
                ChatColor.BLUE + "BLUE" + ChatColor.DARK_PURPLE +"DARKPURPLE" + ChatColor.COLOR_CHAR + "ColorChar?");

    }

    private static String getKdrDifference(double kills, double deaths, double totalkills, double totalDeaths) {
        double ratioBefore = getRatio(totalkills, totalDeaths);
        double ratioAfter = getRatio(totalkills+kills, totalDeaths+deaths);

        double difference = ratioAfter-ratioBefore;

        String differenceString;
        if(difference < 0) {
            differenceString = ChatColor.RED + "-";
            difference = Math.abs(difference);
        } else {
            differenceString = ChatColor.GREEN + "";
        }

        if(kills+deaths > 100000) {
            differenceString += getNDecimalPlaces(8,difference);
        } else if(kills+deaths > 10000) {
            differenceString += getNDecimalPlaces(7,difference);
        } else if(kills+deaths > 1000) {
            differenceString += getNDecimalPlaces(6,difference);
        } else if(kills+deaths > 100) {
            differenceString += getNDecimalPlaces(5,difference);
        } else if(kills+deaths > 10) {
            differenceString += getNDecimalPlaces(4,difference);
        } else {
            differenceString += getNDecimalPlaces(3,difference);
        }

        System.out.println(differenceString);
        return differenceString;
    }

    private static String getNDecimalPlaces(int n, double number) {
        return String.valueOf(Math.round(number*Math.pow(10,n))/Math.pow(10,n));
    }

    private static double getRatio(double numerator, double denominator) {
        return (denominator == 0) ? numerator : numerator/denominator;
    }
}
