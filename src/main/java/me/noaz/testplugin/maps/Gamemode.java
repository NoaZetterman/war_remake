package me.noaz.testplugin.maps;

public enum Gamemode {
    TEAM_DEATHMATCH("Team Deathmatch"),
    CAPTURE_THE_FLAG ("Capture the Flag"),
    INFECT ("Infect"),
    FREE_FOR_ALL ("Free for all");

    private String gamemode;

    Gamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public String toString() {
        return gamemode;
    }
}
