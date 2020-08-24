package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EMP implements KillstreakInterface {
    public void use(PlayerExtension player, CustomTeam friendlyCustomTeam, CustomTeam enemyCustomTeam) {
        for(PlayerExtension enemyPlayer : enemyCustomTeam.getPlayers()) {
            if(enemyPlayer != player) {
                enemyPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 15, 4, false, false, false));
            }
        }

        BroadcastMessage.launchEmp(player.getName());
    }
}
