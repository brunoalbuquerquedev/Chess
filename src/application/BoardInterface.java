package application;

import boardgame.Position;
import chess.ChessMatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardInterface extends JPanel {

    private final PieceDrawer pieces;
    private final Sizes sizes;
    private Position firstSelection;

    public BoardInterface(PieceDrawer pieces, ChessMatch match, Sizes sizes) {
        super();
        this.pieces = pieces;
        this.sizes = sizes;
        this.firstSelection = null;

        setPreferredSize(new Dimension(sizes.getDIMENSION(), sizes.getDIMENSION()));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int x = e.getX() / sizes.getTILE_SIZE();
                int y = e.getY() / sizes.getTILE_SIZE();
                System.out.println("Clicked at: " + x + ", " + y);

                if (firstSelection == null) {
                    firstSelection = new Position(x, y);
                } else {
                    match.performChessMove(firstSelection, new Position(x, y));
                }
            }
        });
    }

    private boolean isWhite(int a, int b) {
        return (a + b) % 2 == 0;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Color BROWN = new Color(139, 69, 19, 220);
        final Color BEIGE = new Color(215, 195, 155);

        for (int row = 0; row < sizes.getBOARD_SIZE(); row++) {
            for (int col = 0; col < sizes.getBOARD_SIZE(); col++) {
                g.setColor(isWhite(row, col) ? BEIGE : BROWN);
                g.fillRect(row * sizes.getTILE_SIZE(), col * sizes.getTILE_SIZE(), sizes.getTILE_SIZE(), sizes.getTILE_SIZE());
            }
        }
        pieces.placePiecesOnBoard(g);
    }
}
