package me.noaz.testplugin;

import me.noaz.testplugin.Messages.BossBarMessage;
import me.noaz.testplugin.Messages.BroadcastMessage;
import me.noaz.testplugin.gamemodes.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class GameLoop extends BukkitRunnable {
    private Game currentGame;
    private GameController gameController;
    private int timer = 60;
    private int i = 0;

    public GameLoop(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void run() {
        i++;
        if(currentGame != null) {
            gameAction();
        } else {
            lobbyAction();
        }
    }

    private void gameAction() {
        if(i % 20 == 0) {
            timer--;
            BossBarMessage.timeUntilGameEnds(timer);
        }

        if (currentGame.teamHasWon() || timer <= 0) {
            gameController.endGame();
            currentGame = null;
        } else {
            currentGame.updatePlayerList();

        }
    }

    private void lobbyAction() {
        if(i % 20 == 0) {
            timer--;
            BossBarMessage.timeUntilNextGame(timer);
            if (timer % 10 == 0) {
                BroadcastMessage.timeLeftUntilGameStarts(timer);
            } else if (timer % 10 == 5) {
                BroadcastMessage.gameAndGamemode("idk", gameController.getCurrentGamemode());
            }
        }

        if(timer <= 0) {
            currentGame = gameController.startGame();
            timer = currentGame.getLength();
        }
    }
}
