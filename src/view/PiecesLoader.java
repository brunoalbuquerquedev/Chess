package view;

import javax.swing.*;
import java.util.Objects;

public class PiecesLoader {

    private final ImageIcon[][] piecesIcons;

    public PiecesLoader(ImageIcon[][] piecesIcons) {
        this.piecesIcons = piecesIcons;
        loadInitialPiecesIcons();
    }

    public ImageIcon[][] getPiecesIcons() {
        return piecesIcons;
    }

    public void loadInitialPiecesIcons() {

        /* Load the piecesImages image files. */
        ImageIcon whitePawn = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/white_pawn.png")));
        ImageIcon whiteRook = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/white_rook.png")));
        ImageIcon whiteBishop = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/white_bishop.png")));
        ImageIcon whiteKing = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/white_king.png")));
        ImageIcon whiteQueen = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/white_queen.png")));
        ImageIcon whiteKnight = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/white_knight.png")));

        ImageIcon blackPawn = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/black_pawn.png")));
        ImageIcon blackRook = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/black_rook.png")));
        ImageIcon blackBishop = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/black_bishop.png")));
        ImageIcon blackKing = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/black_king.png")));
        ImageIcon blackQueen = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/black_queen.png")));
        ImageIcon blackKnight = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/black_knight.png")));

        /* Place white pieces images. */
        for (int a = 0; a < 8; a++) {
            piecesIcons[a][6] = whitePawn;
        }
        piecesIcons[0][7] = whiteRook;
        piecesIcons[1][7] = whiteKnight;
        piecesIcons[2][7] = whiteBishop;
        piecesIcons[3][7] = whiteKing;
        piecesIcons[4][7] = whiteQueen;
        piecesIcons[5][7] = whiteBishop;
        piecesIcons[6][7] = whiteKnight;
        piecesIcons[7][7] = whiteRook;

        /* Black pieces images. */
        for (int a = 0; a < 8; a++) {
            piecesIcons[a][1] = blackPawn;
        }
        piecesIcons[0][0] = blackRook;
        piecesIcons[1][0] = blackKnight;
        piecesIcons[2][0] = blackBishop;
        piecesIcons[3][0] = blackKing;
        piecesIcons[4][0] = blackQueen;
        piecesIcons[5][0] = blackBishop;
        piecesIcons[6][0] = blackKnight;
        piecesIcons[7][0] = blackRook;
    }
}
