package game.defenses;

import game.animation.Acacu;
import game.entities.AttackType;
import game.entities.Enemy;
import game.entities.Tower;
import game.utils.Constants;

import java.awt.*;
import java.util.List;

public class TreeDefense extends Tower {

    private final Acacu anim;

    public TreeDefense() {
        super("Açaçu",
                Constants.TREE_HP,
                Constants.TREE_DAMAGE,
                Constants.TREE_RANGE,
                AttackType.PHYSICAL);
        this.attackCooldownMax = 45;
        this.anim = new Acacu();
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
                game.utils.AudioPlayer.play("public/arvore_attack.wav");
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
