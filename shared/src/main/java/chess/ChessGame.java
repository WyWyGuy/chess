package chess;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTurn;
    private ChessBoard board;
    private boolean whiteLeftRookHasMoved;
    private boolean whiteRightRookHasMoved;
    private boolean whiteKingHasMoved;
    private boolean blackLeftRookHasMoved;
    private boolean blackRightRookHasMoved;
    private boolean blackKingHasMoved;
    private ChessPosition isEnPassantable;

    public ChessGame() {
        this.currentTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.whiteLeftRookHasMoved = false;
        this.whiteRightRookHasMoved = false;
        this.whiteKingHasMoved = false;
        this.blackLeftRookHasMoved = false;
        this.blackRightRookHasMoved = false;
        this.blackKingHasMoved = false;
        this.isEnPassantable = null;
    }

    public ChessGame(ChessGame original) {
        this.currentTurn = original.currentTurn;
        this.board = new ChessBoard(original.getBoard());
        this.whiteLeftRookHasMoved = original.whiteLeftRookHasMoved;
        this.whiteRightRookHasMoved = original.whiteRightRookHasMoved;
        this.whiteKingHasMoved = original.whiteKingHasMoved;
        this.blackLeftRookHasMoved = original.blackLeftRookHasMoved;
        this.blackRightRookHasMoved = original.blackRightRookHasMoved;
        this.blackKingHasMoved = original.blackKingHasMoved;
        this.isEnPassantable = original.isEnPassantable;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public ChessPosition getKingPosition(TeamColor color) throws NoKingFoundException {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition ijPosition = new ChessPosition(i, j);
                ChessPiece ijPiece = this.getBoard().getPiece(ijPosition);
                if (ijPiece != null && ijPiece.getTeamColor() == color && ijPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return ijPosition;
                }
            }
        }
        throw new NoKingFoundException(String.format("%s king not found on the board.", color));
    }

    public boolean leavesKingInCheck(TeamColor color, ChessMove move) {
        ChessGame gameCopy = new ChessGame(this);
        ChessPiece movingPiece = gameCopy.getBoard().getPiece(move.getStartPosition());
        if (move.getPromotionPiece() == null) {
            gameCopy.getBoard().addPiece(move.getEndPosition(), movingPiece);
        } else {
            ChessPiece promotedPawn = new ChessPiece(color, move.getPromotionPiece());
            gameCopy.getBoard().addPiece(move.getEndPosition(), promotedPawn);
        }
        gameCopy.getBoard().addPiece(move.getStartPosition(), null);
        this.castle(gameCopy, move);
        this.enPassant(gameCopy, move);
        return gameCopy.isInCheck(color);
    }

    public Collection<ChessMove> generatePossibleMoves(ChessBoard board, TeamColor color) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition iterPosition = new ChessPosition(row, col);
                ChessPiece iterPiece = board.getPiece(iterPosition);
                if ((iterPiece == null) || (iterPiece.getTeamColor() != color)) {
                    continue;
                }
                possibleMoves.addAll(iterPiece.pieceMoves(board, iterPosition));
            }
        }
        possibleMoves.addAll(addCastling(color));
        possibleMoves.addAll(addEnPassant(color));
        return possibleMoves;
    }

    public Collection<ChessMove> addCastling(TeamColor color) {
        Collection<ChessMove> castleMoves = new ArrayList<ChessMove>();
        if ((this.whiteKingHasMoved == false) && (!isInCheck(color))) {
            if ((color == TeamColor.WHITE) &&
                (this.whiteLeftRookHasMoved == false) &&
                (this.getBoard().getPiece(new ChessPosition(1, 3)) == null) &&
                (this.getBoard().getPiece(new ChessPosition(1, 4)) == null) &&
                (this.getBoard().getPiece(new ChessPosition(1, 2)) == null) &&
                (!leavesKingInCheck(color, new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 4), null))) &&
                (!leavesKingInCheck(color, new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null)))) {
                    castleMoves.add(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null));
            }
            if ((color == TeamColor.WHITE) &&
                    (this.whiteRightRookHasMoved == false) &&
                    (this.getBoard().getPiece(new ChessPosition(1, 7)) == null) &&
                    (this.getBoard().getPiece(new ChessPosition(1, 6)) == null) &&
                    (!leavesKingInCheck(color, new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 6), null))) &&
                    (!leavesKingInCheck(color, new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null)))) {
                castleMoves.add(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null));
            }
        }
        if ((this.blackKingHasMoved == false) && (!isInCheck(color))) {
            if ((color == TeamColor.BLACK) &&
                    (this.blackLeftRookHasMoved == false) &&
                    (this.getBoard().getPiece(new ChessPosition(8, 3)) == null) &&
                    (this.getBoard().getPiece(new ChessPosition(8, 4)) == null) &&
                    (this.getBoard().getPiece(new ChessPosition(8, 2)) == null) &&
                    (!leavesKingInCheck(color, new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 4), null))) &&
                    (!leavesKingInCheck(color, new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null)))) {
                castleMoves.add(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null));
            }
            if ((color == TeamColor.BLACK) &&
                    (this.blackRightRookHasMoved == false) &&
                    (this.getBoard().getPiece(new ChessPosition(8, 7)) == null) &&
                    (this.getBoard().getPiece(new ChessPosition(8, 6)) == null) &&
                    (!leavesKingInCheck(color, new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 6), null))) &&
                    (!leavesKingInCheck(color, new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null)))) {
                castleMoves.add(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null));
            }
        }
        return castleMoves;
    }

    public Collection<ChessMove> addEnPassant(TeamColor color) {
        Collection<ChessMove> enPassantMoves = new ArrayList<ChessMove>();
        if (this.isEnPassantable == null) {
            return enPassantMoves;
        }
        ChessPosition leftOfEnPassantable = null;
        ChessPosition rightOfEnPassantable = null;
        if (this.isEnPassantable.getColumn() > 1) {
            leftOfEnPassantable = new ChessPosition(this.isEnPassantable.getRow(), this.isEnPassantable.getColumn() - 1);
        }
        if (this.isEnPassantable.getColumn() < 8) {
            rightOfEnPassantable = new ChessPosition(this.isEnPassantable.getRow(), this.isEnPassantable.getColumn() + 1);
        }
        ChessPosition enPassantEnd = new ChessPosition((color == TeamColor.WHITE ? (this.isEnPassantable.getRow() + 1) : (this.isEnPassantable.getRow() - 1)), this.isEnPassantable.getColumn());
        if ((leftOfEnPassantable != null) &&
            (this.getBoard().getPiece(leftOfEnPassantable) != null) &&
            (this.getBoard().getPiece(leftOfEnPassantable).getPieceType() == ChessPiece.PieceType.PAWN) &&
            (this.getBoard().getPiece(leftOfEnPassantable).getTeamColor() == color) &&
            (this.getBoard().getPiece(this.isEnPassantable).getTeamColor() != color) &&
            (this.getBoard().getPiece(enPassantEnd) == null)) {
                enPassantMoves.add(new ChessMove(leftOfEnPassantable, enPassantEnd, null));
        }
        if ((rightOfEnPassantable != null) &&
            (this.getBoard().getPiece(rightOfEnPassantable) != null) &&
            (this.getBoard().getPiece(rightOfEnPassantable).getPieceType() == ChessPiece.PieceType.PAWN) &&
            (this.getBoard().getPiece(rightOfEnPassantable).getTeamColor() == color) &&
            (this.getBoard().getPiece(this.isEnPassantable).getTeamColor() != color) &&
            (this.getBoard().getPiece(enPassantEnd) == null)) {
                enPassantMoves.add(new ChessMove(rightOfEnPassantable, enPassantEnd, null));
        }
        return enPassantMoves;
    }

    public void castle(ChessGame game, ChessMove move) {
        ChessBoard board = game.getBoard();
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if (movingPiece == null) {
            return;
        }
        if (movingPiece.getPieceType() != ChessPiece.PieceType.KING) {
            return;
        }
        TeamColor color = movingPiece.getTeamColor();
        if (((color == TeamColor.WHITE && !game.whiteKingHasMoved) || (color == TeamColor.BLACK && !game.blackKingHasMoved)) &&
            ((Objects.equals(move.getEndPosition(), new ChessPosition(3, (color == TeamColor.WHITE ? 1 : 8)))) || (Objects.equals(move.getEndPosition(), new ChessPosition(7, (color == TeamColor.WHITE ? 1 : 8)))))) {
                if (color == TeamColor.WHITE) {
                    if (move.getEndPosition().getColumn() == 3) {
                        ChessPiece leftWhiteRook = board.getPiece(new ChessPosition(1, 1));
                        board.addPiece((new ChessPosition(1, 4)), leftWhiteRook);
                        board.addPiece((new ChessPosition(1, 1)), null);
                    }
                    if (move.getEndPosition().getColumn() == 7) {
                        ChessPiece rightWhiteRook = board.getPiece(new ChessPosition(1, 8));
                        board.addPiece((new ChessPosition(1, 6)), rightWhiteRook);
                        board.addPiece((new ChessPosition(1, 8)), null);
                    }
                }
                if (color == TeamColor.BLACK) {
                    if (move.getEndPosition().getColumn() == 3) {
                        ChessPiece leftBlackRook = board.getPiece(new ChessPosition(8, 1));
                        board.addPiece((new ChessPosition(8, 4)), leftBlackRook);
                        board.addPiece((new ChessPosition(8, 1)), null);
                    }
                    if (move.getEndPosition().getColumn() == 7) {
                        ChessPiece rightBlackRook = board.getPiece(new ChessPosition(8, 8));
                        board.addPiece((new ChessPosition(8, 6)), rightBlackRook);
                        board.addPiece((new ChessPosition(8, 8)), null);
                    }                }
        }
        if (color == TeamColor.WHITE) {
            game.whiteKingHasMoved = true;
            if (move.getEndPosition().getColumn() == 3) {
                game.whiteLeftRookHasMoved = true;
            }
            if (move.getEndPosition().getColumn() == 7) {
                game.whiteRightRookHasMoved = true;
            }
        }
        if (color == TeamColor.BLACK) {
            game.blackKingHasMoved = true;
            if (move.getEndPosition().getColumn() == 3) {
                game.blackLeftRookHasMoved = true;
            }
            if (move.getEndPosition().getColumn() == 7) {
                game.blackRightRookHasMoved = true;
            }
        }
    }

    public void enPassant(ChessGame game, ChessMove move) {
        ChessBoard board = game.getBoard();
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if (movingPiece == null) {
            return;
        }
        if (movingPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return;
        }
        if (game.isEnPassantable == null) {
            return;
        }
        TeamColor color = movingPiece.getTeamColor();
        if (((color == TeamColor.WHITE) && (Objects.equals(move.getEndPosition(), new ChessPosition(game.isEnPassantable.getRow() + 1, game.isEnPassantable.getColumn())))) ||
            ((color == TeamColor.BLACK) && (Objects.equals(move.getEndPosition(), new ChessPosition(game.isEnPassantable.getRow() - 1, game.isEnPassantable.getColumn()))))) {
                board.addPiece(game.isEnPassantable, null);
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieceThere = this.getBoard().getPiece(startPosition);
        if (pieceThere == null) {
            return null;
        } else {
            TeamColor pieceColor = pieceThere.getTeamColor();
            Collection<ChessMove> defaultMoves = pieceThere.pieceMoves(this.getBoard(), startPosition);
            Collection<ChessMove> allowedMoves = new ArrayList<ChessMove>();
            if ((startPosition.equals(new ChessPosition(1, 5))) && (!this.whiteKingHasMoved) ||
                (startPosition.equals(new ChessPosition(8, 5))) && (!this.blackKingHasMoved)) {
                    allowedMoves.addAll(addCastling(pieceColor));
            }
            if (this.isEnPassantable != null) {
                if ((startPosition.getRow() == isEnPassantable.getRow()) &&
                    ((startPosition.getColumn() == isEnPassantable.getColumn() + 1) || (startPosition.getColumn() == isEnPassantable.getColumn() - 1))) {
                        allowedMoves.addAll(addEnPassant(pieceColor));
                }
            }
            for (ChessMove possibleMove : defaultMoves) {
                if (!leavesKingInCheck(pieceColor, possibleMove)) {
                    allowedMoves.add(possibleMove);
                }
            }
            return allowedMoves;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = this.getBoard().getPiece(move.getStartPosition());
        if (movingPiece == null) {
            throw new InvalidMoveException(String.format("No piece found at row %d, col %d.",
                    move.getStartPosition().getRow(),
                    move.getStartPosition().getColumn()));
        }
        TeamColor movingPieceColor = movingPiece.getTeamColor();
        if (movingPieceColor != this.currentTurn) {
            throw new InvalidMoveException(String.format("%s cannot play on %s's turn.",
                    movingPieceColor,
                    this.currentTurn));
        }
        Collection<ChessMove> validMoves = this.validMoves(move.getStartPosition());
        if (validMoves != null && validMoves.contains(move)) {
            if (move.getPromotionPiece() == null) {
                this.getBoard().addPiece(move.getEndPosition(), movingPiece);
            } else {
                ChessPiece promotedPawn = new ChessPiece(movingPieceColor, move.getPromotionPiece());
                this.getBoard().addPiece(move.getEndPosition(), promotedPawn);
            }
            this.getBoard().addPiece(move.getStartPosition(), null);
            this.castle(this, move);
            this.enPassant(this, move);
            if ((movingPieceColor == TeamColor.WHITE) && (movingPiece.getPieceType() == ChessPiece.PieceType.KING)) {
                this.whiteKingHasMoved = true;
            }
            if ((movingPieceColor == TeamColor.BLACK) && (movingPiece.getPieceType() == ChessPiece.PieceType.KING)) {
                this.blackKingHasMoved = true;
            }
            if ((movingPieceColor == TeamColor.WHITE) && (movingPiece.getPieceType() == ChessPiece.PieceType.ROOK) && (Objects.equals(move.getStartPosition(), new ChessPosition(1, 1)))) {
                this.whiteLeftRookHasMoved = true;
            }
            if ((movingPieceColor == TeamColor.WHITE) && (movingPiece.getPieceType() == ChessPiece.PieceType.ROOK) && (Objects.equals(move.getStartPosition(), new ChessPosition(1, 8)))) {
                this.whiteRightRookHasMoved = true;
            }
            if ((movingPieceColor == TeamColor.BLACK) && (movingPiece.getPieceType() == ChessPiece.PieceType.ROOK) && (Objects.equals(move.getStartPosition(), new ChessPosition(8, 1)))) {
                this.blackLeftRookHasMoved = true;
            }
            if ((movingPieceColor == TeamColor.BLACK) && (movingPiece.getPieceType() == ChessPiece.PieceType.ROOK) && (Objects.equals(move.getStartPosition(), new ChessPosition(8, 8)))) {
                this.blackRightRookHasMoved = true;
            }
            this.isEnPassantable = null;
            if ((movingPiece.getPieceType() == ChessPiece.PieceType.PAWN) &&
                (((move.getStartPosition().getRow() == 2) && (move.getEndPosition().getRow() == 4) && (movingPieceColor == TeamColor.WHITE)) ||
                ((move.getStartPosition().getRow() == 7) && (move.getEndPosition().getRow() == 5) && (movingPieceColor == TeamColor.BLACK)))) {
                this.isEnPassantable = move.getEndPosition();
            }
            this.currentTurn = (this.currentTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
            System.out.println(this.board); //THIS LINE
        } else {
            throw new InvalidMoveException(String.format("Cannot move %s from row %d, col %d to row %d, col %d.",
                    (movingPiece == null ? null : movingPiece.getPieceType()),
                    move.getStartPosition().getRow(),
                    move.getStartPosition().getColumn(),
                    move.getEndPosition().getRow(),
                    move.getEndPosition().getColumn()));
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition;
        try {
            kingPosition = this.getKingPosition(teamColor);
        } catch (NoKingFoundException e) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition ijPosition = new ChessPosition(i, j);
                ChessPiece ijPiece = this.getBoard().getPiece(ijPosition);
                if (ijPiece != null && ijPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> ijMoves = ijPiece.pieceMoves(this.getBoard(), ijPosition);
                    for (ChessMove move : ijMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!this.isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> possibleMoves = generatePossibleMoves(this.getBoard(), teamColor);
        for (ChessMove possibleMove : possibleMoves) {
            if (!leavesKingInCheck(teamColor, possibleMove)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (this.isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> possibleMoves = generatePossibleMoves(this.getBoard(), teamColor);
        for (ChessMove possibleMove : possibleMoves) {
            if (!leavesKingInCheck(teamColor, possibleMove)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whiteLeftRookHasMoved == chessGame.whiteLeftRookHasMoved &&
                whiteRightRookHasMoved == chessGame.whiteRightRookHasMoved &&
                whiteKingHasMoved == chessGame.whiteKingHasMoved &&
                blackLeftRookHasMoved == chessGame.blackLeftRookHasMoved &&
                blackRightRookHasMoved == chessGame.blackRightRookHasMoved &&
                blackKingHasMoved == chessGame.blackKingHasMoved &&
                currentTurn == chessGame.currentTurn &&
                Objects.equals(board, chessGame.getBoard()) &&
                Objects.equals(isEnPassantable, chessGame.isEnPassantable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                currentTurn,
                board,
                whiteLeftRookHasMoved,
                whiteRightRookHasMoved,
                whiteKingHasMoved,
                blackLeftRookHasMoved,
                blackRightRookHasMoved,
                blackKingHasMoved,
                isEnPassantable);
    }
}
