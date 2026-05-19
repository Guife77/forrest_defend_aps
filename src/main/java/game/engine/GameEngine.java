package game.engine;

import game.defenses.BarrierDefense;
import game.defenses.BirdDefense;
import game.defenses.SpiderDefense;
import game.defenses.TreeDefense;
import game.entities.Enemy;
import game.entities.Player;
import game.entities.Tower;
import game.map.MapRenderer;
import game.renderer.EnemyRenderer;
import game.renderer.TowerRenderer;
import game.ui.CuriosityScreen;
import game.ui.HUD;
import game.utils.Constants;
import game.utils.GameClock;
import game.world.GameMap;
import game.world.MapLoader;
import game.ui.Menu;
import game.utils.AudioPlayer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GameEngine extends MouseAdapter {

    public static final int SCREEN_W = Constants.SCREEN_W;
    public static final int SCREEN_H = Constants.SCREEN_H;
    private static final int TILE    = Constants.TILE_SIZE;

    private Player      player;
    private WaveManager waveManager;
    private List<Enemy> enemies;
    private List<Tower> towers;
    private Menu menu;
    private boolean inMenu;
    private CuriosityScreen curiosity;
    private boolean inCuriosity = false;
    private String curiosityReturn = "menu"; // "menu" | "victory" | "gameover"

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
    private int    speedMultiplier = 1;
    private static final int[] SPEED_CYCLE = {1, 2, 3};
    public GameEngine() { init(); }

    // ── INIT ──────────────────────────────────────────────────────

    private void init() {
        GameClock.reset();
        player         = new Player(Constants.INITIAL_MANA,
                Constants.INITIAL_FOREST_RESOURCES,
                Constants.INITIAL_BASE_HEALTH);
        enemies        = new ArrayList<>();
        towers         = new ArrayList<>();
        barrierTileMap = new LinkedHashMap<>();
        waveManager    = new WaveManager(TILE);
        gameOver       = false;
        victory        = false;
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
        menu          = new Menu();
        curiosity     = new CuriosityScreen();
        inMenu        = true;
        inCuriosity   = false;
    }

    // ── UPDATE ────────────────────────────────────────────────────

    public void update() {
        if (inMenu) return;
        if (gameOver || victory) return;

        // Avança o relógio do mundo (sincroniza animações com speed multiplier)
        GameClock.tick();

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

        if (inCuriosity) {
            curiosity.render(g2, SCREEN_W, SCREEN_H);
            return;
        }

        if (inMenu) {
            menu.render(g2, SCREEN_W, SCREEN_H);
            return;
        }

        mapRenderer.render(g2, map);
        mapRenderer.renderPath(g2, path);
        towerRenderer.render(g2, towers, showRanges);
        enemyRenderer.render(g2, enemies);
        hud.render(g2, player, waveManager, SCREEN_W, SCREEN_H, selectedTower, showRanges, speedMultiplier);

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
        if (victory)  hud.renderVictory(g2, SCREEN_W, SCREEN_H,
                                        waveManager.getCurrentWave(),
                                        player.getForestResources());
    }

    // ── TECLADO ───────────────────────────────────────────────────

    public void onKeyPressed(int keyCode) {
        // Tela de curiosidades captura todas as teclas
        if (inCuriosity) {
            switch (keyCode) {
                case KeyEvent.VK_LEFT:  curiosity.prevPage(); break;
                case KeyEvent.VK_RIGHT: curiosity.nextPage(); break;
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_BACK_SPACE:
                    inCuriosity = false;
                    break;
            }
            return;
        }

        if (inMenu) {
            if (keyCode == KeyEvent.VK_ENTER) {
                inMenu = false;
                // Música continua tocando durante o jogo
            } else if (keyCode == KeyEvent.VK_C) {
                openCuriosity("menu");
            }
            return;
        }

        if ((gameOver || victory) && keyCode == KeyEvent.VK_C) {
            openCuriosity(victory ? "victory" : "gameover");
            return;
        }

        switch (keyCode) {
            case KeyEvent.VK_SPACE:
                if (!gameOver && !victory && !waveManager.isWaveActive())
                    waveManager.startNextWave();
                break;
            case KeyEvent.VK_T: selectedTower = 'T'; break;
            case KeyEvent.VK_A: selectedTower = 'A'; break;
            case KeyEvent.VK_S: selectedTower = 'S'; break;
            case KeyEvent.VK_B: selectedTower = 'B'; break;
            case KeyEvent.VK_2: cycleSpeed(); break;
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_EQUALS:
            case KeyEvent.VK_ADD:
                AudioPlayer.adjustMusicVolume(0.1f);
                showFeedback("Volume: " + Math.round(AudioPlayer.getRawMusicVolume() * 100) + "%");
                break;
            case KeyEvent.VK_MINUS:
            case KeyEvent.VK_SUBTRACT:
                AudioPlayer.adjustMusicVolume(-0.1f);
                showFeedback("Volume: " + Math.round(AudioPlayer.getRawMusicVolume() * 100) + "%");
                break;
            case KeyEvent.VK_M:
                AudioPlayer.toggleMute();
                showFeedback(AudioPlayer.isMuted() ? "Som mutado" : "Som ligado");
                break;
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
        handleClickAt(e.getX(), e.getY(), e.getButton());
    }

    /** Entrada lógica de clique — coords já em espaço lógico (pós-inversão de escala). */
    public void handleClickAt(int lx, int ly, int button) {
        if (inCuriosity) {
            String action = curiosity.hitTest(lx, ly, SCREEN_W, SCREEN_H);
            if ("prev".equals(action)) curiosity.prevPage();
            else if ("next".equals(action)) curiosity.nextPage();
            else if ("back".equals(action)) inCuriosity = false;
            return;
        }
        if (inMenu || gameOver || victory) return;

        // 1. Clique nos botões de volume
        String volAction = hud.hitTestVolume(lx, ly);
        if (volAction != null) {
            switch (volAction) {
                case "vol_up":   AudioPlayer.adjustMusicVolume(0.1f); break;
                case "vol_down": AudioPlayer.adjustMusicVolume(-0.1f); break;
                case "vol_mute": AudioPlayer.toggleMute(); break;
            }
            return;
        }

        // 2. Clique em card do HUD seleciona a torre correspondente
        char card = hud.hitTestTowerCard(lx, ly, SCREEN_W, SCREEN_H);
        if (card != 0) {
            selectedTower = card;
            return;
        }

        // 2. Clique fora do HUD = tentativa de construir no mapa
        if (ly >= SCREEN_H - HUD.PANEL_H) return; // dentro do painel mas fora dos cards

        int col = lx / TILE;
        int row = ly / TILE;
        if (row >= map.getRows() || col >= map.getCols()) return;

        var tile = map.getTile(row, col);
        if (tile == null) return;
        var tileType = tile.getType();

        // Torres normais só em GRASS
        if (selectedTower != 'B' && tileType != game.world.enums.TileType.GRASS) {
            showFeedback("Torres só podem ser construídas na grama!");
            return;
        }
        // Barreira tem que ir no CAMINHO (PATH) — é onde ela trava os inimigos
        if (selectedTower == 'B' && tileType != game.world.enums.TileType.PATH) {
            showFeedback("Barreira só pode ser colocada no caminho!");
            return;
        }

        double px = col * TILE + TILE / 2.0;
        double py = row * TILE + TILE / 2.0;

        // Checa sobreposição com torre existente
        for (Tower t : towers)
            if (Math.abs(t.getX() - px) < TILE / 2.0 && Math.abs(t.getY() - py) < TILE / 2.0) return;

        Tower nova = buildTower(selectedTower, px, py);
        if (nova == null) return;

        towers.add(nova);

        AudioPlayer.play("public/construir.wav");

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
            case 'S': cost = Constants.COST_SPIDER;  t = new SpiderDefense();  break;
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

    public int getSpeedMultiplier() { return speedMultiplier; }

    private void openCuriosity(String returnTo) {
        inCuriosity = true;
        curiosityReturn = returnTo;
        curiosity.reset();
    }

    private void cycleSpeed() {
        int idx = 0;
        for (int i = 0; i < SPEED_CYCLE.length; i++) {
            if (SPEED_CYCLE[i] == speedMultiplier) { idx = i; break; }
        }
        speedMultiplier = SPEED_CYCLE[(idx + 1) % SPEED_CYCLE.length];
        showFeedback("Velocidade: " + speedMultiplier + "x");
    }
}