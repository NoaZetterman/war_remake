package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.messages.ChatMessage;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.player.Reward;
import org.bukkit.potion.PotionEffectType;

public class Nuke implements KillstreakInterface {
    @Override
    public void use(PlayerExtension player, CustomTeam friendlyCustomTeam, CustomTeam enemyCustomTeam) {
        BroadcastMessage.launchNuke(player.getName());
        for(PlayerExtension enemyPlayer : enemyCustomTeam.getPlayers()) {
            if(!enemyPlayer.isDead() && enemyPlayer != player
                    && !enemyPlayer.getPlayer().hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                enemyPlayer.addDeath();

                ChatMessage.playerNukeKilled(player.getPlayer(), enemyPlayer.getPlayer(),
                        enemyPlayer.getTeamChatColor());
                ChatMessage.playerWasNukeKilled(enemyPlayer.getPlayer(), player.getPlayer(), player.getTeamChatColor());
                player.addKill(Reward.NUKE_KILL);
                enemyPlayer.respawn(player.getPlayer());

            }
        }
    }
}
