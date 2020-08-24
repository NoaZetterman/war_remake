package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.guns.Gun;

public class Resupply implements KillstreakInterface {
    @Override
    public void use(PlayerExtension player, CustomTeam friendlyCustomTeam, CustomTeam enemyCustomTeam) {
        Gun primaryGun = player.getActivePrimaryGun();
        Gun secondaryGun = player.getActiveSecondaryGun();

        primaryGun.addBullets(primaryGun.getConfiguration().getMaxResupplyAmmunition());
        secondaryGun.addBullets(secondaryGun.getConfiguration().getMaxResupplyAmmunition());

        //Send message to player?
    }
}
