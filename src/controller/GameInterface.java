package controller;

import view.GameColors;
import view.GameDrawer;
import view.Sizes;
import boardgame.Piece;
import boardgame.Position;
import chess.ChessMatch;
import pieces.King;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.Optional;

public class GameInterface extends JPanel {

    private final ChessMatch match;
    private final GameDrawer gameDrawer;
    private final GameController gameController;
    private final Runnable restart;

    public GameInterface(ChessMatch match, GameDrawer gameDrawer,
                         GameController gameController, Runnable restart) {
        super();
        this.match = match;
        this.gameDrawer = gameDrawer;
        this.gameController = gameController;
        this.restart = restart;

        /* Set the game window size. */
        setPreferredSize(new Dimension(Sizes.getDimension(), Sizes.getDimension()));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                /* Get the coordinates of the click (in number of pixels). */
                int x = e.getX();
                int y = e.getY();

                /* Divide the coordinate by the size of the tile to get
                the square of the clicked area. */
                int row = x / Sizes.getTileSize();
                int col = y / Sizes.getTileSize();

                /* Make sure the row or column values are not less than 0 or
                greater than 7. */
                row = Util.valueWithinBoardLimits(row);
                col = Util.valueWithinBoardLimits(col);

                /* Set the values of the coordinates in MouseActions. */
                gameController.handleScreenSelection(row, col);

                /* When the player clicks on the board, repaint the board
                to highlight the moves. */
                if (Objects.nonNull(gameController.getaX())
                        && Objects.nonNull(gameController.getaY())) {
                    repaint();
                }

                if (gameController.isAnyCoordinateNull())
                    return;

                gameController.controllerActions();

                /* Always repaint the interface and clean the coordinates after
                a move to remove the selected piece highlights from the board
                and clean the coordinates. */
                repaint();

                if (match.isCheckmate()) {
                    String winner = match.getWinner() == chess.ChessColor.WHITE
                            ? "White" : "Black";
                    int choice = JOptionPane.showConfirmDialog(
                            GameInterface.this,
                            "Checkmate! " + winner + " wins.\n\nStart a new game?",
                            "Chess — Game Over",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        restart.run();
                    } else {
                        System.exit(0);
                    }
                    return;
                }

                if (match.isStalemate()) {
                    int choice = JOptionPane.showConfirmDialog(
                            GameInterface.this,
                            "It's a stalemate. It's a draw!\n\nStart a new game?",
                            "Chess — Stalemate",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        restart.run();
                    } else {
                        System.exit(0);
                    }
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /* By default, the background color of the panel is white, so I changed it
        to black because of the square edges.
        It looks better with a dark color. */
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        /* Divide the panel in a matrix of 8x8 squares, switching between two colors,
        representing the black and white from the classical chess board.
        The size of the tiles is calculated based on the frame (screen) size. */
        for (int row = 0; row < Sizes.getBOARD_SIZE(); row++) {
            for (int col = 0; col < Sizes.getBOARD_SIZE(); col++) {
                g.setColor(Util.isEven(row + col)
                        ? GameColors.getLight() : GameColors.getDark());

                /*
                 * This method will create the squares on the panel.
                 * The x and y values are for the left and right, the value I added
                 * is so the border can be seen.
                 * You add one more to the sides and one less to the width and height,
                 * so you have a slightly smaller tile and a "border" appearing
                 * around the tile.
                 * The size of the small tile had to be changed to be larger as the
                 * value added to x and y.
                 */
                g.fillRect(
                        1 + col * Sizes.getTileSize(),
                        1 + row * Sizes.getTileSize(),
                        Sizes.getTileSize() - 1,
                        Sizes.getTileSize() - 1
                );
            }
        }

        /* Get the position of the selected piece. */
        Optional<Integer> optionalRow = Optional.ofNullable(gameController.getaX());
        Optional<Integer> optionalCol = Optional.ofNullable(gameController.getaY());

        if (optionalRow.isPresent() && optionalCol.isPresent()) {
            boolean[][] possibilities;

            /* If there is a piece on the position, then it will highlight the
            piece possible movements. */
            Position position = new Position(optionalRow.get(), optionalCol.get());
            Optional<Piece> piece = Optional.ofNullable(match.getBoard().getPiece(position));

            /* If the selected piece is the king, then check the safe possible moves.
            If not, then only get the possible moves for the piece. */
            if (piece.isPresent()) {
                if (piece.get() instanceof King)
                    possibilities = ((King) piece.get()).possibleMoves();
                else
                    possibilities = piece.get().possibleMoves(true);
            } else {
                possibilities = new boolean[8][8];
            }

            /*
             * Since the chess board has a different system of coordinates from a
             * computer matrix, the columns need to be inverted,
             * as with all the game coordinates that have the actual columns in the
             * row position and vice versa.
             * This loop will paint the square another color to show the player
             * where the piece can move to.
             */
            for (int row = 0; row < Sizes.getBOARD_SIZE(); row++) {
                for (int col = Sizes.getBOARD_SIZE() - 1; col >= 0; col--) {
                    if (possibilities[col][row]) {
                        g.setColor(GameColors.getHighlights());
                        g.fillRect(
                                1 + col * Sizes.getTileSize(),
                                1 + row * Sizes.getTileSize(),
                                Sizes.getTileSize() - 1,
                                Sizes.getTileSize() - 1
                        );
                    }
                }
            }
        }
        gameDrawer.placePiecesOnBoard(g);
    }
}
