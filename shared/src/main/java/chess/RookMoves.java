package chess;

import java.util.Collection;

public class RookMoves {

    private ChessBoard board;
    private ChessPosition position;

    public RookMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> GenerateMoves() {
        throw new RuntimeException("Not implemented");
    }

}
