package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

public abstract class ChessPiece extends Piece {
    private final ChessColor color;
    private int moveCount;

    public ChessPiece(Board board, ChessColor color) {
        super(board);
        this.color = color;
    }

    public ChessColor getColor() {
        return color;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    // Check if there is a piece on the position and if it has, checks the color of the piece
    public boolean checkPossibleCapture(Position position) {
        ChessPiece piece = (ChessPiece) getBoard().getPieceOnBoard(position);
        return piece != null && piece.getColor() != getColor();
    }
}
