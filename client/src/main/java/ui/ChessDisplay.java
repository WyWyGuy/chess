package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Set;

import static ui.EscapeSequences.*;

public class ChessDisplay {

    private String boarderBg = SET_BG_COLOR_LIGHT_GREY;
    private String boarderColor = SET_TEXT_COLOR_BLACK;
    private String whiteBg = SET_BG_COLOR_WHITE;
    private String whiteColor = SET_TEXT_COLOR_WHITE;
    private String blackBg = SET_BG_COLOR_BLACK;
    private String blackColor = SET_TEXT_COLOR_BLACK;

    public void drawBoard(ChessGame game, boolean whitePerspective) {
        Set<String> wideCharacters = Set.of(
                WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING,
                BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING
        );
        String[] letters = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        String[] numbers = {"8", "7", "6", "5", "4", "3", "2", "1"};
        ChessPiece[][] rows = new ChessPiece[8][8];
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                rows[7 - i][j] = game.getBoard().getPiece(new ChessPosition(i + 1, j + 1));
            }
        }
        if (!whitePerspective) {
            reverse(letters);
            reverse(numbers);
            for (int i = 0; i <= 7; i++) {
                reverse(rows[i]);
            }
            reverse(rows);
        }

        boolean bgColorWhite = true;

        System.out.print(boarderColor + boarderBg);
        for (int i = 0; i < letters.length; i++) {
            System.out.print(" " + letters[i] + " ");
        }
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");

        for (int i = 0; i < numbers.length; i++) {
            System.out.print(boarderColor + boarderBg);
            System.out.print(" " + numbers[i] + " ");

            for (int j = 0; j < 8; j++) {
                System.out.print(bgColorWhite ? whiteBg : blackBg);
                System.out.print(bgColorWhite ? blackColor : whiteColor);
                ChessPiece piece = rows[i][j];
                String posChar;
                if (piece == null) {
                    posChar = " ";
                } else {
                    switch (piece.getPieceType()) {
                        case KING:
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                posChar = WHITE_KING;
                            } else {
                                posChar = BLACK_KING;
                            }
                            break;
                        case QUEEN:
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                posChar = WHITE_QUEEN;
                            } else {
                                posChar = BLACK_QUEEN;
                            }
                            break;
                        case ROOK:
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                posChar = WHITE_ROOK;
                            } else {
                                posChar = BLACK_ROOK;
                            }
                            break;
                        case KNIGHT:
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                posChar = WHITE_KNIGHT;
                            } else {
                                posChar = BLACK_KNIGHT;
                            }
                            break;
                        case BISHOP:
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                posChar = WHITE_BISHOP;
                            } else {
                                posChar = BLACK_BISHOP;
                            }
                            break;
                        case PAWN:
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                posChar = WHITE_PAWN;
                            } else {
                                posChar = BLACK_PAWN;
                            }
                            break;
                        default:
                            posChar = " ";
                    }
                }
                if (wideCharacters.contains(posChar)) {
                    System.out.print(posChar);
                } else {
                    System.out.print(" " + posChar + " ");
                }
                if (j < 7) {
                    bgColorWhite = !bgColorWhite;
                }
            }

            System.out.print(boarderColor + boarderBg);
            System.out.print(" " + numbers[i] + " ");
            System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");
        }

        System.out.print(boarderColor + boarderBg);
        for (int i = 0; i < letters.length; i++) {
            System.out.print(" " + letters[i] + " ");
        }
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");
    }

    private static <T> void reverse(T[] arr) {
        int left = 0;
        int right = arr.length - 1;
        while (left < right) {
            T temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }

}
