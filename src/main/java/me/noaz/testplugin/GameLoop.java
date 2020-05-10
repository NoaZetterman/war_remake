package me.noaz.testplugin;

import me.noaz.testplugin.maps.GameMap;
import me.noaz.testplugin.messages.BossBarMessage;
import me.noaz.testplugin.messages.BroadcastMessage;
import me.noaz.testplugin.messages.PlayerListMessage;
import me.noaz.testplugin.gamemodes.*;

import me.noaz.testplugin.player.PlayerExtension;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameLoop {
    private GameData data;
    private TestPlugin plugin;

    private Game currentGame;
    private GameMap currentMap;
    private String currentGamemode;

    private BukkitRunnable perTickLoop;
    private BukkitRunnable perSecondLoop;

    private int timer = 60;

    public GameLoop(GameData data, TestPlugin plugin) {
        this.data = data;
        this.plugin = plugin;
        runGameLoops();
        pickNextGame();
    }

    private void runGameLoops() {

        perTickLoop = new BukkitRunnable() {

            @Override
            public void run() {
                for (PlayerExtension player : data.getPlayerExtensions()) {
                    player.updateActionBar();
                }

                if (currentGame != null) {
                    gameActionPerTick();
                } else {
                    lobbyActionPerTick();
                }
            }
        };

        perTickLoop.runTaskTimerAsynchronously(plugin, 120L, 1L);

        perSecondLoop = new BukkitRunnable() {
            @Override
            public void run() {
                timer--;
                if (currentGame != null) {
                    gameActionPerSecond();
                } else {
                    lobbyActionPerSecond();
                }

            }
        };

        perSecondLoop.runTaskTimer(plugin, 120L, 20L);

    }


    private void gameActionPerTick() {
        currentGame.updatePlayerList();
    }

    private void gameActionPerSecond() {
        BossBarMessage.timeUntilGameEnds(timer);
        if (currentGame.teamHasWon() || timer <= 0) {
            endGame();
        }
    }

    private void lobbyActionPerTick() {

    }

    private void lobbyActionPerSecond() {
        BossBarMessage.timeUntilNextGame(timer);
        if (timer % 10 == 0) {
            BroadcastMessage.timeLeftUntilGameStarts(timer);
        } else if (timer % 10 == 5) {
            BroadcastMessage.gameAndGamemode(currentMap.getName(), currentGamemode);
        }

        for(Player player : data.getPlayers()) {
            PlayerListMessage.setLobbyHeader(player, currentGamemode, currentMap.getName(),
                    currentMap.getMapCreators());
        }

        if(timer <= 0) {
            startGame();
        }
    }

    /**
     * Lets the player join the current game
     * @param player The player that should join the game
     * @return True if player is in game or joined game, false if there is no game to join
     */
    public boolean joinGame(Player player) {
        if(currentGame == null)
            return false;

        return currentGame.join(data.getPlayerExtension(player));
    }

    /**
     * Tries to leave the current game, if there is one, otherwise it does not
     * @param player The player that should leave the game
     * @return True if the player successfully left the game, false if the player is not in a game.
     */
    public boolean leaveGame(Player player) {
        if(currentGame == null)
            return false;

        return currentGame.leave(data.getPlayerExtension(player));
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public String getCurrentGamemode() {
        return currentGamemode;
    }

    /**
     * Start a game with the current settings for game and gamemode.
     */
    public void startGame() {
        if(currentGame == null) {
            switch(currentGamemode) {
                case "tdm":
                    //Send the map in instead of locations etc
                    currentGame = new TeamDeathMatch(currentMap, data.getPlayerExtensionHashMap());
                    break;
                case "ctf":
                    currentGame = new CaptureTheFlag(currentMap, plugin, data.getPlayerExtensionHashMap());
                    break;
                case "ffa":
                    currentGame = new FreeForAll(currentMap, data.getPlayerExtensionHashMap());
                    break;
                case "infect":
                    currentGame = new Infect(currentMap, data.getPlayerExtensionHashMap());
                default:
                    System.out.println("Something went wrong when starting game");
            }

            timer = currentGame.getLength();
            System.out.println("Starting game");
        }
    }

    /**
     * End the current game, and start a new one in 60 seconds
     */
    public void endGame() {
        if(currentGame != null) {
            currentGame.end(false);
            currentGame = null;

            BroadcastMessage.endGameMessage();

            timer = 60;

            pickNextGame();
        }
    }

    public void pickNextGame() {
        if(currentMap != null)
            currentMap.unloadMap();

        currentMap = data.getNewGameMap(currentMap);
        currentGamemode = currentMap.getRandomGamemode(data.getPlayercount());

        currentMap.loadMap();

        for(Player player : data.getPlayers()) {
            PlayerListMessage.setLobbyHeader(player, currentGamemode, currentMap.getName(),
                    currentMap.getMapCreators());
        }
    }

    /**
     * Picks a game with given map name and gamemode, if the map exists with that gamemode
     * @param mapName The name of the map
     * @param gamemode The name of the gamemode (ex: tdm, ctf, ...)
     * @return True if it was changed successfully, false otherwise
     */
    public boolean pickNextGame(String mapName, String gamemode) {
        //TODO: Fix so that this work when just switching gamemode and not map
        if(data.gameAndGamemodeExists(mapName, gamemode) && !mapName.equals(currentMap.getName())) {
            if(currentMap != null)
                currentMap.unloadMap();

            currentMap = data.getGameMap(mapName);
            currentGamemode = gamemode;

            currentMap.loadMap();


            for(Player player : data.getPlayers()) {
                PlayerListMessage.setLobbyHeader(player, currentGamemode, currentMap.getName(),
                        currentMap.getMapCreators());
            }
            return true;
        }

        return false;
    }

    public void stop() {
        if(currentGame != null) {
            System.out.println("Ending game and saving player data");
            currentGame.end(true);
        }

        perSecondLoop.cancel();
        perTickLoop.cancel();

        currentMap.unloadMap();
    }
}
