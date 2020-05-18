package me.noaz.testplugin.maps;

public enum Gamemode {
    TEAM_DEATH_MATCH ("tdm"),
    CAPTURE_THE_FLAG ("ctf"),
    INFECT ("infect"),
    FREE_FOR_ALL ("ffa");

    private String gamemode;

    Gamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public String getGamemodeString() {
        return gamemode;
    }
}
