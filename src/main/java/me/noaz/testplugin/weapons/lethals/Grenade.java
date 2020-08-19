package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.ThrowableItem;
import org.bukkit.Location;

public class Grenade extends ThrowableItem implements Lethal {
    public Grenade(PlayerExtension playerExtension, TestPlugin plugin, int cooldownTimeInTicks) {
        super(playerExtension, playerExtension.getPlayer().getWorld(), plugin,3, 1.3f, itemSlot, cooldownTimeInTicks, 30);
    }

    public void activateItem(Location itemLocation) {
        world.createExplosion(itemLocation, range,false,false, playerExtension.getPlayer());
    }
}
