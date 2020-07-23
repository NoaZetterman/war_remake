package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.guns.Gun;

public class Resupply implements KillstreakInterface {
    @Override
    public void use(PlayerExtension player, Team friendlyTeam, Team enemyTeam) {
        Gun primaryGun = player.getPrimaryGun();
        Gun secondaryGun = player.getSecondaryGun();

        primaryGun.addBullets(primaryGun.getConfiguration().resupplyAmmo);
        secondaryGun.addBullets(secondaryGun.getConfiguration().resupplyAmmo);

        //Send message to player?
    }
}
