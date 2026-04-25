package game.entities;

import java.awt.Point;
import java.util.List;

public abstract class Enemy {

    protected String  name;
    protected double  maxHp;
    protected double  hp;
    protected double  speed;
    protected int     damageToBase;
    protected int     rewardRF;
    protected double  x, y;
    protected boolean alive;
    protected boolean reachedBase;

    // Waypoint atual no path
    private int pathIndex = 0;

    // Ataque do inimigo à barreira
    private int     attackCooldown    = 0;
    private static final int ATTACK_INTERVAL = 60; // 1 ataque/segundo

    public Enemy(String name, double maxHp, double speed, int damageToBase, int rewardRF) {
        this.name         = name;
        this.maxHp        = maxHp;
        this.hp           = maxHp;
        this.speed        = speed;
        this.damageToBase = damageToBase;
        this.rewardRF     = rewardRF;
        this.alive        = true;
        this.reachedBase  = false;
    }

    /**
     * Move o inimigo ao longo do path.
     * Se encontrar uma barreira no próximo waypoint, para e ataca ela.
     *
     * @param barriers lista de torres — inimigo verifica se alguma está bloqueando o caminho
     */
    public void moveAlongPath(List<Point> path, Player player, List<Tower> barriers) {
        if (path == null || path.isEmpty() || !alive) return;
        if (attackCooldown > 0) attackCooldown--;

        if (pathIndex >= path.size()) {
            reachBase(player);
            return;
        }

        // Verifica se há barreira bloqueando o waypoint atual
        Tower blocker = getBlockerAt(path.get(pathIndex), barriers);
        if (blocker != null) {
            // Para e ataca a barreira
            if (attackCooldown <= 0) {
                blocker.takeDamage(damageToBase, AttackType.PHYSICAL);
                attackCooldown = ATTACK_INTERVAL;
            }
            return; // não move enquanto há barreira no caminho
        }

        // Sem bloqueio — move normalmente
        Point target = path.get(pathIndex);
        double dx   = target.x - x;
        double dy   = target.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= speed) {
            x = target.x;
            y = target.y;
            pathIndex++;
            if (pathIndex >= path.size()) reachBase(player);
        } else {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
    }

    /** Retorna a barreira viva que ocupa aproximadamente o ponto p, ou null */
    private Tower getBlockerAt(Point p, List<Tower> barriers) {
        if (barriers == null) return null;
        for (Tower t : barriers) {
            if (!t.isAlive()) continue;
            if (!(t instanceof game.defenses.BarrierDefense)) continue;
            double dx = t.getX() - p.x;
            double dy = t.getY() - p.y;
            if (Math.sqrt(dx * dx + dy * dy) < 20) return t;
        }
        return null;
    }

    /**
     * Chamado quando o path muda (barreira colocada ou removida).
     * Encontra o waypoint mais próximo da posição atual.
     */
    public void resetPathIndex(List<Point> newPath) {
        if (newPath == null || newPath.isEmpty()) return;
        double best = Double.MAX_VALUE;
        int    idx  = 0;
        for (int i = 0; i < newPath.size(); i++) {
            Point p = newPath.get(i);
            double d = Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2);
            if (d < best) { best = d; idx = i; }
        }
        pathIndex = Math.min(idx + 1, newPath.size() - 1);
    }

    public abstract void takeDamage(double damage, AttackType attackType);

    public void onDeath(Player player) {
        if (!alive) return;
        alive = false;
        if (player != null) player.addForestResources(rewardRF);
    }

    public void reachBase(Player player) {
        if (!alive || reachedBase) return;
        reachedBase = true;
        alive = false;
        if (player != null) player.takeBaseDamage(damageToBase);
    }

    public boolean isAlive()        { return alive && hp > 0; }
    public boolean hasReachedBase() { return reachedBase; }

    protected void applyRawDamage(double damage) {
        if (!alive || damage <= 0) return;
        hp -= damage;
        if (hp <= 0) hp = 0;
    }

    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public String getName()      { return name; }
    public double getMaxHp()     { return maxHp; }
    public double getHp()        { return hp; }
    public double getSpeed()     { return speed; }
    public int getDamageToBase() { return damageToBase; }
    public int getRewardRF()     { return rewardRF; }
    public double getX()         { return x; }
    public double getY()         { return y; }
}