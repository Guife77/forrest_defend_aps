package game.entities;

public class Player {

    private int mana;
    private int forestResources;
    private int baseHealth;

    public Player(int mana, int forestResources, int baseHealth) {
        this.mana            = mana;
        this.forestResources = forestResources;
        this.baseHealth      = baseHealth;
    }

    public void addMana(int value) {
        mana += value;
        if (mana < 0) mana = 0;
    }

    public boolean spendMana(int value) {
        if (value <= 0 || mana < value) return false;
        mana -= value;
        return true;
    }

    /** Positivo = ganhar, negativo = gastar */
    public void addForestResources(int value) {
        forestResources += value;
        if (forestResources < 0) forestResources = 0;
    }

    public void takeBaseDamage(int value) {
        if (value <= 0) return;
        baseHealth -= value;
        if (baseHealth < 0) baseHealth = 0;
    }

    public boolean isBaseAlive()        { return baseHealth > 0; }
    public int getMana()                { return mana; }
    public int getForestResources()     { return forestResources; }
    public int getBaseHealth()          { return baseHealth; }
}