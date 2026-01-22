package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves {

    private ChessBoard board;
    private ChessPosition position;

    public KnightMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> generateMoves() {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        int row = this.position.getRow();
        int col = this.position.getColumn();
        int newRow;
        int newCol;
        for (int r = -2; r <= 2; r++) {
            for (int c = -2; c <= 2; c++) {
                newRow = row + r;
                newCol = col + c;
                if ((c != 0) && (r != 0) && (Math.abs(r) != Math.abs(c)) && (newRow >= 1) && (newRow <= 8) && (newCol >= 1) && (newCol <= 8)) {
                    ChessPosition newPos = new ChessPosition(newRow, newCol);
                    ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
                    if ((pieceAlreadyThere == null) || (pieceAlreadyThere.getTeamColor() != this.board.getPiece(this.position).getTeamColor())) {
                        ChessMove possibleMove = new ChessMove(this.position, newPos, null);
                        possibleMoves.add(possibleMove);
                    }
                }
            }
        }
        return possibleMoves;
    }

}
