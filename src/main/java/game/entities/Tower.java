package game.entities;

import java.awt.Graphics2D;
import java.util.List;

public abstract class Tower {

    protected String name;
    protected double maxHp;
    protected double hp;
    protected int damage;
    protected double range;
    protected AttackType attackType;

    protected double x;
    protected double y;
    protected boolean alive;

    // Cooldown de ataque
    protected int attackCooldown    = 0;
    protected int attackCooldownMax = 60; // ticks entre ataques (1 segundo a 60fps)

    public Tower(String name, double maxHp, int damage, double range, AttackType attackType) {
        this.name       = name;
        this.maxHp      = maxHp;
        this.hp         = maxHp;
        this.damage     = damage;
        this.range      = range;
        this.attackType = attackType;
        this.alive      = true;
    }

    public abstract void attack(List<Enemy> enemies);
    public abstract void takeDamage(double damage, AttackType attackType);
    public abstract void render(Graphics2D g);

    public void update() {
        if (attackCooldown > 0) attackCooldown--;
    }

    protected boolean canAttack() {
        return attackCooldown <= 0;
    }

    protected void resetCooldown() {
        attackCooldown = attackCooldownMax;
    }

    public void onDeath() {
        if (!alive) return;
        alive = false;
    }

    public boolean isAlive() { return alive && hp > 0; }

    protected void applyRawDamage(double damage) {
        if (!alive || damage <= 0) return;
        hp -= damage;
        if (hp <= 0) { hp = 0; onDeath(); }
    }

    public void setPosition(double x, double y) { this.x = x; this.y = y; }

    public String getName()         { return name; }
    public double getMaxHp()        { return maxHp; }
    public double getHp()           { return hp; }
    public int getDamage()          { return damage; }
    public double getRange()        { return range; }
    public AttackType getAttackType(){ return attackType; }
    public double getX()            { return x; }
    public double getY()            { return y; }
}