package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceType type = board.getPiece(myPosition).getPieceType();
        Collection<ChessMove> possibleMoves;
        switch (type) {
            case PieceType.KING:
                KingMoves kingMoveGenerator = new KingMoves(board, myPosition);
                possibleMoves = kingMoveGenerator.generateMoves();
                break;
            case PieceType.QUEEN:
                QueenMoves queenMoveGenerator = new QueenMoves(board, myPosition);
                possibleMoves = queenMoveGenerator.generateMoves();
                break;
            case PieceType.ROOK:
                RookMoves rookMoveGenerator = new RookMoves(board, myPosition);
                possibleMoves = rookMoveGenerator.generateMoves();
                break;
            case PieceType.BISHOP:
                BishopMoves bishopMoveGenerator = new BishopMoves(board, myPosition);
                possibleMoves = bishopMoveGenerator.generateMoves();
                break;
            case PieceType.KNIGHT:
                KnightMoves knightMoveGenerator = new KnightMoves(board, myPosition);
                possibleMoves = knightMoveGenerator.generateMoves();
                break;
            case PieceType.PAWN:
                PawnMoves pawnMoveGenerator = new PawnMoves(board, myPosition);
                possibleMoves = pawnMoveGenerator.generateMoves();
                break;
            default:
                throw new RuntimeException("Cannot list moves for unknown piece");
        }
        return possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
