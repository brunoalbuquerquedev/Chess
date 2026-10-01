package view;

import chess.ChessMatch;
import controller.GameController;
import controller.GameInterface;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GameInitializer extends JFrame {

    public GameInitializer() throws HeadlessException {
        super("Chess");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Media source (board logo): https://www.flaticon.com/free-icon/chess-board_107617 */
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/resources/chess_logo.png")));
        setIconImage(icon.getImage());

        initGame();

        /* Make the container visible. */
        setVisible(true);
    }

    /**
     * Builds (or rebuilds) the game: creates a new {@link ChessMatch},
     * attaches the menu bar and a fresh {@link GameInterface}, then packs.
     *
     * <p>Calling {@code setJMenuBar()} before {@code pack()} ensures the frame
     * grows to accommodate the menu without shrinking the board panel.</p>
     */
    private void initGame() {
        ChessMatch chessMatch = new ChessMatch();
        GameInterface gameInterface = createGameInterface(chessMatch);

        /* The menu bar must be set before pack() so the JFrame increases its
         * height by the menu height rather than stealing it from the board. */
        GameMenuBar menuBar = new GameMenuBar(
                this::restart,
                gameInterface::repaint
        );
        setJMenuBar(menuBar);

        getContentPane().removeAll();
        getContentPane().add(gameInterface);

        /* Let the frame resize to fit the new content + menu bar. */
        pack();
        centralizeGameWindow();
    }

    /** Resets the game by re-initialising all game objects. */
    private void restart() {
        initGame();
        revalidate();
        repaint();
    }

    private GameInterface createGameInterface(ChessMatch chessMatch) {
        PiecesLoader piecesLoader = new PiecesLoader(
                new ImageIcon[Sizes.getBOARD_SIZE()][Sizes.getBOARD_SIZE()]
        );

        /* Pass the pieces images to the drawer. */
        GameDrawer gameDrawer = new GameDrawer(piecesLoader.getPiecesIcons(), chessMatch);

        GameController gameController = new GameController(chessMatch, gameDrawer);

        /* Make the connection between the user and the board. */
        return new GameInterface(chessMatch, gameDrawer, gameController);
    }

    private void centralizeGameWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }
}
