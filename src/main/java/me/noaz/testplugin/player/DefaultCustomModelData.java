package me.noaz.testplugin.player;

public enum DefaultCustomModelData {
    DEFAULT_VALUE(10000000),
    SCOPE_START(10000000),
    RELOAD_START(10000100);

    int value;
    DefaultCustomModelData(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
