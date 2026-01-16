package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves {

    private ChessBoard board;
    private ChessPosition position;

    public RookMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> generateMoves() {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        if (this.position.getRow() < 8) {
            int newRow = this.position.getRow() + 1;
            ChessPosition newPos = new ChessPosition(newRow, this.position.getColumn());
            ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
            while (newRow <= 8) {
                pieceAlreadyThere = this.board.getPiece(newPos);
                if ((pieceAlreadyThere != null) && (pieceAlreadyThere.getTeamColor() == board.getPiece(this.position).getTeamColor())) {
                    break;
                }
                ChessMove possibleMove = new ChessMove(this.position, newPos, null);
                possibleMoves.add(possibleMove);
                if (pieceAlreadyThere != null) {
                    break;
                }
                newRow++;
                newPos = new ChessPosition(newRow, this.position.getColumn());
            }
        }
        if (this.position.getRow() > 1) {
            int newRow = this.position.getRow() - 1;
            ChessPosition newPos = new ChessPosition(newRow, this.position.getColumn());
            ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
            while (newRow >= 1) {
                pieceAlreadyThere = this.board.getPiece(newPos);
                if ((pieceAlreadyThere != null) && (pieceAlreadyThere.getTeamColor() == board.getPiece(this.position).getTeamColor())) {
                    break;
                }
                ChessMove possibleMove = new ChessMove(this.position, newPos, null);
                possibleMoves.add(possibleMove);
                if (pieceAlreadyThere != null) {
                    break;
                }
                newRow--;
                newPos = new ChessPosition(newRow, this.position.getColumn());
            }
        }
        if (this.position.getColumn() < 8) {
            int newCol = this.position.getColumn() + 1;
            ChessPosition newPos = new ChessPosition(this.position.getRow(), newCol);
            ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
            while (newCol <= 8) {
                pieceAlreadyThere = this.board.getPiece(newPos);
                if ((pieceAlreadyThere != null) && (pieceAlreadyThere.getTeamColor() == board.getPiece(this.position).getTeamColor())) {
                    break;
                }
                ChessMove possibleMove = new ChessMove(this.position, newPos, null);
                possibleMoves.add(possibleMove);
                if (pieceAlreadyThere != null) {
                    break;
                }
                newCol++;
                newPos = new ChessPosition(this.position.getRow(), newCol);
            }
        }
        if (this.position.getColumn() > 1) {
            int newCol = this.position.getColumn() - 1;
            ChessPosition newPos = new ChessPosition(this.position.getRow(), newCol);
            ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
            while (newCol >= 1) {
                pieceAlreadyThere = this.board.getPiece(newPos);
                if ((pieceAlreadyThere != null) && (pieceAlreadyThere.getTeamColor() == board.getPiece(this.position).getTeamColor())) {
                    break;
                }
                ChessMove possibleMove = new ChessMove(this.position, newPos, null);
                possibleMoves.add(possibleMove);
                if (pieceAlreadyThere != null) {
                    break;
                }
                newCol--;
                newPos = new ChessPosition(this.position.getRow(), newCol);
            }
        }
        return possibleMoves;
    }

}
