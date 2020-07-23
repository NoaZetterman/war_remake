package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EMP implements KillstreakInterface {
    public void use(PlayerExtension player, Team friendlyTeam, Team enemyTeam) {
        for(PlayerExtension enemyPlayer : enemyTeam.getPlayers()) {
            if(enemyPlayer != player) {
                enemyPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 15, 4));
            }
        }

        BroadcastMessage.launchEmp(player.getName());
    }
}
