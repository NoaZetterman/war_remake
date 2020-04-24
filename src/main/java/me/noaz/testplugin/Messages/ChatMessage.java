package me.noaz.testplugin.Messages;

import me.noaz.testplugin.player.PlayerExtension;
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

    public static void playerWasShotToDeath(Player killedPlayer, Player shooter) {
        killedPlayer.sendMessage(killedPlayer.getName() + " was shot by " + shooter.getName());
    }

    public static void playerShotKilled(Player shooter, Player killedPlayer) {
        shooter.sendMessage(shooter.getName() + " shot " + killedPlayer.getName());
    }

    public static void playerWasHeadshotToDeath(Player killedPlayer, Player shooter) {
        killedPlayer.sendMessage(killedPlayer.getName() + " was headshot by " + shooter.getName());
    }

    public static void playerHeadshotKilled(Player shooter, Player killedPlayer) {
        shooter.sendMessage(shooter.getName() + " headshot " + killedPlayer.getName());
    }

    public static void playerWasKnifedToDeath(Player killedPlayer, Player killer) {
        killedPlayer.sendMessage(killer.getName() + " knifed " + killedPlayer.getName());
    }

    public static void playerKnifeKilled(Player killedPlayer, Player killer) {
        killedPlayer.sendMessage(killer.getName() + " was knife by " + killedPlayer.getName());
    }
}
