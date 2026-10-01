package pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessColor;
import chess.ChessMatch;
import chess.ChessPiece;

public class Pawn extends ChessPiece {

    private final ChessMatch match;

    public Pawn(Board board, ChessColor chessColor, ChessMatch match) {
        super(board, chessColor);
        this.match = match;
    }

    @Override
    public boolean[][] possibleMoves(boolean captureAllowed) {
        boolean[][] possibilities = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position oneStepWhite = new Position(
                getPosition().getRow(),
                getPosition().getColumn() - 1
        );
        Position twoStepsWhite = new Position(
                getPosition().getRow(),
                getPosition().getColumn() - 2
        );
        Position leftDiagonalWhite = new Position(
                getPosition().getRow() - 1,
                getPosition().getColumn() - 1
        );
        Position rightDiagonalWhite = new Position(
                getPosition().getRow() + 1,
                getPosition().getColumn() - 1
        );
        Position oneStepBlack = new Position(
                getPosition().getRow(),
                getPosition().getColumn() + 1
        );
        Position twoStepsBlack = new Position(
                getPosition().getRow(),
                getPosition().getColumn() + 2
        );
        Position leftDiagonalBlack = new Position(
                getPosition().getRow() + 1,
                getPosition().getColumn() + 1
        );
        Position rightDiagonalBlack = new Position(
                getPosition().getRow() - 1,
                getPosition().getColumn() + 1
        );

        if (captureAllowed) {
            if (getColor() == ChessColor.WHITE) {

                // One house move
                if (getBoard().positionExists(oneStepWhite)
                        && getBoard().isPositionEmpty(oneStepWhite)) {
                    possibilities[oneStepWhite.getRow()][oneStepWhite.getColumn()] = true;
                }

                // Two houses move
                if (getBoard().positionExists(oneStepWhite)
                        && getBoard().isPositionEmpty(oneStepWhite)
                        && getBoard().positionExists(twoStepsWhite)
                        && getBoard().isPositionEmpty(twoStepsWhite)
                        && !hasMoved()) {
                    possibilities[twoStepsWhite.getRow()][twoStepsWhite.getColumn()] = true;
                }

                // Capturing a piece on the left diagonal
                if (getBoard().positionExists(leftDiagonalWhite)
                        && !getBoard().isPositionEmpty(leftDiagonalWhite)
                        && validatePieceCapture(leftDiagonalWhite)) {
                    possibilities[leftDiagonalWhite.getRow()][leftDiagonalWhite.getColumn()] = true;
                }

                // Capturing a piece on the right diagonal
                if (getBoard().positionExists(rightDiagonalWhite)
                        && !getBoard().isPositionEmpty(rightDiagonalWhite)
                        && validatePieceCapture(rightDiagonalWhite)) {
                    possibilities[rightDiagonalWhite.getRow()][rightDiagonalWhite.getColumn()] = true;
                }
            }

            if (getColor() == ChessColor.BLACK) {
                // One house move
                if (getBoard().positionExists(oneStepBlack)
                        && getBoard().isPositionEmpty(oneStepBlack)) {
                    possibilities[oneStepBlack.getRow()][oneStepBlack.getColumn()] = true;
                }

                // Two houses move
                if (getBoard().positionExists(oneStepBlack)
                        && getBoard().isPositionEmpty(oneStepBlack)
                        && getBoard().positionExists(twoStepsBlack)
                        && getBoard().isPositionEmpty(twoStepsBlack)
                        && !hasMoved()) {
                    possibilities[twoStepsBlack.getRow()][twoStepsBlack.getColumn()] = true;
                }

                // Capturing a piece on the left diagonal
                if (getBoard().positionExists(leftDiagonalBlack)
                        && !getBoard().isPositionEmpty(leftDiagonalBlack)
                        && validatePieceCapture(leftDiagonalBlack)) {
                    possibilities[leftDiagonalBlack.getRow()][leftDiagonalBlack.getColumn()] = true;
                }

                // Capturing a piece on the right diagonal
                if (getBoard().positionExists(rightDiagonalBlack)
                        && !getBoard().isPositionEmpty(rightDiagonalBlack)
                        && validatePieceCapture(rightDiagonalBlack)) {
                    possibilities[rightDiagonalBlack.getRow()][rightDiagonalBlack.getColumn()] = true;
                }
            }
        } else {

            if (getColor() == ChessColor.WHITE) {
                // Capturing a piece on the left diagonal
                if (getBoard().positionExists(leftDiagonalWhite)) {
                    possibilities[leftDiagonalWhite.getRow()][leftDiagonalWhite.getColumn()] = true;
                }

                // Capturing a piece on the right diagonal
                if (getBoard().positionExists(rightDiagonalWhite)) {
                    possibilities[rightDiagonalWhite.getRow()][rightDiagonalWhite.getColumn()] = true;
                }
            }

            if (getColor() == ChessColor.BLACK) {
                // Capturing a piece on the left diagonal
                if (getBoard().positionExists(leftDiagonalBlack)) {
                    possibilities[leftDiagonalBlack.getRow()][leftDiagonalBlack.getColumn()] = true;
                }

                // Capturing a piece on the right diagonal
                if (getBoard().positionExists(rightDiagonalBlack)) {
                    possibilities[rightDiagonalBlack.getRow()][rightDiagonalBlack.getColumn()] = true;
                }
            }
        }
        return possibilities;
    }
}
