package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves {

    private ChessBoard board;
    private ChessPosition position;

    public PawnMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> GenerateMoves() {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();

        return possibleMoves;
    }

}
