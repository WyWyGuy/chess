package ui;

import java.util.Set;

import static ui.EscapeSequences.*;

public class ChessDisplay {

    private String boarderBg = SET_BG_COLOR_LIGHT_GREY;
    private String boarderColor = SET_TEXT_COLOR_BLACK;
    private String whiteBg = SET_BG_COLOR_WHITE;
    private String whiteColor = SET_TEXT_COLOR_WHITE;
    private String blackBg = SET_BG_COLOR_BLACK;
    private String blackColor = SET_TEXT_COLOR_BLACK;

    public void drawBoard(int gameID, boolean whitePerspective) {
        Set<String> wideCharacters = Set.of(
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
            reverse(row1);
            reverse(row8);
            //TODO
            // Must reverse the rows and row order when printing from black perspective
            // So reverse each internal row, then the outer rows
            // that way, the printing logic is always the same.
            // Steps:
            // 1) Load the board and create an 8x8 array of pieces from white perspective
            // 2) If not white or observer, reverse all internal arrays and the order of the arrays
            // 3) Loop through and print the board, referencing the array for what piece.
            // 4) We may need to add logic for highlighting possible moves if the curr_position is in possible moves
            // 5) Then we should perhaps keep i, j indices to track which positions should be highlighted
            // 6) And we can do 7-i and 7-j to "reverse" them when we do the black perspective
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
