package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] layout;

    public ChessBoard() {
        layout = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.layout[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.layout[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece.PieceType[] nonPawnOrder = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };
        for (int i = 0; i < 8; i++) {
            ChessPiece whitePiece = new ChessPiece(ChessGame.TeamColor.WHITE, nonPawnOrder[i]);
            ChessPosition whitePos = new ChessPosition(1, i+1);
            this.addPiece(whitePos, whitePiece);
        }
        for (int i = 0; i < 8; i++) {
            ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPosition whitePos = new ChessPosition(2, i+1);
            this.addPiece(whitePos, whitePawn);
        }
        for (int i = 0; i < 8; i++) {
            ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            ChessPosition blackPos = new ChessPosition(7, i+1);
            this.addPiece(blackPos, blackPawn);
        }
        for (int i = 0; i < 8; i++) {
            ChessPiece blackPiece = new ChessPiece(ChessGame.TeamColor.BLACK, nonPawnOrder[i]);
            ChessPosition blackPos = new ChessPosition(8, i+1);
            this.addPiece(blackPos, blackPiece);
        }
        for (int i = 3; i <= 6; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition emptySpace = new ChessPosition(i, j);
                this.addPiece(emptySpace, null);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(layout, that.layout);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(layout);
    }
}
