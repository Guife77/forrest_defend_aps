package game.enemies;

import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;

public class Excavator extends Enemy {

    private static final double PHYSICAL_RESISTANCE = 0.5;

    public Excavator() {
        super("Excavator",
                Constants.EXCAVATOR_HP,
                Constants.EXCAVATOR_SPEED,
                Constants.EXCAVATOR_DAMAGE_TO_BASE,
                Constants.EXCAVATOR_REWARD_RF);
    }

    @Override
    public void takeDamage(double damage, AttackType attackType) {
        double finalDamage = damage;
        if (attackType == AttackType.PHYSICAL || attackType == AttackType.PROJECTILE) {
            finalDamage *= (1.0 - PHYSICAL_RESISTANCE);
        }
        applyRawDamage(finalDamage);
    }
}