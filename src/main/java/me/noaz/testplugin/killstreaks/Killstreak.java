package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.player.PlayerExtension;

public enum Killstreak {
    EMP(new EMP(), 15),
    NUKE(new Nuke(), 25),
    RESUPPLY(new Resupply(), 5);


    KillstreakInterface killstreak;
    int killAmount;

    Killstreak(KillstreakInterface killstreak, int killAmount) {
        this.killstreak = killstreak;
        this.killAmount = killAmount;

    }

    public void use(PlayerExtension player, Team friendlyTeam, me.noaz.testplugin.gamemodes.misc.Team enemyTeam) {
        this.killstreak.use(player, friendlyTeam, enemyTeam);
    }

    public int getKillAmount() {
        return killAmount;
    }
}//Turn into enum with a use method
