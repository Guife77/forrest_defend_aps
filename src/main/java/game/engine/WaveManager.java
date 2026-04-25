package game.engine;

import game.enemies.Excavator;
import game.enemies.Lumberjack;
import game.enemies.Pollution;
import game.entities.Enemy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class WaveManager {

    private int currentWave   = 0;
    private int spawnTick     = 0;
    private int spawnIndex    = 0;
    private final List<Enemy> spawnQueue = new ArrayList<>();
    private boolean waveActive = false;

    private static final int SPAWN_INTERVAL = 90; // ticks entre spawns (~1.5s a 60fps)

    private final int tileSize;

    public WaveManager(int tileSize) {
        this.tileSize = tileSize;
    }

    public void startNextWave() {
        if (waveActive) return;
        currentWave++;
        spawnQueue.clear();
        spawnIndex = 0;
        spawnTick  = SPAWN_INTERVAL; // já começa pronto pra spawnar
        buildWave(currentWave);
        waveActive = true;
        System.out.println("[Wave] Iniciando wave " + currentWave + " com " + spawnQueue.size() + " inimigos");
    }

    private void buildWave(int wave) {
        switch (wave) {
            case 1:
                add(8, "L");
                break;
            case 2:
                add(9, "L"); add(3, "E"); add(6, "L");
                break;
            case 3:
                add(5, "L"); add(5, "P"); add(4, "E"); add(5, "P");
                break;
            default:
                int n = 6 + (wave - 4) * 2;
                for (int i = 0; i < n; i++) {
                    int t = i % 3;
                    if (t == 0) add(1, "L");
                    else if (t == 1) add(1, "P");
                    else add(1, "E");
                }
        }
    }

    private void add(int qty, String type) {
        for (int i = 0; i < qty; i++) {
            switch (type) {
                case "L": spawnQueue.add(new Lumberjack()); break;
                case "E": spawnQueue.add(new Excavator());  break;
                case "P": spawnQueue.add(new Pollution());  break;
            }
        }
    }

    /**
     * Atualiza o wave manager. Retorna lista de inimigos a spawnar neste tick.
     * @param path caminho calculado pelo PathFinder (o spawn usa o primeiro waypoint)
     * @param activeEnemies inimigos ativos no momento
     */
    public List<Enemy> update(List<Point> path, List<Enemy> activeEnemies) {
        List<Enemy> toSpawn = new ArrayList<>();
        if (!waveActive || path == null || path.isEmpty()) return toSpawn;

        spawnTick++;

        if (spawnIndex < spawnQueue.size() && spawnTick >= SPAWN_INTERVAL) {
            spawnTick = 0;
            Enemy e = spawnQueue.get(spawnIndex++);
            // Posiciona na entrada do caminho (primeiro waypoint)
            Point entry = path.get(0);
            e.setPosition(entry.x, entry.y);
            toSpawn.add(e);
            System.out.println("[Wave] Spawnou: " + e.getName() + " em (" + entry.x + "," + entry.y + ")");
        }

        // Onda concluída quando todos foram spawnados e não há mais ninguém vivo
        if (spawnIndex >= spawnQueue.size() && activeEnemies.isEmpty()) {
            waveActive = false;
            System.out.println("[Wave] Wave " + currentWave + " concluida!");
        }

        return toSpawn;
    }

    public boolean isWaveActive()   { return waveActive; }
    public int getCurrentWave()     { return currentWave; }
    public boolean isWaveComplete() { return !waveActive && spawnIndex > 0 && spawnIndex >= spawnQueue.size(); }
}