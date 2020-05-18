package me.noaz.testplugin.messages;

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
        if(gamemode == Gamemode.TEAM_DEATH_MATCH || gamemode == Gamemode.FREE_FOR_ALL) {
            message = ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] ";
        } else {
            message = ChatColor.GRAY + "[+] ";
        }

        message += ChatColor.GRAY + "Shot " + killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+" + xp + "xp +" + credits + "$";
        shooter.sendMessage(message);

        //Maybe use below for specials, like the objective
        /*shooter.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY + "Shot " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+25xp +1$");*/
    }

    public static void playerWasHeadshotToDeath(Player killedPlayer, Player shooter, ChatColor shooterColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Headshot by " +
                shooterColor + shooter.getName());
    }

    public static void playerHeadshotKilled(Player shooter, int xp, int credits, Player killedPlayer, ChatColor killedColor, Gamemode gamemode) {
        String message;
        if(gamemode == Gamemode.TEAM_DEATH_MATCH || gamemode == Gamemode.FREE_FOR_ALL) {
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
        if(gamemode == Gamemode.TEAM_DEATH_MATCH || gamemode == Gamemode.FREE_FOR_ALL) {
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

}
