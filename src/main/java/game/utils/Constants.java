package game.utils;

public final class Constants {

    private Constants() {}

    public static final int TILE_SIZE = 32;
    public static final int FPS = 60;

    public static final int INITIAL_MANA           = 100;
    public static final int INITIAL_FOREST_RESOURCES = 150;
    public static final int INITIAL_BASE_HEALTH    = 100;
    public static final int MANA_REGEN_PER_SECOND  = 5;

    // Inimigos
    public static final double LUMBERJACK_HP           = 80.0;
    public static final double LUMBERJACK_SPEED        = 1.2;
    public static final int    LUMBERJACK_DAMAGE_TO_BASE = 10;
    public static final int    LUMBERJACK_REWARD_RF    = 20;

    public static final double EXCAVATOR_HP            = 300.0;
    public static final double EXCAVATOR_SPEED         = 0.5;
    public static final int    EXCAVATOR_DAMAGE_TO_BASE = 80;
    public static final int    EXCAVATOR_REWARD_RF     = 60;

    public static final double POLLUTION_HP            = 120.0;
    public static final double POLLUTION_SPEED         = 1.8;
    public static final int    POLLUTION_DAMAGE_TO_BASE = 15;
    public static final int    POLLUTION_REWARD_RF     = 35;

    // Defesas
    public static final double BIRD_HP     = 50.0;
    public static final int    BIRD_DAMAGE = 15;
    public static final double BIRD_RANGE  = 120.0;

    public static final double TREE_HP     = 100.0;
    public static final int    TREE_DAMAGE = 10;
    public static final double TREE_RANGE  = 100.0;

    public static final double BARRIER_HP    = 500.0;
    public static final int    BARRIER_DAMAGE = 0;
    public static final double BARRIER_RANGE  = 0.0;

    // Custo em RF para construir
    public static final int COST_TREE    = 50;
    public static final int COST_BIRD    = 80;
    public static final int COST_BARRIER = 150;
}