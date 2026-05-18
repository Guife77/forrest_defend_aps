package game.utils;

/**
 * Relógio do mundo do jogo. Avança ~16ms (1 frame a 60fps) por chamada de tick().
 *
 * A GameEngine chama tick() a cada update(). Como update() é invocado
 * speedMultiplier vezes por frame, o relógio acelera proporcionalmente em 2x/3x
 * — fazendo todas as animações sincronizarem com a velocidade dos inimigos.
 *
 * Substitui System.currentTimeMillis() em todas as animações.
 */
public final class GameClock {

    private static long gameMs = 0;
    private static final long TICK_MS = 1000L / Constants.FPS; // ~16ms

    private GameClock() {}

    /** Avança o relógio um tick. Chamado uma vez por update() da engine. */
    public static void tick() { gameMs += TICK_MS; }

    /** Tempo atual do jogo em ms (acelerado pelo speed multiplier). */
    public static long now() { return gameMs; }

    /** Reinicia o relógio — usado ao reiniciar o jogo. */
    public static void reset() { gameMs = 0; }
}
