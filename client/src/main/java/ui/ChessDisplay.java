package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;

public class ChessDisplay {

    private final String boarderBg = SET_BG_COLOR_LIGHT_GREY;
    private final String boarderColor = SET_TEXT_COLOR_BLACK;
    private final String whiteBg = SET_BG_COLOR_WHITE;
    private final String whiteColor = SET_TEXT_COLOR_WHITE;
    private final String blackBg = SET_BG_COLOR_BLACK;
    private final String blackColor = SET_TEXT_COLOR_BLACK;
    private final String selectedWhiteColor = SET_TEXT_COLOR_WHITE;
    private final String selectedWhiteBgColor = SET_BG_COLOR_RED;
    private final String selectedBlackColor = SET_TEXT_COLOR_BLACK;
    private final String selectedBlackBgColor = SET_BG_COLOR_RED;
    private final String legalWhiteColor = SET_TEXT_COLOR_WHITE;
    private final String legalWhiteBgColor = SET_BG_COLOR_GREEN;
    private final String legalBlackColor = SET_TEXT_COLOR_BLACK;
    private final String legalBlackBgColor = SET_BG_COLOR_DARK_GREEN;
    private final Set<String> wideCharacters = Set.of(
            WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING,
            BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING
    );

    public void drawBoard(ChessGame game, boolean whitePerspective, ChessPosition selected, HashSet<ChessPosition> highlights) {
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
                int xIndex = whitePerspective ? 8 - i : i + 1;
                int yIndex = whitePerspective ? j + 1 : 8 - j;
                ChessPosition currRender = new ChessPosition(xIndex, yIndex);
                if (currRender.equals(selected)) {
                    System.out.print(bgColorWhite ? selectedWhiteBgColor : selectedBlackBgColor);
                    System.out.print(bgColorWhite ? selectedBlackColor : selectedWhiteColor);
                } else if (highlights.contains(currRender)) {
                    System.out.print(bgColorWhite ? legalWhiteBgColor : legalBlackBgColor);
                    System.out.print(bgColorWhite ? legalBlackColor : legalWhiteColor);
                } else {
                    System.out.print(bgColorWhite ? whiteBg : blackBg);
                    System.out.print(bgColorWhite ? blackColor : whiteColor);
                }
                ChessPiece piece = rows[i][j];
                String posChar;
                if (piece == null) {
                    posChar = " ";
                } else {
                    posChar = determinePiece(piece);
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

    private String determinePiece(ChessPiece piece) {
        chess.ChessPiece.PieceType type = piece.getPieceType();
        String posChar;
        switch (type) {
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
        return posChar;
    }

    public void renderHighlights(ChessGame game, boolean whitePerspective, int row, int col) {
        ChessPosition selected = new ChessPosition(row + 1, col + 1);
        HashSet<ChessPosition> highlights = new HashSet<>();
        Collection<ChessMove> validMoves = game.validMoves(selected);
        if (validMoves != null) {
            for (ChessMove move : validMoves) {
                if (move.getStartPosition().equals(selected)) {
                    highlights.add(move.getEndPosition());
                }
            }
        }

        drawBoard(game, whitePerspective, selected, highlights);
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
