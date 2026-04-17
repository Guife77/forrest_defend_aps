package game.defenses;

import game.entities.Tower;
import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;
import java.awt.Graphics;
import java.awt.Color;
import java.util.List;

public class TreeDefense extends Tower {

    public TreeDefense() {
        super(
                "Árvore",
                Constants.TREE_HP,
                Constants.TREE_DAMAGE,
                Constants.TREE_RANGE,
                AttackType.PHYSICAL
        );
    }

    @Override
    public void attack(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            double distance = Math.hypot(enemy.getX() - this.x, enemy.getY() - this.y);

            if (distance <= this.range) {
                // Ataca com dano e tipo físico
                enemy.takeDamage(this.damage, this.attackType);
                break;
            }
        }
    }

    @Override
    public void takeDamage(double damage, AttackType attackType) {
        // Árvore toma dano normal, sem armadura extra
        applyRawDamage(damage);
    }

    @Override
    public void update() {
        // sem animacao
    }

    @Override
    public void render(Graphics g) {

    }
}