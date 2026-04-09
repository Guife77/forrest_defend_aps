package game.entities;

public class Player {

    private int mana;
    private int forestResources;
    private int baseHealth;

    public Player(int mana, int forestResources, int baseHealth) {
        this.mana = mana;
        this.forestResources = forestResources;
        this.baseHealth = baseHealth;
    }

    public void addMana(int value) {
        if (value > 0) {
            this.mana += value;
        }
    }

    public boolean spendMana(int value) {
        if (value <= 0) {
            return false;
        }

        if (mana < value) {
            return false;
        }

        mana -= value;
        return true;
    }

    public void addForestResources(int value) {
        if (value > 0) {
            this.forestResources += value;
        }
    }

    public void takeBaseDamage(int value) {
        if (value <= 0) {
            return;
        }

        this.baseHealth -= value;

        if (this.baseHealth < 0) {
            this.baseHealth = 0;
        }
    }

    public boolean isBaseAlive() {
        return baseHealth > 0;
    }

    public int getMana() {
        return mana;
    }

    public int getForestResources() {
        return forestResources;
    }

    public int getBaseHealth() {
        return baseHealth;
    }
}