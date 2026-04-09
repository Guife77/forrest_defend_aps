package game.entities;

public abstract class Enemy {

    protected String name;
    protected double maxHp;
    protected double hp;
    protected double speed;
    protected int damageToBase;
    protected int rewardRF;

    protected double x;
    protected double y;

    protected boolean alive;
    protected boolean reachedBase;

    public Enemy(String name, double maxHp, double speed, int damageToBase, int rewardRF) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.speed = speed;
        this.damageToBase = damageToBase;
        this.rewardRF = rewardRF;
        this.alive = true;
        this.reachedBase = false;
    }

    public abstract void move();

    public abstract void takeDamage(double damage, AttackType attackType);

    public void onDeath(Player player) {
        if (!alive) {
            return;
        }

        alive = false;

        if (player != null) {
            player.addForestResources(rewardRF);
        }
    }

    public boolean isAlive() {
        return alive && hp > 0;
    }

    public void reachBase(Player player) {
        if (!alive || reachedBase) {
            return;
        }

        reachedBase = true;
        alive = false;

        if (player != null) {
            player.takeBaseDamage(damageToBase);
        }
    }

    protected void applyRawDamage(double damage) {
        if (!alive || damage <= 0) {
            return;
        }

        hp -= damage;

        if (hp <= 0) {
            hp = 0;
        }
    }

    public String getName() {
        return name;
    }

    public double getMaxHp() {
        return maxHp;
    }

    public double getHp() {
        return hp;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDamageToBase() {
        return damageToBase;
    }

    public int getRewardRF() {
        return rewardRF;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean hasReachedBase() {
        return reachedBase;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}