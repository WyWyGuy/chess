package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves {

    private ChessBoard board;
    private ChessPosition position;

    public KingMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> GenerateMoves() {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = this.position.getRow() + i;
                int newCol = this.position.getColumn() + j;
                if ((i != 0 || j != 0) && (1 <= newRow && newRow <= 8) && (1 <= newCol && newCol <= 8)) {
                    ChessPosition newPos = new ChessPosition(newRow, newCol);
                    ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
                    ChessPiece myPiece = this.board.getPiece(this.position);
                    if (pieceAlreadyThere == null || pieceAlreadyThere.getTeamColor() != myPiece.getTeamColor()) {
                        ChessMove possibleMove = new ChessMove(this.position, newPos, null);
                        possibleMoves.add(possibleMove);
                    }
                }
            }
        }
        return possibleMoves;
    }

}
