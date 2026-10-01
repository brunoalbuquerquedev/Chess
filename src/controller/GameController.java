package controller;

import view.GameDrawer;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.KingNotFoundException;
import pieces.King;
import pieces.Rook;

import javax.swing.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class GameController {

    private final ChessMatch match;
    private final GameDrawer drawer;

    private Integer aX = null;
    private Integer aY = null;
    private Integer bX = null;
    private Integer bY = null;

    public boolean playerHasLegalMoves;

    public GameController(ChessMatch match, GameDrawer drawer) {
        this.match = match;
        this.drawer = drawer;
    }

    public Integer getaX() {
        return aX;
    }

    public void setaX(Integer aX) {
        this.aX = aX;
    }

    public Integer getaY() {
        return aY;
    }

    public void setaY(Integer aY) {
        this.aY = aY;
    }

    public Integer getbX() {
        return bX;
    }

    public void setbX(Integer bX) {
        this.bX = bX;
    }

    public Integer getbY() {
        return bY;
    }

    public void setbY(Integer bY) {
        this.bY = bY;
    }

    /**
     * Handles the player interactions on the board.
     * @param x the row coordinate of the player interaction on the screen.
     * @param y the column coordinate of the player interaction on the screen.
     */
    protected void handleScreenSelection(int x, int y) {
        Position position = new Position(x, y);
        ChessPiece piece = (ChessPiece) match.getBoard().getPiece(position);

        if (shouldSelectSource(piece)) {
            selectSource(x, y, position);
        } else if (shouldSelectTarget()) {
            setbX(x);
            setbY(y);
        }
    }

    private boolean shouldSelectSource(ChessPiece piece) {
        return isAnyCoordinateNull() && Objects.nonNull(piece)
                && match.validatePieceColor(piece.getPosition());
    }

    private boolean shouldSelectTarget() {
        return Objects.nonNull(aX) && Objects.nonNull(aY);
    }

    /**
     * Check the coordinates to select the normal move or a special move.
     * @param x the row of a coordinate.
     * @param y the column of a coordinate.
     * @param position the position of the selected piece.
     * */
    private void selectSource(int x, int y, Position position) {
        if (shouldSelectTarget() && match.validateCastlingPieces(new Position(aX, aY), position)) {
            setbX(x);
            setbY(y);
        } else {
            setaX(x);
            setaY(y);
        }
    }

    /**
     * Checks if any coordinate is null.
     * @return true if any coordinate is null.
     */
    protected boolean isAnyCoordinateNull() {
        return Objects.isNull(aX) || Objects.isNull(aY)
                || Objects.isNull(bX) || Objects.isNull(bY);
    }

    /**
     * Sets all coordinates null.
     */
    protected void cleanAllCoordinates() {
        setaX(null);
        setaY(null);
        setbX(null);
        setbY(null);
    }

    /**
     * This method validates if a move can be performed in the game.
     * @return true if a move can be performed, else false.
     */
    protected boolean verifyPlayerMove() {
        Optional<Position> optionalSource = Optional.of(new Position(aX, aY));
        Optional<Position> optionalTarget = Optional.of(new Position(bX, bY));

        if (!match.validateSourcePosition(optionalSource.get())
                || !match.validateTargetPosition(optionalTarget.get())) {
            return false;
        }

        /* Validate if the move can be done checking the target position on the board.
         * If the piece is the king, it only allows the move to safe squares. */
        if (!match.validateMoveExecution(optionalSource.get(), optionalTarget.get())) {
            cleanAllCoordinates();
            return false;
        }
        return true;
    }

    /**
     * Performs all the movements in the game.
     * Both logical and visual moves.
     * Performs logical and visual moves, as well as handling all exceptions
     * thrown during moves.
     */
    protected void controllerActions() {
        try {
            if (!verifyPlayerMove())
                return;

            Position source = new Position(aX, aY);
            Position target = new Position(bX, bY);
            chessPlayerMove();
            match.nextTurn();

            /* Check game status for the next player after the move is done. */
            checkGameStatus(source, target);
        } catch (NullPointerException | NoSuchElementException e) {
            System.out.println("Controller: " + e.getClass() + "; "
                    + Arrays.toString(e.getStackTrace()));
        } catch (KingNotFoundException e) {

            /*
             * If the king is not on the board, the game can't continue,
             * since the king is necessary for the game.
             * If the king piece is not found, the game has to end immediately.
             * */
            JOptionPane.showMessageDialog(null,
                    "An error occurred: the king was not found " +
                            "on the board. The game will be closed.");
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.exit(1);
        } finally {
            cleanAllCoordinates();
        }
    }

    /**
     * Check the stalemate, checkmate status and if the King or the
     * player can perform some play.
     * After {@code nextTurn()} has been called, {@code match.getPlayerColor()} already
     * returns the opponent's color, so we check that player's king.
     * @param source the position of the piece that just moved (used for king-in-check detection).
     * @param target the position the piece moved to.
     * @throws KingNotFoundException if {@code King}'s instance is not found.
     */
    private void checkGameStatus(Position source, Position target) throws KingNotFoundException {
        playerHasLegalMoves = match.playerHasAnyLegalMove();
        match.setKingCheck(match.verifyPossibleCheck(match.getBoard().getKingPosition(match.getPlayerColor())));
        match.setCheckmate(match.isCheckmate(playerHasLegalMoves));
        match.setStalemate(match.isStalemate(playerHasLegalMoves));
    }

    /**
     * Perform one of the three possible moves: a normal move, a castling move
     * or en passant.
     */
    protected void chessPlayerMove() throws KingNotFoundException {
        Optional<Position> optionalSource = Optional.of(new Position(aX, aY));
        Optional<Position> optionalTarget = Optional.of(new Position(bX, bY));

        /* Capture the source piece before the move, so pawn promotion can
         * reference the correct piece regardless of what ends up at target. */
        ChessPiece sourcePiece = (ChessPiece) match.getBoard().getPiece(optionalSource.get());

        /* Determine whether this is a castling move before executing it. */
        boolean isCastling = match.validateCastlingPieces(optionalSource.get(), optionalTarget.get())
                && match.validateCastlingMove(optionalSource.get(), optionalTarget.get());

        if (isCastling) {
            performCastlingMove(optionalSource.get(), optionalTarget.get());
            drawer.graphicCastlingMove(aX, aY, bX, bY);
        } else {
            /* If the movement isn't a special move, perform a normal move. */
            match.performPieceMove(optionalSource.get(), optionalTarget.get());
            drawer.graphicNormalMove(aX, aY, bX, bY);
        }

        /* Checks for pawn promotion. */
        if (match.validatePawnPromotion(sourcePiece, optionalTarget.get())) {
            match.performPawnPromotion(optionalTarget.get(), sourcePiece);
            drawer.graphicPawnPromotion(bX, bY, sourcePiece.getColor());
        }
    }

    /**
     * Perform the castling move.
     * @param kingPosition {@code King}'s position.
     * @param rookPosition {@code Rook}'s position;
     */
    private void performCastlingMove(Position kingPosition, Position rookPosition) {
        King king = (King) match.getBoard().getPiece(kingPosition);
        Rook rook = (Rook) match.getBoard().getPiece(rookPosition);

        match.getBoard().removePiece(kingPosition);
        match.getBoard().removePiece(rookPosition);

        /* When the king's position is higher than the rook's position (further away),
        perform the long castling, otherwise perform the short castling. */
        int kingRow = (kingPosition.getRow() > rookPosition.getRow())
                ? kingPosition.getRow() - 2 : kingPosition.getRow() + 2;

        int rookRow = (kingPosition.getRow() > rookPosition.getRow())
                ? rookPosition.getRow() + 2 : rookPosition.getRow() - 3;

        Position kingAfterCastling = new Position(kingRow, kingPosition.getColumn());
        Position rookAfterCastling = new Position(rookRow, rookPosition.getColumn());

        match.getBoard().placePiece(kingAfterCastling, king);
        match.getBoard().placePiece(rookAfterCastling, rook);
    }
}