package me.noaz.testplugin.weapons.guns;

public enum GunType {
    ASSAULT_RIFLE ("Assault rifle"),
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
