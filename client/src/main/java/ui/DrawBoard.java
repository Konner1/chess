package ui;

import static ui.EscapeSequences.*;

import java.io.PrintStream;

public class DrawBoard {

    private static final String[][] INITIAL_BOARD = new String[8][8];

    static {
        // Piece order for back ranks
        String[] backRank = { BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK };
        String[] whiteBackRank = { WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK };

        // Black pieces
        INITIAL_BOARD[0] = backRank;
        for (int col = 0; col < 8; col++) {
            INITIAL_BOARD[1][col] = BLACK_PAWN;
        }

        // Empty squares
        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < 8; col++) {
                INITIAL_BOARD[row][col] = EMPTY;
            }
        }

        // White pieces
        for (int col = 0; col < 8; col++) {
            INITIAL_BOARD[6][col] = WHITE_PAWN;
        }
        INITIAL_BOARD[7] = whiteBackRank;
    }


    public static void print(PrintStream out, boolean whitePerspective) {
        out.print(ERASE_SCREEN);
        out.println(SET_TEXT_ITALIC + "Chess Game >>>" + RESET_TEXT_ITALIC);

        int[] rows = whitePerspective ? new int[]{7,6,5,4,3,2,1,0} : new int[]{0,1,2,3,4,5,6,7};
        int[] cols = whitePerspective ? new int[]{0,1,2,3,4,5,6,7} : new int[]{7,6,5,4,3,2,1,0};

        // Top file labels
        printFileLabels(out, cols);

        for (int row : rows) {
            // Left rank label
            setBorder(out);
            out.print(" " + (row + 1) + " ");
            for (int col : cols) {
                boolean light = (row + col) % 2 == 0;
                setSquareColor(out, light);
                out.print(INITIAL_BOARD[row][col]);
            }
            setBorder(out);
            out.print(" " + (row + 1) + " ");
            out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
            out.println();
        }

        // Bottom file labels
        printFileLabels(out, cols);

        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private static void setSquareColor(PrintStream out, boolean light) {
        if (light) {
            out.print(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
        } else {
            out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_BLACK);
        }
    }

    private static void setBorder(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE);
    }

    private static void printFileLabels(PrintStream out, int[] cols) {
        setBorder(out);
        out.print("    "); // Left border space

        for (int col : cols) {
            out.print(" " + (char)('a' + col) + " ");
        }


        out.print("  ");// Extra right-side space
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println();
    }
}
