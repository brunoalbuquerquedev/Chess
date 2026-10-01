package pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessColor;
import chess.ChessPiece;

public class Rook extends ChessPiece {

    public Rook(Board board, ChessColor chessColor) {
        super(board, chessColor);
    }

    private final int[][] directions = {
            {0, -1}, // Up
            {0, 1}, // Down
            {-1, 0}, // Left
            {1, 0}, // Right
    };

    @Override
    public boolean[][] possibleMoves(boolean captureAllowed) {
        boolean[][] possibilities = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position currentRookPosition = getPosition();

        /* All the sides directions. */
        for (int[] direction : directions) {
            if (captureAllowed)
                checkRookDirection(currentRookPosition, possibilities, direction);
            else
                checkRookWithoutCapture(currentRookPosition, possibilities, direction);
        }
        return possibilities;
    }

    private void checkRookDirection(Position rook, boolean[][] possibilities, int[] arr) {
        Position position = new Position(rook.getRow() + arr[0], rook.getColumn() + arr[1]);

        while (getBoard().positionExists(position)
                && getBoard().isPositionEmpty(position)
                || validatePieceCapture(position)) {
            possibilities[position.getRow()][position.getColumn()] = true;

            /* If the piece can be captured, breaks the loop. */
            if (validatePieceCapture(position))
                break;

            /* Increment the value of the row and column until reach a piece
            or to the end of the board. */
            position.setPosition(
                    position.getRow() + arr[0],
                    position.getColumn() + arr[1]);
        }
    }

    private void checkRookWithoutCapture(Position rook, boolean[][] possibilities, int[] arr) {
        Position position = new Position(rook.getRow() + arr[0], rook.getColumn() + arr[1]);

        while (getBoard().positionExists(position)) {

            possibilities[position.getRow()][position.getColumn()] = true;

            // If there is a piece at the position, breaks the loop
            if (!getBoard().isPositionEmpty(position))
                break;

            /* Increment the value of the row and column until reach a piece
            or to the end of the board. */
            position.setPosition(
                    position.getRow() + arr[0],
                    position.getColumn() + arr[1]);
        }
    }
}
