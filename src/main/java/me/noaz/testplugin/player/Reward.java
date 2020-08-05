package me.noaz.testplugin.player;

public enum Reward {
    CAPTURE_FLAG(100, 5),
    BODYSHOT_KILL(25, 1),
    HEADSHOT_KILL(35,2),
    KNIFE_KILL(25, 1),
    ZOMBIE_KILL_HUMAN(25, 1),
    NUKE_KILL(25,1),
    GRENADE_KILL(45, 3),
    MOLOTOV_KILL(50, 4),
    TOMAHAWK_KILL(50,3);

    private int xp;
    private int credits;

    Reward(int xp, int credits) {
        this.xp = xp;
        this.credits = credits;
    }

    public int getCredits() {
        return credits;
    }

    public int getXp() {
        return xp;
    }
}
