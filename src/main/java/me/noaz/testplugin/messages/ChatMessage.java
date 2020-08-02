package me.noaz.testplugin.messages;

import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.maps.Gamemode;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.Reward;
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

    //Kill messages

    public static void playerShotKilled(Player shooter, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        int xp = Reward.BODYSHOT_KILL.getXp();
        int credits = Reward.BODYSHOT_KILL.getCredits();

        String message;
        if(gamemode == Gamemode.TEAM_DEATHMATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += ChatColor.GRAY + "Shot " + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";
        shooter.sendMessage(message);
    }

    public static void playerHeadshotKilled(Player shooter, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        int xp = Reward.HEADSHOT_KILL.getXp();
        int credits = Reward.HEADSHOT_KILL.getCredits();

        String message;
        if(gamemode == Gamemode.TEAM_DEATHMATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += ChatColor.GRAY + "Headshot " + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";
        shooter.sendMessage(message);
    }

    public static void playerKnifeKilled(Player killer, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        int xp = Reward.KNIFE_KILL.getXp();
        int credits = Reward.KNIFE_KILL.getCredits();

        String message;
        if(gamemode == Gamemode.TEAM_DEATHMATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += ChatColor.GRAY + "Knifed " + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";
        killer.sendMessage(message);
    }

    public static void playerInfectedKilled(Player killer, Player killedPlayer, ChatColor killedColor) {
        int xp = Reward.ZOMBIE_KILL_HUMAN.getXp();
        int credits = Reward.ZOMBIE_KILL_HUMAN.getCredits();

        killer.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY + "Infected "
                + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$");
    }

    public static void playerNukeKilled(Player killer, Player killedPlayer, ChatColor killedColor) {
        int xp = Reward.NUKE_KILL.getXp();
        int credits = Reward.NUKE_KILL.getCredits();

        killer.sendMessage(ChatColor.GRAY + "[+] " + ChatColor.GRAY + "Nuked " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$");
    }

    public static void playerGrenadeKilled(Player killer, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        int xp = Reward.GRENADE_KILL.getXp();
        int credits = Reward.GRENADE_KILL.getCredits();

        String message;
        if(gamemode == Gamemode.TEAM_DEATHMATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += "Grenaded " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";

        killer.sendMessage(message);
    }

    public static void playerMolotovKilled(Player killer, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        int xp = Reward.MOLOTOV_KILL.getXp();
        int credits = Reward.MOLOTOV_KILL.getCredits();

        String message;
        if(gamemode == Gamemode.TEAM_DEATHMATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += "Burned " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";

        killer.sendMessage(message);
    }



    //Death messages

    public static void playerWasShotToDeath(Player killedPlayer, Player shooter, ChatColor shooterColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Shot by " +
                shooterColor + shooter.getName());
    }

    public static void playerWasHeadshotToDeath(Player killedPlayer, Player shooter, ChatColor shooterColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Headshot by " +
                shooterColor + shooter.getName());
    }

    public static void playerWasKnifedToDeath(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Knifed by " +
                killerColor + killer.getName());
    }

    public static void playerWasInfectedDeath(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Infected by " + killerColor + killer.getName());
    }

    public static void playerWasNukeKilled(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Nuked by " +
                killerColor + killer.getName());
    }

    public static void playerOutOfMapKilled(Player killedPlayer) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] Out of the map!");
    }

    public static void playerWasGrenadedToDeath(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Grenaded by " +
                killerColor + killer.getName());
    }

    public static void playerWasBurnedByMolotov(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Burned by " +
                killerColor + killer.getName());
    }

    //Capture flag

    public static void playerCapturedFlag(Player playerWhoCaptured, ChatColor flagColor) {
        int xp = Reward.CAPTURE_FLAG.getXp();
        int credits = Reward.CAPTURE_FLAG.getCredits();

        String message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY +
                "Captured the " + flagColor;

        if(flagColor == ChatColor.BLUE) {
            message += "Blue";
        } else {
            message += "Red";
        }

        message += ChatColor.GRAY + " flag" + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";

        playerWhoCaptured.sendMessage(message);
    }

    public static void teamCapturedFlag(Player player, Team team) {
        if(team.getTeamColorAsChatColor() == ChatColor.RED) {
            player.sendMessage(ChatColor.RED + "Red" + ChatColor.GRAY + " flag was captured");
        } else {
            player.sendMessage(ChatColor.BLUE + "Blue" + ChatColor.GRAY + " flag was captured");
        }
    }

    public static void enemyTeamCapturedFlag(Player player, Team team) {
        if(team.getTeamColorAsChatColor() == ChatColor.BLUE) {
            player.sendMessage(ChatColor.RED + "Red" + ChatColor.GRAY + " flag was captured");
        } else {
            player.sendMessage(ChatColor.BLUE + "Blue" + ChatColor.GRAY + " flag was captured");
        }
    }


    //End game statistics

    public static void displayFreeForAllEndGame(PlayerExtension winner, int winnerKills, Player player) {
        if(winner != null) {
            player.sendMessage(ChatColor.GOLD + "Winner: " + ChatColor.LIGHT_PURPLE + winner.getName() + ChatColor.GRAY
                    + " (" + ChatColor.LIGHT_PURPLE + winnerKills + ChatColor.GRAY + ")");
        } else {
            player.sendMessage(ChatColor.GOLD + "Winner: " + ChatColor.GRAY + "None");
        }
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
                    " (" + winnerTeam.getTeamColorAsChatColor() + winnerTeam.getCaptures() + ChatColor.GRAY + " - " +
                    loserTeam.getTeamColorAsChatColor() + loserTeam.getCaptures() + ChatColor.GRAY + ")");
        } else {
            player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + ChatColor.GRAY +
                    " (" + winnerTeam.getTeamColorAsChatColor() + winnerTeam.getCaptures() + ChatColor.GRAY + " - " +
                    loserTeam.getTeamColorAsChatColor() + loserTeam.getCaptures() + ChatColor.GRAY + ")");
        }
    }

    public static void displayInfectEndGame(String winner, Team winnerTeam, Player player) {
        player.sendMessage(ChatColor.GOLD + "Winner: " + winnerTeam.getTeamColorAsChatColor() + winner + ChatColor.GOLD + " won!");
    }

    public static void displayPersonalStats(Player player, int kills, int deaths, int totalKills, int totalDeaths,int xpGained, int creditsGained) {

        player.sendMessage(ChatColor.GOLD + "Kills: " + ChatColor.GREEN +  kills + ChatColor.GOLD + "   Deaths: " + ChatColor.RED + deaths + ChatColor.GOLD +
                "   K/D Ratio: " + TextUtils.getRatioAsRedOrGreenString(kills, deaths, totalKills, totalDeaths) + "\n" + ChatColor.RESET +
                ChatColor.GOLD + "Earned xp: " + ChatColor.GREEN + xpGained + ChatColor.GOLD + "   Earned credits: " + ChatColor.GREEN + creditsGained
        );

        /*
        player.sendMessage("Colors:" + ChatColor.DARK_GRAY + "DARKGRAY" + ChatColor.GRAY + "GRAY" + ChatColor.GOLD + "GOLD" +
                ChatColor.YELLOW + "YELLOW" + ChatColor.DARK_BLUE + "DARKBLUE" + ChatColor.BLACK + "BLACK" + ChatColor.GREEN +
                "GREEN" + ChatColor.LIGHT_PURPLE + "LIGHTPURPLE" + ChatColor.DARK_RED + "DARKRED" + ChatColor.DARK_GREEN + "DARKGREEN" +
                ChatColor.AQUA + "AQUA" + ChatColor.RED + "RED" + ChatColor.DARK_AQUA + "DARKAQUA" + ChatColor.WHITE + "WHITE" +
                ChatColor.BLUE + "BLUE" + ChatColor.DARK_PURPLE +"DARKPURPLE" + ChatColor.COLOR_CHAR + "ColorChar?");*/

    }

    private static String getNDecimalPlaces(int n, double number) {
        return String.valueOf(Math.round(number*Math.pow(10,n))/Math.pow(10,n));
    }

    private static double getRatio(double numerator, double denominator) {
        return (denominator == 0) ? numerator : numerator/denominator;
    }
}
