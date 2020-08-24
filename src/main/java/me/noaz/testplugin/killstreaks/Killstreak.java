package me.noaz.testplugin.killstreaks;

import me.noaz.testplugin.Buyable;
import me.noaz.testplugin.gamemodes.misc.CustomTeam;
import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.Material;

public enum Killstreak {
    EMP(new EMP(), 15, 10, Material.APPLE),
    NUKE(new Nuke(), 25, -1, Material.APPLE),
    RESUPPLY(new Resupply(), 5, -1, Material.APPLE),
    VSAT(new VSAT(), 12, 9, Material.APPLE);


    KillstreakInterface killstreak;
    int killAmount;
    int loadoutMenuSlot;
    Material material;

    Killstreak(KillstreakInterface killstreak, int killAmount, int loadoutMenuSlot, Material material) {
        this.killstreak = killstreak;
        this.killAmount = killAmount;
        this.loadoutMenuSlot = loadoutMenuSlot;
        this.material = material;

    }

    public void use(PlayerExtension player, CustomTeam friendlyCustomTeam, CustomTeam enemyCustomTeam) {
        this.killstreak.use(player, friendlyCustomTeam, enemyCustomTeam);
    }

    public int getKillAmount() {
        return killAmount;
    }

    public int getLoadoutMenuSlot() {
        return loadoutMenuSlot;
    }

    public Material getMaterial() {
        return material;
    }

    public int getUnlockLevel() {
        return 0;
    }

    public int getCostToBuy() {
        return 0;
    }

    public Buyable getAsBuyable() {
        return new Buyable(name(), name(), getUnlockLevel(), getCostToBuy(), loadoutMenuSlot, material);
    }
}//Turn into enum with a use method
