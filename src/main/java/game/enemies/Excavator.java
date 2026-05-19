package game.enemies;

import game.animation.Escavadeira;
import game.entities.AttackType;
import game.entities.Enemy;
import game.utils.Constants;
import java.awt.*;

public class Excavator extends Enemy {

    private static final double PHYSICAL_RESISTANCE = 0.5;
    private final Escavadeira anim; // Mantém a nossa animação protegida aqui

    // ── NOVAS VARIÁVEIS PARA DETECTAR DIREÇÃO ──
    // Guardam a posição do inimigo no quadro (render) anterior.
    private double lastX = -1;
    private double lastY = -1;

    public Excavator() {
        super("Excavator",
                Constants.EXCAVATOR_HP,
                Constants.EXCAVATOR_SPEED,
                Constants.EXCAVATOR_DAMAGE_TO_BASE,
                Constants.EXCAVATOR_REWARD_RF);
        
        // Inicializa apontando para os seus arquivos com o nome base "escavadeira"
        this.anim = new game.animation.Escavadeira("escavadeira", 3);
    }

    @Override
    public void takeDamage(double damage, AttackType attackType) {
        double finalDamage = damage;
        if (attackType == AttackType.PHYSICAL || attackType == AttackType.PROJECTILE) {
            finalDamage *= (1.0 - PHYSICAL_RESISTANCE);
        }
        applyRawDamage(finalDamage);
    }

    // O próprio render se encarrega de atualizar os frames com segurança
    public void render(Graphics2D g) {
        
        // ── 1. INICIALIZAÇÃO NO PRIMEIRO QUADRO ──
        if (lastX == -1 && lastY == -1) {
            lastX = x;
            lastY = y;
            // Se for o primeiro render, apenas guarda a posição e pula
            // para não detectar uma direção falsa no nascimento.
            anim.update();
            anim.render(g, (int) x, (int) y);
            return; 
        }

        // ── 2. CALCULA A DIFERENÇA DE MOVIMENTO (Velocidade do quadro) ──
        double dx = x - lastX;
        double dy = y - lastY;

        // ── 3. DETECTA A DIREÇÃO DO MOVIMENTO E MUDA A ANIMAÇÃO ATIVA ──
        if (Math.abs(dx) > Math.abs(dy)) {
            // O movimento horizontal é mais forte. É para a esquerda ou direita.
            if (dx > 0) {
                anim.setDirection("direita");
            } else if (dx < 0) {
                anim.setDirection("esquerda");
            }
        } else {
            // O movimento vertical é mais forte. É para cima ou baixo.
            if (dy > 0) {
                anim.setDirection("baixo");
            } else if (dy < 0) {
                anim.setDirection("cima");
            }
        }

        // ── 4. ATUALIZA E DESENHA O SPRITE ANIMAÇÃO ──
        anim.update(); // Faz a esteira rodar a cada frame renderizado
        anim.render(g, (int) x, (int) y); // Desenha a imagem na tela usando o novo render proporcional

        // ── 5. GUARDA A POSIÇÃO ATUAL PARA O PRÓXIMO QUADRO ──
        lastX = x;
        lastY = y;
    }
}