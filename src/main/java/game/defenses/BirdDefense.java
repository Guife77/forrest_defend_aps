package game.defenses;

import game.animation.Arara;
import game.entities.AttackType;
import game.entities.Enemy;
import game.entities.Tower;
import game.utils.Constants;

import java.awt.*;
import java.util.List;

public class BirdDefense extends Tower {

    private final Arara anim;

    public BirdDefense() {
        super("Arara",
                Constants.BIRD_HP,
                Constants.BIRD_DAMAGE,
                Constants.BIRD_RANGE,
                AttackType.PROJECTILE);
        this.attackCooldownMax = 40;
        anim = new Arara("public/arara.png", 4);
    }

    @Override
    public void update() {
        super.update();
        anim.update();
    }

    @Override
    public void attack(List<Enemy> enemies) {
        if (!canAttack()) return;
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            double dist = Math.hypot(e.getX() - x, e.getY() - y);
            if (dist <= range) {
                e.takeDamage(damage, attackType);
                resetCooldown();
                break;
            }
        }
    }

    @Override
    public void takeDamage(double damage, AttackType type) {
        applyRawDamage(damage);
    }

    @Override
    public void render(Graphics2D g) {
        anim.render(g, (int) x - anim.getFrameWidth() / 2, (int) y - anim.getFrameHeight() / 2);
    }
}