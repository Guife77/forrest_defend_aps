package game.enemies;

import game.animation.LumberjackArt;
import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;

import java.awt.Graphics2D;

public class Lumberjack extends Enemy {

    private final long spawnTime = System.currentTimeMillis();

    public Lumberjack() {
        super("Lumberjack",
                Constants.LUMBERJACK_HP,
                Constants.LUMBERJACK_SPEED,
                Constants.LUMBERJACK_DAMAGE_TO_BASE,
                Constants.LUMBERJACK_REWARD_RF);
    }

    @Override
    public void takeDamage(double damage, AttackType attackType) {
        applyRawDamage(damage);
    }

    public void render(Graphics2D g) {
        float phase = (System.currentTimeMillis() - spawnTime) / 1000f;
        LumberjackArt.render(g, (int) x, (int) y, 3, phase);
    }
}
