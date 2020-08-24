package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.player.PlayerExtension;

public interface KillstreakInterface {
    void use(PlayerExtension player, CustomTeam friendlyCustomTeam, CustomTeam enemyCustomTeam);
}
