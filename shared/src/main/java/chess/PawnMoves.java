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

    public Collection<ChessMove> generateMoves() {
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        ChessPiece thisPawn = this.board.getPiece(this.position);
        ChessGame.TeamColor thisPawnColor = thisPawn.getTeamColor();
        boolean isWhite = thisPawnColor == ChessGame.TeamColor.WHITE;
        if (((isWhite) && (this.position.getRow() >= 8)) || ((!isWhite) && (this.position.getRow() <= 1))) {
            return possibleMoves;
        }
            ChessPosition forwardByOne = new ChessPosition(this.position.getRow() + (isWhite ? 1 : -1), this.position.getColumn());
            ChessPiece pieceDirectlyInFront = this.board.getPiece(forwardByOne);
            if (pieceDirectlyInFront == null) {
                if (((isWhite) && (forwardByOne.getRow() < 8)) || ((!isWhite) && (forwardByOne.getRow() > 1))) {
                    ChessMove possibleMove = new ChessMove(this.position, forwardByOne, null);
                    possibleMoves.add(possibleMove);
                } else if (((isWhite) && (forwardByOne.getRow() == 8)) || ((!isWhite) && (forwardByOne.getRow() == 1))) {
                    ChessMove possibleQueenPromotion = new ChessMove(this.position, forwardByOne, ChessPiece.PieceType.QUEEN);
                    possibleMoves.add(possibleQueenPromotion);
                    ChessMove possibleRookPromotion = new ChessMove(this.position, forwardByOne, ChessPiece.PieceType.ROOK);
                    possibleMoves.add(possibleRookPromotion);
                    ChessMove possibleBishopPromotion = new ChessMove(this.position, forwardByOne, ChessPiece.PieceType.BISHOP);
                    possibleMoves.add(possibleBishopPromotion);
                    ChessMove possibleKnightPromotion = new ChessMove(this.position, forwardByOne, ChessPiece.PieceType.KNIGHT);
                    possibleMoves.add(possibleKnightPromotion);
                }
                if (((isWhite) && (this.position.getRow() == 2)) || ((!isWhite) && (this.position.getRow() == 7))) {
                    ChessPosition forwardByTwo = new ChessPosition(this.position.getRow() + (isWhite ? 2 : -2), this.position.getColumn());
                    ChessPiece pieceTwoInFront = this.board.getPiece(forwardByTwo);
                    if (pieceTwoInFront == null) {
                        ChessMove possibleMoveTwo = new ChessMove(this.position, forwardByTwo, null);
                        possibleMoves.add(possibleMoveTwo);
                    }
                }
            }
            if (this.position.getColumn() > 1) {
                ChessPosition potentialLeftCapture = new ChessPosition(this.position.getRow() + (isWhite ? 1 : -1), this.position.getColumn() - 1);
                ChessPiece potentialLeftCapturePiece = this.board.getPiece(potentialLeftCapture);
                if ((potentialLeftCapturePiece != null) &&
                        (potentialLeftCapturePiece.getTeamColor() != this.board.getPiece(this.position).getTeamColor())) {
                    if (((isWhite) && (potentialLeftCapture.getRow() < 8)) || ((!isWhite) && (potentialLeftCapture.getRow() > 1))) {
                        ChessMove possibleLeftCapture = new ChessMove(this.position, potentialLeftCapture, null);
                        possibleMoves.add(possibleLeftCapture);
                    } else if (((isWhite) && (potentialLeftCapture.getRow() == 8)) || ((!isWhite) && (potentialLeftCapture.getRow() == 1))) {
                        ChessMove possibleCaptureQueenPromotion = new ChessMove(this.position, potentialLeftCapture, ChessPiece.PieceType.QUEEN);
                        possibleMoves.add(possibleCaptureQueenPromotion);
                        ChessMove possibleCaptureRookPromotion = new ChessMove(this.position, potentialLeftCapture, ChessPiece.PieceType.ROOK);
                        possibleMoves.add(possibleCaptureRookPromotion);
                        ChessMove possibleCaptureBishopPromotion = new ChessMove(this.position, potentialLeftCapture, ChessPiece.PieceType.BISHOP);
                        possibleMoves.add(possibleCaptureBishopPromotion);
                        ChessMove possibleCaptureKnightPromotion = new ChessMove(this.position, potentialLeftCapture, ChessPiece.PieceType.KNIGHT);
                        possibleMoves.add(possibleCaptureKnightPromotion);
                    }
                }
            }
            if (this.position.getColumn() < 8) {
                ChessPosition potentialRightCapture = new ChessPosition(this.position.getRow() + (isWhite ? 1 : -1), this.position.getColumn() + 1);
                ChessPiece potentialRightCapturePiece = this.board.getPiece(potentialRightCapture);
                if ((potentialRightCapturePiece != null) &&
                        (potentialRightCapturePiece.getTeamColor() != this.board.getPiece(this.position).getTeamColor())) {
                    if (((isWhite) && (potentialRightCapture.getRow() < 8)) || ((!isWhite) && (potentialRightCapture.getRow() > 1))) {
                        ChessMove possibleRightCapture = new ChessMove(this.position, potentialRightCapture, null);
                        possibleMoves.add(possibleRightCapture);
                    } else if (((isWhite) && (potentialRightCapture.getRow() == 8)) || ((!isWhite) && (potentialRightCapture.getRow() == 1))) {
                        ChessMove possibleCaptureQueenPromotion = new ChessMove(this.position, potentialRightCapture, ChessPiece.PieceType.QUEEN);
                        possibleMoves.add(possibleCaptureQueenPromotion);
                        ChessMove possibleCaptureRookPromotion = new ChessMove(this.position, potentialRightCapture, ChessPiece.PieceType.ROOK);
                        possibleMoves.add(possibleCaptureRookPromotion);
                        ChessMove possibleCaptureBishopPromotion = new ChessMove(this.position, potentialRightCapture, ChessPiece.PieceType.BISHOP);
                        possibleMoves.add(possibleCaptureBishopPromotion);
                        ChessMove possibleCaptureKnightPromotion = new ChessMove(this.position, potentialRightCapture, ChessPiece.PieceType.KNIGHT);
                        possibleMoves.add(possibleCaptureKnightPromotion);
                    }
                }
            }
        return possibleMoves;
    }

}
