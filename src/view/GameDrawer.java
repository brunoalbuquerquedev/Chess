package view;

import boardgame.Position;
import chess.ChessColor;
import chess.ChessMatch;
import chess.KingNotFoundException;
import pieces.King;
import pieces.Rook;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GameDrawer extends JPanel {

    private final ImageIcon[][] piecesIcons;
    private final ChessMatch match;

    public GameDrawer(ImageIcon[][] piecesIcons, ChessMatch match) {
        this.piecesIcons = piecesIcons;
        this.match = match;
    }

    public ImageIcon[][] getPiecesIcons() {
        return piecesIcons;
    }

    public void placePieceIcon(int x, int y, ImageIcon image) {
        piecesIcons[x][y] = image;
    }

    public void removePieceIcon(int x, int y) {
        piecesIcons[x][y] = null;
    }

    /* Perform the moves of the piece icons on the board. */
    public void iconMove(Integer aX, Integer aY, Integer bX, Integer bY) {
        if (aX == null || aY == null || bX == null || bY == null) {
            throw new IllegalArgumentException("Coordinates cannot be null.");
        }

        ImageIcon icon = getPiecesIcons()[aX][aY];

        if (Objects.isNull(icon))
            return;

        removePieceIcon(aX, aY);
        placePieceIcon(bX, bY, icon);
    }

    /* Do the change of a pawn icon to a queen icon when the pawn is promoted. */
    public void graphicPawnPromotion(int aX, int aY, ChessColor color) {

        /* Checks the color of the piece because the icon files are different. */
        if (color == ChessColor.WHITE) {
            ImageIcon whiteQueen = new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/resources/white_queen.png"))
            );
            removePieceIcon(aX, aY);
            placePieceIcon(aX, aY, whiteQueen);
        } else {
            ImageIcon blackQueen = new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/resources/black_queen.png"))
            );
            removePieceIcon(aX, aY);
            placePieceIcon(aX, aY, blackQueen);
        }
    }

    /**
     * Perform the icon's move on the panel for a normal (non-castling) move.
     * @param aX {@code x} coordinate from the source square.
     * @param aY {@code y} coordinate from the source square.
     * @param bX {@code x} coordinate from the target square.
     * @param bY {@code y} coordinate from the target square.
     */
    public void graphicNormalMove(Integer aX, Integer aY, Integer bX, Integer bY) {
        iconMove(aX, aY, bX, bY);
    }

    /**
     * Perform the icon's move on the panel for a castling move.
     * Moves both the king and the rook icons to their post-castling positions
     * and increments their move counters.
     * @param kingSourceX {@code x} coordinate from the king's source square.
     * @param kingSourceY {@code y} coordinate from the king's source square.
     * @param rookSourceX {@code x} coordinate from the rook's source square.
     * @param rookSourceY {@code y} coordinate from the rook's source square.
     * @throws KingNotFoundException if {@code King}'s instance is not found on the board.
     */
    public void graphicCastlingMove(Integer kingSourceX, Integer kingSourceY,
                                    Integer rookSourceX, Integer rookSourceY)
            throws KingNotFoundException {

        /* Calculate destination rows for the king and rook after castling.
         * King moves 2 squares toward the rook; rook lands on the other side. */
        int kingDestRow = (kingSourceX > rookSourceX)
                ? kingSourceX - 2 : kingSourceX + 2;
        int rookDestRow = (kingSourceX > rookSourceX)
                ? rookSourceX + 2 : rookSourceX - 3;

        iconMove(kingSourceX, kingSourceY, kingDestRow, kingSourceY);
        iconMove(rookSourceX, rookSourceY, rookDestRow, rookSourceY);

        /*
         * Increment move counters for both pieces only after the graphical move,
         * because the castling validation requires move count to be zero.
         */
        ChessColor playerColor = match.getPlayerColor();
        Position kingPosition = match.getBoard().getKingPosition(playerColor);

        King king = (King) match.getBoard().getPiece(kingPosition);
        Rook rook = (Rook) match.getBoard().getPiece(new Position(rookDestRow, rookSourceY));
        king.addMoveCount();
        rook.addMoveCount();
    }

    /* Load all pieces icons to the board. */
    public void placePiecesOnBoard(Graphics g) {

        /* If there is any problem with the piece icons, then the game cannot
        be initiated, so it will close. */
        if (Objects.isNull(piecesIcons)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Game resources could not be loaded.",
                    "Error", JOptionPane.ERROR_MESSAGE,
                    null);
            System.exit(1);
        }

        /* Resizes every icon to the size of the tile. */
        for (int row = 0; row < Sizes.getBOARD_SIZE(); row++) {
            for (int col = 0; col < Sizes.getBOARD_SIZE(); col++) {

                if (Objects.nonNull(piecesIcons[row][col])) {
                    Image image = piecesIcons[row][col].getImage();

                    Image resizedImage = image.getScaledInstance(
                            Sizes.getPieceSize() - 1,
                            Sizes.getPieceSize() - 1,
                            Image.SCALE_SMOOTH);

                    ImageIcon newImage = new ImageIcon(resizedImage);
                    newImage.paintIcon(this, g,
                            row * Sizes.getTileSize(),
                            col * Sizes.getTileSize());
                }
            }
        }
    }
}
