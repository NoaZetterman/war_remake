package me.noaz.testplugin.player;

public enum Reward {
    CAPTURE_FLAG(100, 5),
    BODYSHOT_KILL(25, 1),
    HEADSHOT_KILL(35,2),
    KNIFE_KILL(25, 1),
    ZOMBIE_KILL_HUMAN(25, 1),
    NUKE_KILL(25,1);

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
