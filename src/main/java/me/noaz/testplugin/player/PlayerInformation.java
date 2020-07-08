package me.noaz.testplugin.player;

import me.noaz.testplugin.commands.Resource;
import me.noaz.testplugin.dao.PlayerDao;
import me.noaz.testplugin.perk.Perk;
import me.noaz.testplugin.weapons.lethals.Grenade;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class stores information regarding one players statistics, both in a game and in spawn, should be updated
 * accordingly when player gets a kill etc.
 *
 * @author Noa Zetterman
 * @version 2019-12-10
 */
public class PlayerInformation {
    private Player player;

    private Long totalOnlineTimeInSeconds;
    private Long lastSavedTimeInSeconds;
    private Resourcepack selectedResourcepack;

    private List<String> ownedPrimaryGuns;
    private List<String> ownedSecondaryGuns;
    private List<Perk> ownedPerks;

    private String selectedPrimaryGun;
    private String selectedSecondaryGun;
    private Perk selectedPerk;
    private Grenade selectedLethal;

    private int[] levels;

    private int totalKills;
    private int totalDeaths;
    private int level;
    private int xpOnCurrentLevel;
    private int totalXpOnCurrentLevel;
    private int totalFiredBullets;
    private int totalFiredBulletsThatHitEnemy;
    private int totalHeadshotKills;
    private int credits;
    private int totalCaptures;

    private int kills = 0;
    private int deaths = 0;
    private int killstreak = 0;
    private int firedBullets = 0;
    private int firedBulletsThatHitEnemy = 0;
    private int creditsThisGame;
    private int xpThisGame;
    private int captures = 0;

    public PlayerInformation(Player player, List<String> ownedPrimaryGuns, List<String> ownedSecondaryGuns,
                             List<Perk> ownedPerks, String selectedPrimaryGun, String selectedSecondaryGun, Perk selectedPerk,
                             Resourcepack selectedResourcepack, Long totalOnlineTimeInSeconds,
                             int totalKills, int totalDeaths, int totalFiredBullets, int totalFiredBulletsThatHitEnemy,
                             int xpOnCurrentLevel, int level, int credits, int totalHeadshotKills) {
        this.player = player;

        this.totalOnlineTimeInSeconds = totalOnlineTimeInSeconds;
        lastSavedTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime());

        this.selectedResourcepack = selectedResourcepack;

        this.ownedPrimaryGuns = ownedPrimaryGuns;
        this.ownedSecondaryGuns = ownedSecondaryGuns;
        this.ownedPerks = ownedPerks;
        this.selectedPrimaryGun = selectedPrimaryGun;
        this.selectedSecondaryGun = selectedSecondaryGun;
        this.selectedPerk = selectedPerk;

        this.totalKills = totalKills;
        this.totalDeaths = totalDeaths;
        this.totalFiredBullets = totalFiredBullets;
        this.totalFiredBulletsThatHitEnemy = totalFiredBulletsThatHitEnemy;
        this.xpOnCurrentLevel = xpOnCurrentLevel;
        this.level = level;
        this.credits = credits;
        this.totalHeadshotKills = totalHeadshotKills;

        //Redo lvls
        levels = new int[] {100, 120, 150, 200, 325, 450, 700, 1000, 1500, 3000, 5000, 8500, 12000, 15000, 20000, 30000,
                45000, 60000, 80000, 100000, 150000, 200000, 300000, 400000, 500000};

        totalXpOnCurrentLevel = levels[level-1];

        player.setLevel(level);

        addXp(0);

