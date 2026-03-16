package ui;

import java.util.Set;

import static ui.EscapeSequences.*;

public class ChessDisplay {

    private String BOARDER_BG = SET_BG_COLOR_LIGHT_GREY;
    private String BOARDER_COLOR = SET_TEXT_COLOR_BLACK;
    private String WHITE_BG = SET_BG_COLOR_WHITE;
    private String WHITE_COLOR = SET_TEXT_COLOR_WHITE;
    private String BLACK_BG = SET_BG_COLOR_BLACK;
    private String BLACK_COLOR = SET_TEXT_COLOR_BLACK;

    public void drawBoard(int gameID, boolean whitePerspective) {
        Set<String> WIDE_CHARACTERS = Set.of(
                WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING,
                BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING
        );
        String[] letters = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] row1 = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
        String[] row2 = {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN};
        String[] row7 = {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN};
        String[] row8 = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        if (!whitePerspective) {
            reverse(letters);
            reverse(numbers);
        }

        boolean bgColorWhite = true;

        System.out.print(BOARDER_COLOR + BOARDER_BG);
        for (int i = 0; i < letters.length; i++) {
            System.out.print(" " + letters[i] + " ");
        }
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");

        for (int i = 0; i < numbers.length; i++) {
            System.out.print(BOARDER_COLOR + BOARDER_BG);
            System.out.print(" " + numbers[i] + " ");

            for (int j = 0; j < 8; j++) {
                System.out.print(bgColorWhite ? WHITE_BG : BLACK_BG);
                System.out.print(bgColorWhite ? BLACK_COLOR : WHITE_COLOR);
                String[] row = {" ", " ", " ", " ", " ", " ", " ", " "};
                if (i == 0) {
                    row = whitePerspective ? row8 : row1;
                } else if (i == 1) {
                    row = whitePerspective ? row7 : row2;
                } else if (i == 6) {
                    row = whitePerspective ? row2 : row7;
                } else if (i == 7) {
                    row = whitePerspective ? row1 : row8;
                }
                String posChar = row[j];
                if (WIDE_CHARACTERS.contains(posChar)) {
                    System.out.print(posChar);
                } else {
                    System.out.print(" " + posChar + " ");
                }
                if (j < 7) {
                    bgColorWhite = !bgColorWhite;
                }
            }

            System.out.print(BOARDER_COLOR + BOARDER_BG);
            System.out.print(" " + numbers[i] + " ");
            System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");
        }

        System.out.print(BOARDER_COLOR + BOARDER_BG);
        for (int i = 0; i < letters.length; i++) {
            System.out.print(" " + letters[i] + " ");
        }
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");
    }

    private static void reverse(String[] arr) {
        int left = 0;
        int right = arr.length - 1;
        while (left < right) {
            String temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }

}
