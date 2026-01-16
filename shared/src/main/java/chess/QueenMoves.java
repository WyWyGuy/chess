package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves {

    private ChessBoard board;
    private ChessPosition position;

    public QueenMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> generateMoves() {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        RookMoves rookMoveGenerator = new RookMoves(this.board, this.position);
        BishopMoves bishopMoveGenerator = new BishopMoves(this.board, this.position);
        possibleMoves.addAll(rookMoveGenerator.generateMoves());
        possibleMoves.addAll(bishopMoveGenerator.generateMoves());
        return possibleMoves;
    }

}
