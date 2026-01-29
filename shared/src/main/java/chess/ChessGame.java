package chess;

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
                ChessPiece ijPiece = this.board.getPiece(ijPosition);
                if (ijPiece != null && ijPiece.getTeamColor() == color && ijPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return ijPosition;
                }
            }
        }
        throw new NoKingFoundException(String.format("%s king not found on the board.", color));
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieceThere = this.board.getPiece(startPosition);
        if (pieceThere == null) {
            return null;
        } else {
            //Need some more logic here
            return pieceThere.pieceMoves(this.board, startPosition);
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = this.board.getPiece(move.getStartPosition());
        Collection<ChessMove> validMoves = this.validMoves(move.getStartPosition());
        if (validMoves != null && validMoves.contains(move)) {
            this.board.addPiece(move.getEndPosition(), movingPiece);
            this.board.addPiece(move.getStartPosition(), null);
            //If castling, move rook too. (test by "king moves for the first time and it's two?")
            //If enPassanting, delete pawn too. (test by "pawn captures behind enPassantable")
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
                ChessPiece ijPiece = this.board.getPiece(ijPosition);
                if (ijPiece != null && ijPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> ijMoves = ijPiece.pieceMoves(this.board, ijPosition);
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
        ChessPosition kingPosition;
        try {
            kingPosition = this.getKingPosition(teamColor);
        } catch (NoKingFoundException e) {
            return false;
        }
        return false; //Need more logic
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
        //Needs more logic
        //Not in check, All pieces moves combined are zero moves.
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
                Objects.equals(board, chessGame.board) &&
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
