package me.noaz.testplugin.weapons.tacticals;

import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Location;

public class TacInsert implements Tactical {
    private PlayerExtension playerExtension;

    private Location savedLocation = null;

    public TacInsert(PlayerExtension playerExtension) {
        this.playerExtension = playerExtension;
    }

    @Override
    public void use() {
        playerExtension.getPlayer().getInventory().setItem(itemSlot, null);

        savedLocation = playerExtension.getLocation();
    }

    public boolean hasSavedLocation() {
        return savedLocation != null;
    }

    public Location getSavedLocation() {
        return savedLocation;
    }
}
