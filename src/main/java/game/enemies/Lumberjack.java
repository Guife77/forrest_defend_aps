package game.enemies;

import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;

public class Lumberjack extends Enemy {

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
}