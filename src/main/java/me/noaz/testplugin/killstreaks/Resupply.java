package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.Team;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.guns.Gun;

public class Resupply implements KillstreakInterface {
    @Override
    public void use(PlayerExtension player, Team friendlyTeam, Team enemyTeam) {
        Gun primaryGun = player.getActivePrimaryGun();
        Gun secondaryGun = player.getActiveSecondaryGun();

        primaryGun.addBullets(primaryGun.getConfiguration().maxResupplyAmmunition);
        secondaryGun.addBullets(secondaryGun.getConfiguration().maxResupplyAmmunition);

        //Send message to player?
    }
}
