package game.defenses;

import game.animation.Aranha;
import game.entities.AttackType;
import game.entities.Enemy;
import game.entities.Tower;
import game.utils.Constants;

import java.awt.*;
import java.util.List;

public class SpiderDefense extends Tower {

    private final Aranha anim;

    public SpiderDefense() {
        super("Aranha",
                Constants.SPIDER_HP,
                Constants.SPIDER_DAMAGE,
                Constants.SPIDER_RANGE,
                AttackType.POISON);
        this.attackCooldownMax = 30;
        anim = new Aranha("aranha");
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
                anim.playAttack();
                game.utils.AudioPlayer.play("public/aranha_attack.wav");
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
        anim.render(g, (int) x, (int) y);
    }
}
