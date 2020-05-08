package me.noaz.testplugin.messages;

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

    public static void playerShotKilled(Player shooter, Player killedPlayer, ChatColor killedColor) {
        shooter.sendMessage(ChatColor.GRAY + "[+] " + ChatColor.GRAY + "Shot " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+25xp +1$");

        //Maybe use below for specials, like the objective
        /*shooter.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY + "Shot " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+25xp +1$");*/
    }

    public static void playerWasHeadshotToDeath(Player killedPlayer,Player shooter, ChatColor shooterColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Headshot by " +
                shooterColor + shooter.getName());
    }

    public static void playerHeadshotKilled(Player shooter, Player killedPlayer, ChatColor killedColor) {
        shooter.sendMessage(ChatColor.GRAY + "[+] " + ChatColor.GRAY + "Headshot " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+25xp +1$");
    }

    public static void playerWasKnifedToDeath(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Knifed by " +
                killerColor + killer.getName());
    }

    public static void playerKnifeKilled(Player killer, Player killedPlayer, ChatColor killedColor) {
        killer.sendMessage(ChatColor.GRAY + "[+] " + ChatColor.GRAY + "Knifed " +
                killedColor + killedPlayer.getName() + " " + ChatColor.YELLOW + "+25xp +1$");
        killedPlayer.sendMessage(killer.getName() + " knifed " + killedPlayer.getName());
    }

    public static void playerInfectedKill(Player killer, Player killedPlayer, ChatColor killedColor) {
        killer.sendMessage(ChatColor.GRAY + "[+] " + ChatColor.GRAY + "Infected " + killedColor + killedPlayer.getName());
    }
    public static void playerWasInfectedDeath(Player killedPlayer, Player killer, ChatColor killerColor) {
        killedPlayer.sendMessage(ChatColor.GRAY + "[-] " + ChatColor.GRAY + "Infected by " + killerColor + killer.getName());
    }

}
