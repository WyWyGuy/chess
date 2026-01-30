package chess;

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
        this.board = new ChessBoard(original.board);
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
                ChessPiece ijPiece = this.board.getPiece(ijPosition);
                if (ijPiece != null && ijPiece.getTeamColor() == color && ijPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return ijPosition;
                }
            }
        }
        throw new NoKingFoundException(String.format("%s king not found on the board.", color));
    }

    public boolean leavesKingInCheck(TeamColor color, ChessMove move) {
        ChessGame gameCopy = new ChessGame(this);
        ChessPiece movingPiece = gameCopy.board.getPiece(move.getStartPosition());
        gameCopy.board.addPiece(move.getEndPosition(), movingPiece);
        gameCopy.board.addPiece(move.getStartPosition(), null);
        //This logic needs changed to reflect promotions in makeMove
        //remove pawn if en passanting
        //move rook if castling
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
        //Add castling and en Passant using their functions here!
        return possibleMoves;
    }

    //public Collection<ChessMove> addCastling(TeamColor color) {
        //if king of matching color hasn't moved
        //AND rook hasn't moved
        //AND space is open for king to move
        //AND space is open for rook to move
        //AND (queenside castling) B is cleared for Rook to pass through
        //AND king isn't in check
        //AND middle square isn't in check
        //AND end square isn't in check
        //Then add that possible move
        //return castleMoves. (may be empty)
    //}

    //public Collection<ChessMove> addEnPassant(TeamColor color) {
        //if enPassantable exists
        //AND you have a pawn right next to it
        //AND the spot behind enPassantable is empty
        //Then add that possible move (There might be two if you have two pawns sandwiching enPassantable)
        //return enPassantMoves (may be empty)
    //}

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
            TeamColor pieceColor = pieceThere.getTeamColor();
            Collection<ChessMove> defaultMoves = pieceThere.pieceMoves(this.board, startPosition);
            Collection<ChessMove> allowedMoves = new ArrayList<ChessMove>();
            //Add castling if possible and isn't in check/through check/end in check
            //Add enPassant if it's possible
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
        ChessPiece movingPiece = this.board.getPiece(move.getStartPosition());
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
                this.board.addPiece(move.getEndPosition(), movingPiece);
            } else {
                ChessPiece promotedPawn = new ChessPiece(movingPieceColor, move.getPromotionPiece());
                this.board.addPiece(move.getEndPosition(), promotedPawn);
            }
            this.board.addPiece(move.getStartPosition(), null);
            //If castling, move rook too. (test by "king moves two?" - make a function for this?)
            //If enPassanting, delete pawn too. (test by "pawn captures behind enPassantable" - make a function for this?)
            this.currentTurn = (this.currentTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
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
        if (!this.isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> possibleMoves = generatePossibleMoves(this.board, teamColor);
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
        Collection<ChessMove> possibleMoves = generatePossibleMoves(this.board, teamColor);
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
