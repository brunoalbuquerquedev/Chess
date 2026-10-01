package pieces;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.ChessColor;
import chess.ChessMatch;
import chess.ChessPiece;

public class King extends ChessPiece {

    private final ChessMatch match;

    public King(Board board, ChessColor chessColor, ChessMatch match) {
        super(board, chessColor);
        this.match = match;
    }

    private final int[][] directions = {
            {0, -1}, // Up
            {0, 1},  // Down
            {-1, 0}, // Left
            {1, 0},  // Right
            {-1, -1}, // Up-Left
            {1, -1},  // Up-Right
            {-1, 1},  // Down-Left
            {1, 1}    // Down-Right
    };

    public int[][] getDirections() {
        return directions;
    }

    @Override
    public boolean[][] possibleMoves(boolean captureAllowed) {
        boolean[][] possibilities = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position currentKingPosition = getPosition();

        /* The rook position's on the board. */
        final Position[] rookPositions = {
                new Position(this.getPosition().getRow() + 4,
                        this.getPosition().getColumn()
                ),
                new Position(this.getPosition().getRow() - 3,
                        this.getPosition().getColumn()
                )
        };

        if (captureAllowed) {

            /* Checks if it can move to any position in the matrix "directions". */
            for (int[] direction : directions) {
                Position kingPosition = new Position(
                        currentKingPosition.getRow() + direction[0],
                        currentKingPosition.getColumn() + direction[1]
                );

                if (getBoard().positionExists(kingPosition)
                        && getBoard().isPositionEmpty(kingPosition)
                        || validatePieceCapture(kingPosition)) {
                    possibilities[kingPosition.getRow()][kingPosition.getColumn()] = true;
                }
            }

            /* Marks as true the castling move position. */
            for (Position position : rookPositions) {
                if (match.validateCastlingMove(currentKingPosition, position))
                    possibilities[position.getRow()][position.getColumn()] = true;
            }

        } else {

            /* Marks all king directions as true on the board. */
            for (int[] direction : directions) {
                Position kingPosition = new Position(
                        currentKingPosition.getRow() + direction[0],
                        currentKingPosition.getColumn() + direction[1]
                );

                if (getBoard().positionExists(kingPosition))
                    possibilities[kingPosition.getRow()][kingPosition.getColumn()] = true;
            }
        }
        return possibilities;
    }

    /**
     * Returns the set of squares the king can legally move to, excluding any
     * square that is under attack by an opponent piece.
     *
     * <p>Unlike other pieces, the king must never step onto an attacked square,
     * regardless of whether that square is empty or occupied by an opponent.
     * The method therefore builds the attack map for all opponent pieces and
     * removes every attacked square from the king's candidate moves.
     *
     * <p>{@code possibleMoves(true)} is used as the base candidate set because
     * it already accounts for board boundaries and allied pieces.
     * {@code possibleMoves(false)} is used for each opponent piece so that
     * pieces behind other pieces (including the king itself) are not missed.
     */
    public boolean[][] possibleMoves() {
        boolean[][] candidates = possibleMoves(true);

        /* Build a union of all squares attacked by any opponent piece. */
        boolean[][] attacked = new boolean[8][8];

        for (Piece piece : match.getBoard().getActivePieces()) {
            if (match.validateOpponentPiece(piece.getPosition())) {
                boolean[][] opponentMoves = piece.possibleMoves(false);
                for (int a = 0; a < attacked.length; a++) {
                    for (int b = 0; b < attacked[a].length; b++) {
                        if (opponentMoves[a][b])
                            attacked[a][b] = true;
                    }
                }
            }
        }

        /* Remove attacked squares from the candidate set. */
        for (int a = 0; a < candidates.length; a++) {
            for (int b = 0; b < candidates[a].length; b++) {
                if (attacked[a][b])
                    candidates[a][b] = false;
            }
        }

        return candidates;
    }
}
