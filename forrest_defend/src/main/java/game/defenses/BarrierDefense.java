package game.defenses;

import game.entities.Tower;
import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;
import java.awt.Graphics;
import java.awt.Color;
import java.util.List;

public class BarrierDefense extends Tower {


    private static final double PHYSICAL_RESISTANCE = 0.5;

    public BarrierDefense() {
        super(
                "Barreira",
                Constants.BARRIER_HP,
                Constants.BARRIER_DAMAGE,
                Constants.BARRIER_RANGE,
                AttackType.PHYSICAL
        );
    }

    @Override
    public void takeDamage(double damage, AttackType attackType) {
        double finalDamage = damage;

        // Se o inimigo bater nela com ataque físico, ela só toma metade do dano!
        if (attackType == AttackType.PHYSICAL) {
            finalDamage *= (1.0 - PHYSICAL_RESISTANCE);
        }

        applyRawDamage(finalDamage);
    }

    @Override
    public void attack(List<Enemy> enemies) {

    }

    @Override
    public void update() {
        // sem animacao
    }

    @Override
    public void render(Graphics g) {

    }
}