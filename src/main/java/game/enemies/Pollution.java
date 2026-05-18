package game.enemies;

import game.animation.Poluicao;
import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;

import java.awt.Graphics2D;

public class Pollution extends Enemy {

    private final Poluicao anim;

    public Pollution() {
        super("Pollution",
                Constants.POLLUTION_HP,
                Constants.POLLUTION_SPEED,
                Constants.POLLUTION_DAMAGE_TO_BASE,
                Constants.POLLUTION_REWARD_RF);
        this.anim = new Poluicao();
    }

    @Override
    public void takeDamage(double damage, AttackType attackType) {
        if (attackType == AttackType.POISON) return;
        applyRawDamage(damage);
    }

    public void render(Graphics2D g) {
        anim.render(g, (int) x, (int) y);
    }
}
