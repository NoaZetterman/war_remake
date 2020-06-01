package me.noaz.testplugin.maps;

public enum Gamemode {
    TEAM_DEATHMATCH("tdm"),
    CAPTURE_THE_FLAG ("ctf"),
    INFECT ("infect"),
    FREE_FOR_ALL ("ffa");

    private String gamemode;

    Gamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public String toString() {
        return gamemode;
    }
}
