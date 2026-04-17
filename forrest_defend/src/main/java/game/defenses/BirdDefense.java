package game.defenses;

import game.entities.Tower;
import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;
import animations.animation_arara;
import java.awt.Graphics;
import java.util.List;

public class BirdDefense extends Tower {

    private animation_arara anim;


    public BirdDefense() {
        super(
                "Arara",
                Constants.BIRD_HP,
                Constants.BIRD_DAMAGE,
                Constants.BIRD_RANGE,
                AttackType.PROJECTILE
        );
        anim = new animation_arara("public/arara.png");
    }

    @Override
    public void attack(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            double distance = Math.hypot(enemy.getX() - this.x, enemy.getY() - this.y);

            if (distance <= this.range) {
                enemy.takeDamage(this.damage, this.attackType);
                break; // Atira em um inimigo por vez
            }
        }
    }

    @Override
    public void takeDamage(double damage, AttackType attackType) {

        applyRawDamage(damage);
    }

    @Override
    public void update() {
        if (anim != null) anim.update();
    }

    @Override
    public void render(Graphics g) {
        if (anim != null) anim.render(g, (int)this.x, (int)this.y);
    }
}