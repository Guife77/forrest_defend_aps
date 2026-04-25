package game.defenses;

import game.entities.AttackType;
import game.entities.Enemy;
import game.entities.Tower;
import game.utils.Constants;

import java.awt.*;
import java.util.List;

public class TreeDefense extends Tower {

    public TreeDefense() {
        super("Árvore",
                Constants.TREE_HP,
                Constants.TREE_DAMAGE,
                Constants.TREE_RANGE,
                AttackType.PHYSICAL);
        this.attackCooldownMax = 45; // ataca mais rápido que o default
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
    public void render(Graphics2D g) { /* feito pelo TowerRenderer */ }
}