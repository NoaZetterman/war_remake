package me.noaz.testplugin.player;

import me.noaz.testplugin.killstreaks.Killstreak;
import me.noaz.testplugin.perk.Perk;
import me.noaz.testplugin.weapons.lethals.Grenade;
import me.noaz.testplugin.weapons.lethals.LethalEnum;
import me.noaz.testplugin.weapons.tacticals.Tactical;
import me.noaz.testplugin.weapons.tacticals.TacticalEnum;
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
    private List<Killstreak> ownedKillstreaks;
    private List<LethalEnum> ownedLethals;
    private List<TacticalEnum> ownedTacticals;

    private String selectedPrimaryGun;
    private String selectedSecondaryGun;
    private Perk selectedPerk;
    private LethalEnum selectedLethal;
    private TacticalEnum selectedTactical;
    private Killstreak selectedKillstreak;

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
    private int totalFlagCaptures;
    private int freeForAllWins;

    private int kills = 0;
    private int deaths = 0;
    private int killstreak = 0;
    private int firedBullets = 0;
    private int firedBulletsThatHitEnemy = 0;
    private int creditsThisGame;
    private int xpThisGame;
    private int flagCapturesThisGame = 0;

    public PlayerInformation(Player player, List<String> ownedPrimaryGuns, List<String> ownedSecondaryGuns,
                             List<Perk> ownedPerks, List<Killstreak> ownedKillstreaks, List<LethalEnum> ownedLethals,
                             List<TacticalEnum> ownedTacticals, String selectedPrimaryGun,
                             String selectedSecondaryGun, Perk selectedPerk, Killstreak selectedKillstreak, LethalEnum selectedLethal,
                             TacticalEnum selectedTactical,
                             Resourcepack selectedResourcepack, Long totalOnlineTimeInSeconds,
                             int totalKills, int totalDeaths, int totalFiredBullets, int totalFiredBulletsThatHitEnemy,
                             int xpOnCurrentLevel, int level, int credits, int totalHeadshotKills,
                             int totalFlagCaptures, int freeForAllWins) {
        this.player = player;

        this.totalOnlineTimeInSeconds = totalOnlineTimeInSeconds;
        lastSavedTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime());

        this.selectedResourcepack = selectedResourcepack;

        this.ownedPrimaryGuns = ownedPrimaryGuns;
        this.ownedSecondaryGuns = ownedSecondaryGuns;
        this.ownedPerks = ownedPerks;
        this.ownedKillstreaks = ownedKillstreaks;
        this.ownedLethals = ownedLethals;
        this.ownedTacticals = ownedTacticals;

        this.selectedPrimaryGun = selectedPrimaryGun;
        this.selectedSecondaryGun = selectedSecondaryGun;
        this.selectedPerk = selectedPerk;
        this.selectedKillstreak = selectedKillstreak;
        this.selectedLethal = selectedLethal;
        this.selectedTactical = selectedTactical;

        this.totalKills = totalKills;
        this.totalDeaths = totalDeaths;
        this.totalFiredBullets = totalFiredBullets;
        this.totalFiredBulletsThatHitEnemy = totalFiredBulletsThatHitEnemy;
        this.xpOnCurrentLevel = xpOnCurrentLevel;
        this.level = level;
        this.credits = credits;
        this.totalHeadshotKills = totalHeadshotKills;
        this.freeForAllWins = freeForAllWins;
        this.totalFlagCaptures = totalFlagCaptures;

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

    public boolean hasLethal(LethalEnum lethal) {
        return ownedLethals.contains(lethal);
    }

    public boolean hasTactical(TacticalEnum tactical) {
        return ownedTacticals.contains(tactical);
    }

    public boolean hasKillstreak(Killstreak killstreak) {
        return ownedKillstreaks.contains(killstreak);
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

    public List<Killstreak> getOwnedKillstreaks() {
        return ownedKillstreaks;
    }

    public List<LethalEnum> getOwnedLethals() {
        return ownedLethals;
    }

    public List<TacticalEnum> getOwnedTacticals() {
        return ownedTacticals;
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

    public LethalEnum getSelectedLethal() {
        return selectedLethal;
    }

    public TacticalEnum getSelectedTactical() {
        return selectedTactical;
    }

    public Killstreak getSelectedKillstreak() {
        return selectedKillstreak;
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

    public int getTotalFlagCaptures() {
        return totalFlagCaptures;
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

    public int getFreeForAllWins() {
        return freeForAllWins;
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

    public void addLethal(LethalEnum lethal) {
        ownedLethals.add(lethal);
    }

    public void addTactical(TacticalEnum tactical) {
        ownedTacticals.add(tactical);
    }

    public void addKillstreak(Killstreak killstreak) {
        ownedKillstreaks.add(killstreak);
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

    public void setSelectedKillstreak(Killstreak selectedKillstreak) {
        this.selectedKillstreak = selectedKillstreak;
    }

    public void setSelectedLethal(LethalEnum lethal) {
        this.selectedLethal = lethal;
    }

    public void setSelectedTactical(TacticalEnum tactical) {
        this.selectedTactical = tactical;
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
        flagCapturesThisGame++;
    }

    public void addFreeForAllWin() {
        freeForAllWins++;
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
        totalFlagCaptures += flagCapturesThisGame;

        kills = 0;
        deaths = 0;
        killstreak = 0;
        firedBullets = 0;
        firedBulletsThatHitEnemy = 0;
        xpThisGame = 0;
        creditsThisGame = 0;
        flagCapturesThisGame = 0;

        long currentTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime());
        long passedTimeInSeconds = currentTimeInSeconds - lastSavedTimeInSeconds;

        totalOnlineTimeInSeconds += passedTimeInSeconds;
        lastSavedTimeInSeconds = currentTimeInSeconds;
    }
}