        player.setDisplayName("Lvl " + level + " " + player.getName());
    }

    public Player getPlayer() {
        return player;
    }

    public Long getTotalOnlineTimeInSeconds() {
        return totalOnlineTimeInSeconds;
    }

    public boolean hasPrimary(String gunName) {
        return ownedPrimaryGuns.contains(gunName);
    }

    public boolean hasSecondary(String gunName) {
        return ownedSecondaryGuns.contains(gunName);
    }

    public boolean hasPerk(Perk perk) {
        return ownedPerks.contains(perk);
    }

    public List<String> getOwnedPrimaryGuns() {
        return ownedPrimaryGuns;
    }

    public List<String> getOwnedSecondaryGuns() {
        return ownedSecondaryGuns;
    }

    public List<Perk> getOwnedPerks() {
        return ownedPerks;
    }

    public String getSelectedPrimaryGun() {
        return selectedPrimaryGun;
    }

    public String getSelectedSecondaryGun() {
        return selectedSecondaryGun;
    }

    public Perk getSelectedPerk() {
        return selectedPerk;
    }

    public Grenade getSelectedLethal() {
        return selectedLethal;
    }

    public Resourcepack getSelectedResourcepack() {
        return selectedResourcepack;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getTotalFiredBullets() {
        return totalFiredBullets;
    }

    public int getTotalFiredBulletsThatHitEnemy() {
        return totalFiredBulletsThatHitEnemy;
    }

    public int getXpOnCurrentLevel() {
        return xpOnCurrentLevel;
    }

    public int getTotalXpOnCurrentLevel() {
        return totalXpOnCurrentLevel;
    }

    public int getTotalHeadshotKills() {
        return totalHeadshotKills;
    }

    public int getTotalCaptures() {
        return totalCaptures;
    }

    /**
     * @return This players level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return This players credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * @return This players killstreak
     */
    public int getKillstreak() {
        return killstreak;
    }

    /**
     * @return The amount of kills by this player in this game
     */
    public int getKillsThisGame() {
        return kills;
    }

    /**
     @return The amount of deaths by this player in this game
     */
    public int getDeathsThisGame() {
        return deaths;
    }

    public int getFiredBulletsThisGame() {
        return firedBullets;
    }

    public int getFiredBulletsThatHitEnemyThisGame() {
        return firedBulletsThatHitEnemy;
    }

    /**
     * @return The amount of credits this player have earned during the current game.
     */
    public int getCreditsThisGame() {
        return creditsThisGame;
    }

    public int getXpThisGame() {
        return xpThisGame;
    }

    public void addPrimary(String name) {
        ownedPrimaryGuns.add(name);
    }

    public void addSecondary(String name) {
        ownedSecondaryGuns.add(name);
    }

    public void addPerk(Perk perk) {
        ownedPerks.add(perk);
    }


    public void setSelectedPrimaryGun(String primaryGun) {
        this.selectedPrimaryGun = primaryGun;
    }

    public void setSelectedSecondaryGun(String secondaryGun) {
        this.selectedSecondaryGun = secondaryGun;
    }

    public void setSelectedPerk(Perk perk) {
        this.selectedPerk = perk;
    }

    public void setSelectedLethal(Grenade lethal) {
        this.selectedLethal = lethal;
    }

    public void setSelectedResourcepack(Resourcepack resourcepack) {
        selectedResourcepack = resourcepack;
    }

    /**
     * Add xp to this player, levels the player if necessary.
     * Changes scoreboard accordingly
     *
     * @param amount The amount of xp
     */
    public void addXp(int amount) {
        xpOnCurrentLevel += amount;
        xpThisGame += amount;
        if(xpOnCurrentLevel >= totalXpOnCurrentLevel) {
            level++;
            player.setLevel(level);
            xpOnCurrentLevel -= totalXpOnCurrentLevel;
            player.setExp(0);
            totalXpOnCurrentLevel = levels[level-1]; //Get amount from database
            //much xp per level.
        }

        float xpToAddInPercent = ((float) xpOnCurrentLevel) / ((float) totalXpOnCurrentLevel);
        player.setExp(xpToAddInPercent);
    }

    /**
     * Gives the player a kill and changes scoreboard accordingly
     */
    public void addKill() {
        kills++;
        killstreak++;
    }

    public void addHeadshotKill() {
        totalHeadshotKills++;
    }

    public void addCredits(int amount) {
        credits += amount;

        //Player can only earn credits in a game
        if(amount >= 0) {
            creditsThisGame += amount;
        }
    }

    public void addReward(Reward reward) {
        addXp(reward.getXp());
        addCredits(reward.getCredits());

    }

    public void addCapture() {
        captures++;
    }

    /**
     * Gives the player a death and changes scoreboard accordingly
     */
    public void addDeath() {
        deaths++;
        killstreak = 0;
    }

    /**
     * Adds a bullets to the total amount of hit bullets and changes scoreboard accordingly
     */
    public void addBulletHit() {
        firedBulletsThatHitEnemy++;

    }

    /**
     * Adds bullets to the total amount of fired bullets and changes the scoreboard accordingly
     * @param amount The amount of bullets fired
     */
    public void addBulletsShot(int amount) {
        firedBullets += amount;
    }

    /**
     * Resets the stats from last game, and updates the overall stats accordingly
     */
    public void updateTotalScore() {
        totalKills += kills;
        totalDeaths += deaths;
        totalFiredBullets += firedBullets;
        totalFiredBulletsThatHitEnemy += firedBulletsThatHitEnemy;

        kills = 0;
        deaths = 0;
        killstreak = 0;
        firedBullets = 0;
        firedBulletsThatHitEnemy = 0;
        xpThisGame = 0;
        creditsThisGame = 0;
        captures = 0;

        long currentTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime());
        long passedTimeInSeconds = currentTimeInSeconds - lastSavedTimeInSeconds;

        totalOnlineTimeInSeconds += passedTimeInSeconds;
        lastSavedTimeInSeconds = currentTimeInSeconds;
    }
}