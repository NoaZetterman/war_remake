package me.noaz.testplugin.weapons.guns;

public enum GunType {
    ASSAULT_RIFLE ("Assault rifle"),
    SNIPER_RIFLE("Sniper rifle"),
    SHOTGUN("Shotgun"),
    SMG("SMG"),
    SECONDARY ("Secondary");

    private String gunType;

    GunType(String gunType) {
        this.gunType = gunType;
    }

    @Override
    public String toString() {
        return gunType;
    }
}
