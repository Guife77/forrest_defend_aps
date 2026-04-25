package game.defenses;

import game.entities.AttackType;
import game.entities.Enemy;
import game.entities.Tower;
import game.utils.Constants;

import java.awt.*;
import java.util.List;

public class BarrierDefense extends Tower {

    private static final double PHYSICAL_RESISTANCE = 0.5;

    public BarrierDefense() {
        super("Barreira",
                Constants.BARRIER_HP,
                Constants.BARRIER_DAMAGE,
                Constants.BARRIER_RANGE,
                AttackType.PHYSICAL);
    }

    @Override
    public void attack(List<Enemy> enemies) { /* não ataca */ }

    @Override
    public void takeDamage(double damage, AttackType type) {
        double final_ = damage;
        if (type == AttackType.PHYSICAL) final_ *= (1.0 - PHYSICAL_RESISTANCE);
        applyRawDamage(final_);
    }

    @Override
    public void render(java.awt.Graphics2D g) { /* feito pelo TowerRenderer */ }
}