package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves {

    private ChessBoard board;
    private ChessPosition position;

    public BishopMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> GenerateMoves() {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        if ((this.position.getRow() < 8) && (this.position.getColumn() < 8)) {
            int newRow = this.position.getRow() + 1;
            int newCol = this.position.getColumn() + 1;
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
            while ((newRow <= 8) && (newCol <= 8)) {
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
                newCol++;
                newPos = new ChessPosition(newRow, newCol);
            }
        }
        if ((this.position.getRow() < 8) && (this.position.getColumn() > 1)) {
            int newRow = this.position.getRow() + 1;
            int newCol = this.position.getColumn() - 1;
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
            while ((newRow <= 8) && (newCol >= 1)) {
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
                newCol--;
                newPos = new ChessPosition(newRow, newCol);
            }
        }
        if ((this.position.getRow() > 1) && (this.position.getColumn() < 8)) {
            int newRow = this.position.getRow() - 1;
            int newCol = this.position.getColumn() + 1;
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
            while ((newRow >= 1) && (newCol <= 8)) {
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
                newCol++;
                newPos = new ChessPosition(newRow, newCol);
            }
        }
        if ((this.position.getRow() > 1) && (this.position.getColumn() > 1)) {
            int newRow = this.position.getRow() - 1;
            int newCol = this.position.getColumn() - 1;
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece pieceAlreadyThere = this.board.getPiece(newPos);
            while ((newRow >= 1) && (newCol >= 1)) {
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
                newCol--;
                newPos = new ChessPosition(newRow, newCol);
            }
        }
        return possibleMoves;
    }

}
