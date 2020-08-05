package me.noaz.testplugin.weapons.lethals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.ThrowableItem;
import org.bukkit.Location;
import org.bukkit.Material;

public class Grenade extends ThrowableItem implements Lethal {
    public Grenade(PlayerExtension playerExtension, GameMap map, TestPlugin plugin) {
        super(playerExtension, map, plugin, Material.APPLE, 3, 1.3f, 3, 2, 20, 30);
    }

    public void activateItem(Location itemLocation) {
        map.getWorld().createExplosion(itemLocation, range,false,false, playerExtension.getPlayer());
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }
}
