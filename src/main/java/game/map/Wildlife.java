package game.map;

import game.world.GameMap;
import game.world.Tile;
import game.world.enums.TileType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fauna animada do mapa: borboletas e pássaros sobrevoando.
 * Movimento puramente visual — não interfere no gameplay.
 *
 * As borboletas têm voo em curvas suaves; os pássaros atravessam o mapa em linha
 * reta com leve oscilação vertical de asas.
 */
public final class Wildlife {

    private static final int NUM_BUTTERFLIES = 8;
    private static final int NUM_BIRDS = 3;

    private final List<Butterfly> butterflies = new ArrayList<>();
    private final List<Bird> birds = new ArrayList<>();
    private final Random rng = new Random(99);
    private final int worldW;
    private final int worldH;
    private long lastUpdate = System.currentTimeMillis();

    public Wildlife(GameMap map, int tileSize) {
        this.worldW = map.getCols() * tileSize;
        this.worldH = map.getRows() * tileSize;

        for (int i = 0; i < NUM_BUTTERFLIES; i++) {
            butterflies.add(spawnButterfly(map, tileSize));
        }
        for (int i = 0; i < NUM_BIRDS; i++) {
            birds.add(spawnBird());
        }
    }

    private Butterfly spawnButterfly(GameMap map, int tileSize) {
        // Borboletas pairam sobre tiles de grama
        int rows = map.getRows();
        int cols = map.getCols();
        for (int tries = 0; tries < 20; tries++) {
            int r = rng.nextInt(rows);
            int c = rng.nextInt(cols);
            Tile t = map.getTile(r, c);
            if (t != null && t.getType() == TileType.GRASS) {
                Butterfly b = new Butterfly();
                b.x = c * tileSize + tileSize / 2f;
                b.y = r * tileSize + tileSize / 2f;
                b.phase = rng.nextFloat() * (float) Math.PI * 2f;
                b.color = randomButterflyColor();
                b.radius = 18 + rng.nextFloat() * 14f;
                b.speed = 1.2f + rng.nextFloat() * 0.8f;
                return b;
            }
        }
        Butterfly b = new Butterfly();
        b.x = worldW / 2f;
        b.y = worldH / 2f;
        b.color = randomButterflyColor();
        b.radius = 24f;
        b.speed = 1.5f;
        return b;
    }

    private Color randomButterflyColor() {
        Color[] palette = {
                new Color(255, 130, 70),
                new Color(110, 180, 255),
                new Color(240, 220, 90),
                new Color(220, 100, 200),
                new Color(180, 220, 100)
        };
        return palette[rng.nextInt(palette.length)];
    }

    private Bird spawnBird() {
        Bird b = new Bird();
        boolean fromLeft = rng.nextBoolean();
        b.x = fromLeft ? -30 : worldW + 30;
        b.y = 50 + rng.nextFloat() * (worldH * 0.4f);
        b.vx = (fromLeft ? 1 : -1) * (1.4f + rng.nextFloat() * 0.8f);
        b.phase = rng.nextFloat() * (float) Math.PI * 2f;
        b.size = 6 + rng.nextInt(3);
        return b;
    }

    public void update() {
        long now = System.currentTimeMillis();
        float dt = Math.min(0.05f, (now - lastUpdate) / 1000f);
        lastUpdate = now;

        for (Butterfly b : butterflies) {
            b.phase += dt * 3f;
            // Movimento orbital com deriva — figura de 8 suave
            float orbitX = (float) Math.cos(b.phase) * b.radius * dt * b.speed;
            float orbitY = (float) Math.sin(b.phase * 2) * b.radius * dt * b.speed * 0.5f;
            b.x += orbitX;
            b.y += orbitY;
            // Mantém dentro do mapa
            if (b.x < 10) b.x = 10;
            if (b.x > worldW - 10) b.x = worldW - 10;
            if (b.y < 10) b.y = 10;
            if (b.y > worldH - 10) b.y = worldH - 10;
            b.wing = (float) Math.sin(b.phase * 6);
        }

        for (Bird b : birds) {
            b.x += b.vx;
            b.phase += dt * 8f;
            b.wing = (float) Math.sin(b.phase);
            // Pequena oscilação vertical
            b.y += (float) Math.sin(b.phase * 0.3) * 0.4f;
            if (b.vx > 0 && b.x > worldW + 30) respawnBird(b, false);
            if (b.vx < 0 && b.x < -30) respawnBird(b, true);
        }
    }

    private void respawnBird(Bird b, boolean fromRight) {
        b.x = fromRight ? worldW + 30 : -30;
        b.y = 50 + rng.nextFloat() * (worldH * 0.4f);
        b.vx = (fromRight ? -1 : 1) * (1.4f + rng.nextFloat() * 0.8f);
    }

    public void render(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Butterfly b : butterflies) drawButterfly(g2, b);
        for (Bird bird : birds) drawBird(g2, bird);

        g2.dispose();
    }

    private void drawButterfly(Graphics2D g, Butterfly b) {
        int x = (int) b.x;
        int y = (int) b.y;
        // Sombra
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(x - 4, y + 6, 8, 3);

        // Asas (escala vertical conforme batida)
        int wingW = 7;
        int wingH = 4 + (int) (Math.abs(b.wing) * 3);
        g.setColor(b.color);
        g.fillOval(x - wingW - 1, y - wingH, wingW, wingH);
        g.fillOval(x + 1, y - wingH, wingW, wingH);
        g.fillOval(x - wingW - 1, y, wingW - 2, wingH - 1);
        g.fillOval(x + 1, y, wingW - 2, wingH - 1);

        // Corpo
        g.setColor(Color.BLACK);
        g.fillRect(x - 1, y - 3, 2, 6);
    }

    private void drawBird(Graphics2D g, Bird b) {
        int x = (int) b.x;
        int y = (int) b.y;
        int s = b.size;

        // Asas em "M" achatado pela fase
        int wingY = (int) (b.wing * s * 0.6f);
        g.setColor(new Color(35, 35, 35));
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x - s, y - wingY, x, y);
        g.drawLine(x, y, x + s, y - wingY);
        g.setStroke(new BasicStroke(1));
    }

    // ── Tipos internos ─────────────────────────────────────

    private static class Butterfly {
        float x, y, phase, radius, speed, wing;
        Color color;
    }

    private static class Bird {
        float x, y, vx, phase, wing;
        int size;
    }
}
