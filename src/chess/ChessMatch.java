package chess;

import view.Sizes;
import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import pieces.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChessMatch {
    private final Board board;
    private ChessColor playerColor;
    private int turn;

    private boolean kingCheck;
    private boolean stalemate;
    private boolean checkmate;

    public boolean isKingCheck() {
        return kingCheck;
    }

    public void setKingCheck(boolean kingCheck) {
        this.kingCheck = kingCheck;
    }

    public boolean isStalemate() {
        return stalemate;
    }

    public void setStalemate(boolean stalemate) {
        this.stalemate = stalemate;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public void setCheckmate(boolean checkmate) {
        this.checkmate = checkmate;
    }

    public ChessMatch() {
        this.board = new Board(Sizes.getBOARD_SIZE(), Sizes.getBOARD_SIZE());
        this.playerColor = ChessColor.WHITE;
        this.turn = 0;
        loadInitialPieces();
    }

    public Board getBoard() {
        return board;
    }

    public int getTurn() {
        return turn;
    }

    public ChessColor getPlayerColor() {
        return playerColor;
    }

    /**
     * Changes the player turns.
     */
    public void nextTurn() {
        turn++;
        playerColor = invertColor(playerColor);
    }

    /**
     * Inverts the color of the player.
     * @param chessColor the player's color.
     */
    private ChessColor invertColor(ChessColor chessColor) {
        return chessColor.equals(ChessColor.WHITE)
                ? ChessColor.BLACK
                : ChessColor.WHITE;
    }

    /* Validation methods. Each method will authenticate
    a condition from the game. */

    /**
     * Validate if there is a piece at the position and if it's the same color as the player.
     * @param position the position of the piece to be validated.
     * @return true if the position is not empty, and the piece belongs to the player.
     */
    public boolean validateSourcePosition(Position position) {
        return !board.isPositionEmpty(position)
                && validatePieceColor(position);
    }

    /**
     * Validate the target position. The position should either be empty or contain an opponent's piece.
     * @param position the target position to be validated.
     * @return true if the position is either empty or contains an opponent's piece.
     */
    public boolean validateTargetPosition(Position position) {
        if (!board.isPositionEmpty(position) &&
                validatePieceColor(position)) {
            return false;
        }

        return board.isPositionEmpty(position) ||
                !board.isPositionEmpty(position) &&
                        !validatePieceColor(position);
    }

    /**
     * Validates if there is an opponent's piece at the position.
     * @param position the target position.
     * @return true if there is an opponent piece at the position, false otherwise.
     */
    public boolean validateOpponentPiece(Position position) {
        if (board.isPositionEmpty(position)
                || validateSourcePosition(position)) {
            return false;
        }
        return !board.isPositionEmpty(position)
                && !validatePieceColor(position);
    }

    /**
     * Validates castling move by ensuring the king and rook haven't moved.
     * @param source the king's position.
     * @param target the rook's position.
     * @return true if the king and rook are in their starting positions and haven't moved.
     */
    public boolean validateCastlingPieces(Position source, Position target) {
        if (Objects.isNull(board.getPiece(source)) || Objects.isNull(board.getPiece(target)))
            return false;

        if (board.isPositionEmpty(source) || board.isPositionEmpty(target))
            return false;

        if (!(board.getPiece(source) instanceof King)
                && !(board.getPiece(target) instanceof Rook))
            return false;

        return (!((ChessPiece) board.getPiece(source)).hasMoved()
                && !((ChessPiece) board.getPiece(target)).hasMoved());
    }

    /**
     * Validate pawn promotion when a pawn reaches the last row.
     * @param piece the pawn piece.
     * @param target the target position the pawn moved to.
     * @return true if the pawn is eligible for promotion.
     */
    public boolean validatePawnPromotion(ChessPiece piece, Position target) {
        if (Objects.isNull(piece) || Objects.isNull(target)) {
            return false;
        }

        return piece instanceof Pawn
                && (target.getColumn() == 0 || target.getColumn() == 7);
    }

    /**
     * Compare the color of the player and the piece at the given position.
     * @param position the position to be compared.
     * @return true if the piece's color matches the player's color.
     */
    public boolean validatePieceColor(Position position) {
        return playerColor.equals(((ChessPiece) board.getPiece(position)).getColor());
    }

    /**
     * Checks if castling is possible. Validates that:
     * <ul>
     *   <li>Neither the king nor the rook has moved.</li>
     *   <li>There are no pieces between the king and the rook.</li>
     *   <li>The king is not currently in check.</li>
     *   <li>The king does not pass through any attacked square.</li>
     *   <li>The king does not land on an attacked square (handled by
     *       {@code King.possibleMoves()}, but also verified here for
     *       completeness).</li>
     * </ul>
     * @param kingPosition the king's position.
     * @param rookPosition the rook's position.
     * @return true if the player can perform the castling move.
     */
    public boolean validateCastlingMove(Position kingPosition, Position rookPosition) {
        if (Objects.isNull(kingPosition) || Objects.isNull(rookPosition))
            return false;

        if (!validateCastlingPieces(kingPosition, rookPosition))
            return false;

        King king = (King) board.getPiece(kingPosition);
        Rook rook = (Rook) board.getPiece(rookPosition);

        if (Objects.isNull(king) || Objects.isNull(rook)
                || king.hasMoved() || (rook.hasMoved()
                && validatePieceColor(rook.getPosition()))) {
            return false;
        }

        /* Castling is forbidden if the king is currently in check. */
        if (verifyPossibleCheck(kingPosition))
            return false;

        int step = (rookPosition.getRow() > kingPosition.getRow()) ? 1 : -1;

        /* Check that there are no pieces between king and rook,
         * and that the king does not pass through an attacked square. */
        for (int row = kingPosition.getRow() + step;
             row != rookPosition.getRow(); row += step) {

            Position squareBetween = new Position(row, kingPosition.getColumn());

            if (!board.isPositionEmpty(squareBetween))
                return false;

            /* The king only travels two squares; only check those for attacks. */
            int distanceFromKing = Math.abs(row - kingPosition.getRow());
            if (distanceFromKing <= 2 && verifyPossibleCheck(squareBetween))
                return false;
        }
        return true;
    }

    /**
     * Validate if the king is in check after a move.
     * @param position the position to be checked.
     * @return true if the king is in check.
     */
    public boolean verifyPossibleCheck(Position position) {
        ArrayList<Piece> pieces = new ArrayList<>(board.getActivePieces());
        boolean[][] possibilities;

        for (Piece piece : pieces) {
            if (Objects.nonNull(piece)) {
                Position opponentPosition = piece.getPosition();

                    /* If it's an opponent's piece, possibilities receive
                    their possible moves. */
                if (validateOpponentPiece(opponentPosition)) {
                    possibilities = board
                            .getPiece(opponentPosition)
                            .possibleMoves(false);

                        /* If any piece movement matches the king's position,
                        it returns true. */
                    if (possibilities[position.getRow()][position.getColumn()])
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Executes the move if valid.
     * @param source the source position.
     * @param target the target position.
     * @return true if the move was executed successfully.
     */
    public boolean validateMoveExecution(Position source, Position target) {
        boolean[][] possibilities;
        Piece piece = board.getPiece(source);

        if (piece instanceof King)
            possibilities = ((King) piece).possibleMoves();
        else
            possibilities = piece.possibleMoves(true);

        return possibilities[target.getRow()][target.getColumn()];
    }

    /* Movements and special moves methods. */

    /**
     * Perform a regular piece move on the board.
     * @param source the position of the piece to be moved.
     * @param target the target position.
     */
    public void performPieceMove(Position source, Position target) {
        Piece sourcePiece = getBoard().getPiece(source);
        Piece targetPiece = getBoard().getPiece(target);

        board.removePiece(source);
        board.removePiece(target);

        if (Objects.nonNull(targetPiece)) {
            board.getActivePieces().remove(targetPiece);
            board.getCapturedPieces().add(targetPiece);
        }

        board.placePiece(target, sourcePiece);
        ((ChessPiece) sourcePiece).addMoveCount();
    }

    /**
     * Promotes a pawn to a queen.
     * @param position the pawn's position.
     * @param piece the pawn piece.
     */
    public void performPawnPromotion(Position position, ChessPiece piece) {
        board.removePiece(position);
        board.getActivePieces().remove(piece);
        board.placeNewPiece(position, new Queen(board, piece.getColor()));
    }

    public void enPassant() {

    }

    /**
     * Simulates the move from source to target and checks if the current
     * player's king would be in check after that move.
     * This is used to filter out illegal moves that would expose the king.
     * @param source the source position of the piece to move.
     * @param target the target position.
     * @return true if the king would be in check after the move.
     * @throws KingNotFoundException if the King is not found.
     */
    public boolean isKingInCheck(Position source, Position target) throws KingNotFoundException {
        Piece sourcePiece = board.getPiece(source);
        Piece targetPiece = board.getPiece(target);

        Optional<Position> optionalKingPosition = Optional.ofNullable(
                board.getKingPosition(getPlayerColor()));

        if (optionalKingPosition.isEmpty())
            throw new KingNotFoundException("King piece not found.");

        /* Simulate the move. */
        board.removePiece(source);
        board.removePiece(target);
        board.placePiece(target, sourcePiece);

        if (Objects.nonNull(targetPiece))
            board.getActivePieces().remove(targetPiece);

        /* If the moved piece is the king, update the king position for the check. */
        Position kingPositionAfterMove = sourcePiece instanceof King
                ? target
                : optionalKingPosition.get();

        boolean isCheck = verifyPossibleCheck(kingPositionAfterMove);

        /* Undo the simulated move. */
        board.removePiece(target);
        board.placePiece(source, sourcePiece);

        if (Objects.nonNull(targetPiece)) {
            board.placePiece(target, targetPiece);
            board.getActivePieces().add(targetPiece);
        }

        return isCheck;
    }

    /**
     * Checks if the game has reached a checkmate state.
     * @param playerHasLegalMoves true if the player has legal moves.
     * @return true if the game is in checkmate.
     * @throws KingNotFoundException if the King is not found.
     */
    public boolean isCheckmate(boolean playerHasLegalMoves) throws KingNotFoundException {
        Optional<Position> optionalKingPosition = Optional.ofNullable(
                board.getKingPosition(getPlayerColor())
        );

        if (optionalKingPosition.isEmpty())
            throw new KingNotFoundException("King piece not found.");

        if (kingCheck && !playerHasLegalMoves) {
            JOptionPane.showMessageDialog(
                    null,
                    "Checkmate. Game over.",
                    "Chess",
                    JOptionPane.INFORMATION_MESSAGE,
                    null);
            return true;
        }
        return false;
    }

    /**
     * Checks if the game has reached a stalemate state.
     * @param playerHasLegalMoves true if the player has legal moves.
     * @return true if the game is in stalemate.
     * @throws KingNotFoundException if the King is not found.
     */
    public boolean isStalemate(boolean playerHasLegalMoves) throws KingNotFoundException {

        /*
         * All these conditions lead to a game with impossible checkmate,
         * leading to a draw:
         * King versus King;
         * King and Bishop or Knight versus King;
         * King and Bishop versus King and
         * Bishop with the bishops on the same color square.
         * Since it is not possible to capture any piece in the first and
         * second moves, at least twenty-eight more turns
         * are needed, where captures will be made so that only
         * four pieces remain on the board.
         * After thirty moves, it will check what pieces are
         * on the board.
         */

        Optional<Position> optionalKingPosition = Optional.ofNullable(
                board.getKingPosition(getPlayerColor())
        );

        if (optionalKingPosition.isEmpty())
            throw new KingNotFoundException("King piece not found.");

        /* Returns true if the player has no legal moves and the king is not in check
        (afogamento/stalemate — different from checkmate). */
        if (!kingCheck && !playerHasLegalMoves)
            return true;

        if (turn < 29)
            return false;

        int numberOfPiecesOnBoard = board.getActivePieces().size();

        if (numberOfPiecesOnBoard > 4)
            return false;

        List<Piece> activePieces = new ArrayList<>(board.getActivePieces());

        switch (activePieces.size()) {
            /* King versus king. */
            case 2:
                if (activePieces.stream().allMatch(piece -> piece instanceof King))
                    return true;
                break;

            /* King and bishop or knight versus king. */
            case 3:
                if (activePieces.stream().anyMatch(p -> p instanceof King)
                        && activePieces.stream().anyMatch(p -> p instanceof Bishop
                        || p instanceof Knight)) {
                    return true;
                }
                break;

            /* King and bishop versus king and bishop with the same color square. */
            case 4:
                long bishopCount = activePieces
                        .stream()
                        .filter(piece -> piece instanceof Bishop)
                        .count();

                if (bishopCount == 2) {
                    Bishop[] bishops = activePieces
                            .stream()
                            .filter(piece -> piece instanceof Bishop)
                            .toArray(Bishop[]::new);

                    if (!bishops[0].getColor().equals(bishops[1].getColor()) &&
                            !bishops[0].squareColor().equals(bishops[1].squareColor())) {
                        return true;
                    }
                }
                break;

            default:
                return false;
        }
        return false;
    }

    /**
     * Check if the player has any legal move to do in the game.
     * @return true if the player has any valid move to perform, else false.
     * @throws KingNotFoundException if {@code King}'s instance is not found.
     */
    public boolean playerHasAnyLegalMove() throws KingNotFoundException {
        ArrayList<Piece> pieces = new ArrayList<>(board.getActivePieces());

        for (Piece piece : pieces) {
            if (((ChessPiece) piece).getColor().equals(getPlayerColor())) {
                boolean[][] possibilities = piece.possibleMoves(true);

                for (int row = 0; row < possibilities.length; row++) {
                    for (int col = 0; col < possibilities[row].length; col++) {

                        if (possibilities[row][col]) {
                            Position target = new Position(row, col);

                            if (!isKingInCheck(piece.getPosition(), target))
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Instantiate the pieces on the board.
     * */
    private void loadInitialPieces() {
        board.placeNewPiece(new Position(0, 7), new Rook(board, ChessColor.WHITE));
        board.placeNewPiece(new Position(1, 7), new Knight(board, ChessColor.WHITE));
        board.placeNewPiece(new Position(2, 7), new Bishop(board, ChessColor.WHITE));
        board.placeNewPiece(new Position(3, 7), new King(board, ChessColor.WHITE, this));
        board.placeNewPiece(new Position(4, 7), new Queen(board, ChessColor.WHITE));
        board.placeNewPiece(new Position(5, 7), new Bishop(board, ChessColor.WHITE));
        board.placeNewPiece(new Position(6, 7), new Knight(board, ChessColor.WHITE));
        board.placeNewPiece(new Position(7, 7), new Rook(board, ChessColor.WHITE));

        for (int a = 0; a < Sizes.getBOARD_SIZE(); a++) {
            board.placeNewPiece(
                    new Position(a, 6),
                    new Pawn(board, ChessColor.WHITE, this)
            );
        }

        board.placeNewPiece(new Position(0, 0), new Rook(board, ChessColor.BLACK));
        board.placeNewPiece(new Position(1, 0), new Knight(board, ChessColor.BLACK));
        board.placeNewPiece(new Position(2, 0), new Bishop(board, ChessColor.BLACK));
        board.placeNewPiece(new Position(3, 0), new King(board, ChessColor.BLACK, this));
        board.placeNewPiece(new Position(4, 0), new Queen(board, ChessColor.BLACK));
        board.placeNewPiece(new Position(5, 0), new Bishop(board, ChessColor.BLACK));
        board.placeNewPiece(new Position(6, 0), new Knight(board, ChessColor.BLACK));
        board.placeNewPiece(new Position(7, 0), new Rook(board, ChessColor.BLACK));

        for (int a = 0; a < Sizes.getBOARD_SIZE(); a++) {
            board.placeNewPiece(
                    new Position(a, 1),
                    new Pawn(board, ChessColor.BLACK, this)
            );
        }
    }
}