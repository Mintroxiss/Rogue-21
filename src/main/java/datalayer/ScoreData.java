package datalayer;

public class ScoreData {
    private String name;
    private int gold;
    private int level;
    private int steps;
    private int kills;
    private int foodEaten;
    private int potionsDrunk;
    private int scrollsRead;
    private int hits;
    private int misses;

    public ScoreData() {}

    public ScoreData(String name, int gold, int level, int steps, int kills, int foodEaten, int potionsDrunk, int scrollsRead, int hits, int misses) {
        this.name = name;
        this.gold = gold;
        this.level = level;
        this.steps = steps;
        this.kills = kills;
        this.foodEaten = foodEaten;
        this.potionsDrunk = potionsDrunk;
        this.scrollsRead = scrollsRead;
        this.hits = hits;
        this.misses = misses;
    }

    public int getGold() {
        return gold;
    }

    public int getLevel() {
        return level;
    }

    public int getSteps() {
        return steps;
    }

    public int getKills() {
        return kills;
    }

    public int getPotionsDrunk() {
        return potionsDrunk;
    }

    public int getFoodEaten() {
        return foodEaten;
    }

    public int getScrollsRead() {
        return scrollsRead;
    }

    public int getMisses() {
        return misses;
    }

    public int getHits() {
        return hits;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setFoodEaten(int foodEaten) {
        this.foodEaten = foodEaten;
    }

    public void setPotionsDrunk(int potionsDrunk) {
        this.potionsDrunk = potionsDrunk;
    }

    public void setScrollsRead(int scrollsRead) {
        this.scrollsRead = scrollsRead;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public void setMisses(int misses) {
        this.misses = misses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
