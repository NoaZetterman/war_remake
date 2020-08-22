package me.noaz.testplugin.weapons.tacticals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.ThrowableItem;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Flashbang extends ThrowableItem implements Tactical {
    public Flashbang(PlayerExtension playerExtension, TestPlugin plugin, int cooldownTimeInTicks) {
        super(playerExtension, playerExtension.getPlayer().getWorld(), plugin, 4, 1.3f, itemSlot, cooldownTimeInTicks, 0);
    }

    @Override
    protected void activateItem(Location itemLocation) {
        world.spawnParticle(Particle.FLASH, itemLocation.clone().add(0,0.15,0), 25, 1, 0.20, 1, 0);
    }
}
