package game.engine;

import game.defenses.BarrierDefense;
import game.defenses.BirdDefense;
import game.defenses.TreeDefense;
import game.entities.Enemy;
import game.entities.Player;
import game.entities.Tower;
import game.map.MapRenderer;
import game.map.PathFinder;
import game.renderer.EnemyRenderer;
import game.renderer.TowerRenderer;
import game.ui.HUD;
import game.utils.Constants;
import game.world.GameMap;
import game.world.MapLoader;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GameEngine extends MouseAdapter {

    public static final int SCREEN_W = 800;
    public static final int SCREEN_H = 600;
    private static final int TILE    = Constants.TILE_SIZE;

    private Player      player;
    private WaveManager waveManager;
    private List<Enemy> enemies;
    private List<Tower> towers;

    // Caminho que os inimigos seguem (waypoints do MapLoader — fixo)
    private List<Point> path;

    private GameMap map;

    // Mapa de barreira → tile (col,row) que ela ocupa no PATH
    // Quando a barreira morre, o tile é liberado
    private Map<Tower, Point> barrierTileMap;

    private MapRenderer   mapRenderer;
    private EnemyRenderer enemyRenderer;
    private TowerRenderer towerRenderer;
    private HUD           hud;

    private char    selectedTower = 'T';
    private boolean showRanges    = false;
    private boolean gameOver      = false;
    private boolean victory       = false;

    private String feedbackMsg   = null;
    private int    feedbackTicks = 0;
    private int    manaTick      = 0;

    public GameEngine() { init(); }

    // ── INIT ──────────────────────────────────────────────────────

    private void init() {
        player         = new Player(Constants.INITIAL_MANA,
                Constants.INITIAL_FOREST_RESOURCES,
                Constants.INITIAL_BASE_HEALTH);
        enemies        = new ArrayList<>();
        towers         = new ArrayList<>();
        barrierTileMap = new LinkedHashMap<>();
        waveManager    = new WaveManager(TILE);
        gameOver       = false;
        victory        = false;
        manaTick       = 0;
        feedbackMsg    = null;

        int cols = SCREEN_W / TILE;
        int rows = (SCREEN_H - HUD.PANEL_H) / TILE;
        map = new GameMap(rows, cols);
        new MapLoader().load(map);

        // Caminho fixo definido pelo MapLoader — sem BFS ambíguo
        path = MapLoader.getWaypoints(TILE);

        mapRenderer   = new MapRenderer(TILE);
        enemyRenderer = new EnemyRenderer();
        towerRenderer = new TowerRenderer();
        hud           = new HUD();
    }

    // ── UPDATE ────────────────────────────────────────────────────

    public void update() {
        if (gameOver || victory) return;

        if (++manaTick >= Constants.FPS) {
            player.addMana(Constants.MANA_REGEN_PER_SECOND);
            manaTick = 0;
        }

        if (feedbackTicks > 0) feedbackTicks--;
        else feedbackMsg = null;

        // Spawn de novos inimigos
        List<Enemy> novos = waveManager.update(path, enemies);
        enemies.addAll(novos);

        // Move inimigos — passando lista de torres para detectar barreiras
        for (Enemy e : enemies)
            if (e.isAlive()) e.moveAlongPath(path, player, towers);

        // Torres ativas atacam inimigos normalmente
        for (Tower t : towers)
            if (t.isAlive()) { t.update(); t.attack(enemies); }

        // Remove inimigos mortos
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            if (!e.isAlive()) {
                if (!e.hasReachedBase()) e.onDeath(player);
                it.remove();
            }
        }

        // Verifica barreiras destruídas e remove do mapa de bloqueio
        checkDestroyedBarriers();

        // Remove torres mortas
        towers.removeIf(t -> !t.isAlive());

        if (!player.isBaseAlive()) { gameOver = true; return; }

        if (waveManager.getCurrentWave() >= 3
                && waveManager.isWaveComplete()
                && enemies.isEmpty()) {
            victory = true;
        }
    }

    /**
     * Quando uma barreira é destruída:
     * - Remove do barrierTileMap
     * - Reseta pathIndex dos inimigos (caminho volta ao normal)
     */
    private void checkDestroyedBarriers() {
        Iterator<Map.Entry<Tower, Point>> it = barrierTileMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Tower, Point> entry = it.next();
            if (!entry.getKey().isAlive()) {
                it.remove();
                // Reinicia pathIndex dos inimigos para retomarem movimento
                for (Enemy e : enemies)
                    if (e.isAlive()) e.resetPathIndex(path);
            }
        }
    }

    // ── RENDER ────────────────────────────────────────────────────

    public void render(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        mapRenderer.render(g2, map);
        mapRenderer.renderPath(g2, path);
        towerRenderer.render(g2, towers, showRanges);
        enemyRenderer.render(g2, enemies);
        hud.render(g2, player, waveManager, SCREEN_W, SCREEN_H, selectedTower, showRanges);

        if (feedbackMsg != null) {
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int msgW = fm.stringWidth(feedbackMsg);
            int mx = (SCREEN_W - msgW) / 2;
            int my = SCREEN_H - HUD.PANEL_H - 20;
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(mx - 10, my - 18, msgW + 20, 26, 8, 8);
            g2.setColor(new Color(255, 80, 80));
            g2.drawString(feedbackMsg, mx, my);
        }

        if (gameOver) hud.renderGameOver(g2, SCREEN_W, SCREEN_H, waveManager.getCurrentWave());
        if (victory)  hud.renderVictory(g2, SCREEN_W, SCREEN_H);
    }

    // ── TECLADO ───────────────────────────────────────────────────

    public void onKeyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SPACE:
                if (!gameOver && !victory && !waveManager.isWaveActive())
                    waveManager.startNextWave();
                break;
            case KeyEvent.VK_T: selectedTower = 'T'; break;
            case KeyEvent.VK_A: selectedTower = 'A'; break;
            case KeyEvent.VK_B: selectedTower = 'B'; break;
            case KeyEvent.VK_R:
                if (gameOver || victory) init();
                else showRanges = !showRanges;
                break;
            case KeyEvent.VK_ESCAPE: System.exit(0);
        }
    }

    // ── MOUSE ─────────────────────────────────────────────────────

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver || victory) return;

        int col = e.getX() / TILE;
        int row = e.getY() / TILE;
        if (row >= map.getRows() || col >= map.getCols()) return;

        var tile = map.getTile(row, col);
        if (tile == null) return;
        var tileType = tile.getType();

        // Torres normais só em GRASS
        if (selectedTower != 'B' && tileType != game.world.enums.TileType.GRASS) return;
        // Barreira não pode ir na BASE
        if (selectedTower == 'B' && tileType == game.world.enums.TileType.BASE) return;

        double px = col * TILE + TILE / 2.0;
        double py = row * TILE + TILE / 2.0;

        // Checa sobreposição com torre existente
        for (Tower t : towers)
            if (Math.abs(t.getX() - px) < TILE / 2.0 && Math.abs(t.getY() - py) < TILE / 2.0) return;

        Tower nova = buildTower(selectedTower, px, py);
        if (nova == null) return;

        towers.add(nova);

        // Registra o tile que a barreira ocupa no PATH
        if (nova instanceof BarrierDefense && tileType == game.world.enums.TileType.PATH) {
            barrierTileMap.put(nova, new Point(col, row));
        }
    }

    private Tower buildTower(char type, double px, double py) {
        int cost;
        Tower t;
        switch (type) {
            case 'T': cost = Constants.COST_TREE;    t = new TreeDefense();    break;
            case 'A': cost = Constants.COST_BIRD;    t = new BirdDefense();    break;
            case 'B': cost = Constants.COST_BARRIER; t = new BarrierDefense(); break;
            default: return null;
        }
        if (player.getForestResources() < cost) {
            showFeedback("RF insuficiente! Precisa de " + cost + " RF.");
            return null;
        }
        player.addForestResources(-cost);
        t.setPosition(px, py);
        return t;
    }

    private void showFeedback(String msg) {
        feedbackMsg   = msg;
        feedbackTicks = 180;
    }
}