package view;

import java.awt.*;

/**
 * Defines the color themes available for the chess board.
 * The active theme can be changed at runtime via {@link #setTheme(Theme)}.
 */
public class GameColors {

    /** Available board color themes. */
    public enum Theme {
        CLASSIC("Clássico"),
        OCEAN("Oceano"),
        FOREST("Floresta"),
        CORAL("Coral");

        private final String displayName;

        Theme(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // ── Classic (original brown) ──────────────────────────────────────────
    private static final Color CLASSIC_LIGHT = new Color(215, 195, 155);
    private static final Color CLASSIC_DARK  = new Color(139, 69, 19, 220);

    // ── Ocean ─────────────────────────────────────────────────────────────
    private static final Color OCEAN_LIGHT = new Color(173, 216, 230);
    private static final Color OCEAN_DARK  = new Color(30, 80, 140);

    // ── Forest ────────────────────────────────────────────────────────────
    private static final Color FOREST_LIGHT = new Color(200, 230, 180);
    private static final Color FOREST_DARK  = new Color(50, 100, 50);

    // ── Coral ─────────────────────────────────────────────────────────────
    private static final Color CORAL_LIGHT = new Color(255, 220, 200);
    private static final Color CORAL_DARK  = new Color(180, 70, 50);

    // ── Highlights ────────────────────────────────────────────────────────
    private static final Color HIGHLIGHTS_COLOR = new Color(190, 255, 255, 200);

    // ── Active theme ──────────────────────────────────────────────────────
    private static Theme currentTheme = Theme.CLASSIC;

    private GameColors() {}

    /** Returns the current active theme. */
    public static Theme getTheme() {
        return currentTheme;
    }

    /** Changes the active theme; call {@code repaint()} on the board afterwards. */
    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }

    /** Light square color for the current theme. */
    public static Color getLight() {
        return switch (currentTheme) {
            case OCEAN  -> OCEAN_LIGHT;
            case FOREST -> FOREST_LIGHT;
            case CORAL  -> CORAL_LIGHT;
            default     -> CLASSIC_LIGHT;
        };
    }

    /** Dark square color for the current theme. */
    public static Color getDark() {
        return switch (currentTheme) {
            case OCEAN  -> OCEAN_DARK;
            case FOREST -> FOREST_DARK;
            case CORAL  -> CORAL_DARK;
            default     -> CLASSIC_DARK;
        };
    }

    /** Highlight color used to show possible moves. */
    public static Color getHighlights() {
        return HIGHLIGHTS_COLOR;
    }

    // ── Legacy constants kept for backward compatibility ──────────────────
    /** @deprecated Use {@link #getLight()} instead. */
    @Deprecated
    public static final Color BLACK = CLASSIC_LIGHT;

    /** @deprecated Use {@link #getDark()} instead. */
    @Deprecated
    public static final Color WHITE = CLASSIC_DARK;

    /** @deprecated Use {@link #getHighlights()} instead. */
    @Deprecated
    public static final Color HIGHLIGHTS = HIGHLIGHTS_COLOR;
}
