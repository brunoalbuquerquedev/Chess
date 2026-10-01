package view;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Menu bar for the Chess game.
 *
 * <p>Menus:
 * <ul>
 *   <li><b>Jogo</b> — Reiniciar</li>
 *   <li><b>Cores do Tabuleiro</b> — one item per {@link GameColors.Theme}</li>
 * </ul>
 *
 * <p>The restart logic and board repaint are injected via {@link Runnable} /
 * {@link Runnable} callbacks so this class has no dependency on {@link GameInitializer}.
 */
public class GameMenuBar extends JMenuBar {

    /**
     * @param onRestart  called when the user clicks "Reiniciar"
     * @param onRepaint  called after a theme change so the board repaints
     */
    public GameMenuBar(Runnable onRestart, Runnable onRepaint) {
        add(buildGameMenu(onRestart));
        add(buildColorsMenu(onRepaint));
    }

    // ── Jogo ──────────────────────────────────────────────────────────────

    private JMenu buildGameMenu(Runnable onRestart) {
        JMenu menu = new JMenu("Jogo");

        JMenuItem restart = new JMenuItem("Reiniciar");
        restart.addActionListener(e -> onRestart.run());
        menu.add(restart);

        return menu;
    }

    // ── Cores do Tabuleiro ────────────────────────────────────────────────

    private JMenu buildColorsMenu(Runnable onRepaint) {
        JMenu menu = new JMenu("Cores do Tabuleiro");

        ButtonGroup group = new ButtonGroup();

        for (GameColors.Theme theme : GameColors.Theme.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(theme.getDisplayName());
            item.setSelected(theme == GameColors.getTheme());

            item.addActionListener(e -> {
                GameColors.setTheme(theme);
                onRepaint.run();
            });

            group.add(item);
            menu.add(item);
        }

        return menu;
    }
}
