package ui;

import static ui.EscapeSequences.*;

public class ChessDisplay {

    public void drawBoard(int gameID, boolean whitePerspective) {
        String[] letters = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] row1 = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
        String[] row2 = {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN};
        String[] row7 = {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN};
        String[] row8 = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}
        if (!whitePerspective) {
            reverse(letters);
            reverse(numbers);
        }

        boolean bgColorWhite = true;

        System.out.print(SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY);
        for (int i = 0; i < letters.length; i++) {
            System.out.print(" " + letters[i] + " ");
        }
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");

        for (int i = 0; i < numbers.length; i++) {
            System.out.print(SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY);
            System.out.print(" " + numbers[i] + " ");

            for (int j = 0; j < 8; j++) {
                System.out.print(bgColorWhite ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);
                System.out.print(bgColorWhite ? SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_WHITE);
                System.out.print(" " + "X" + " ");  //Print actual character
                if (j < 7) {
                    bgColorWhite = !bgColorWhite;
                }
            }

            System.out.print(SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY);
            System.out.print(" " + numbers[i] + " ");
            System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");
        }

        System.out.print(SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY);
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
