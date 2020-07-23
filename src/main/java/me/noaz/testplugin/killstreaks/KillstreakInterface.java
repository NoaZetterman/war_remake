package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.player.PlayerExtension;

public interface KillstreakInterface {
    void use(PlayerExtension player, Team friendlyTeam, Team enemyTeam);
}
