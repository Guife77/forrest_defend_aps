package game.enemies;

import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;

public class Pollution extends Enemy {

    public Pollution() {
        super("Pollution",
                Constants.POLLUTION_HP,
                Constants.POLLUTION_SPEED,
                Constants.POLLUTION_DAMAGE_TO_BASE,
                Constants.POLLUTION_REWARD_RF);
    }

    @Override
    public void takeDamage(double damage, AttackType attackType) {
        if (attackType == AttackType.POISON) return; // imune
        applyRawDamage(damage);
    }
}