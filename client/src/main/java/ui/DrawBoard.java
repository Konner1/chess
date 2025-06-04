package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static ui.EscapeSequences.*;


public class DrawBoard {

    /* ── board geometry ─────────────────────────────────────────── */
    private static final int BOARD_SIZE = 8;        // 8×8
    private static final int SQUARE_WIDTH = 3;      // chars per square
    private static final String LIGHT = SET_BG_COLOR_LIGHT_GREY;
    private static final String DARK  = SET_BG_COLOR_DARK_GREY;
    private static final String FRAME = SET_BG_COLOR_BLUE;
    private static final String LABEL = SET_TEXT_COLOR_LIGHT_GREY + SET_TEXT_ITALIC;

    /* ── piece lookup tables ────────────────────────────────────── */
    private static final String[] WHITE_BACK = {
            WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
            WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK
    };
    private static final String[] BLACK_BACK = {
            BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
            BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK
    };
    private static final Set<String> WHITE_PIECES = Set.of(
            WHITE_KING, WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT, WHITE_PAWN
    );

    /* ── public façade ──────────────────────────────────────────── */
    public static void print(PrintStream out, boolean whitePerspective) {
        out.print(ERASE_SCREEN);
        out.print(FRAME);                                           // blue “table”
        out.print(moveCursorToLocation(1, 1));
        out.println(LABEL + "Chess Game >>>" + RESET_TEXT_ITALIC);

        drawFileLabels(out, whitePerspective);                      // top labels
        for (int row = 0; row < BOARD_SIZE; row++) {
            drawBoardRow(out, row, whitePerspective);
        }
        drawFileLabels(out, whitePerspective);                      // bottom labels

        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    /* ── helpers ────────────────────────────────────────────────── */
    private static void drawFileLabels(PrintStream out, boolean whitePerspective) {
        out.print(FRAME + LABEL + " ");                             // left padding
        for (int col = 0; col < BOARD_SIZE; col++) {
            int file = whitePerspective ? col : BOARD_SIZE - 1 - col;
            char letter = (char) ('a' + file);
            out.print(" " + letter + " ");
        }
        out.println(RESET_TEXT_ITALIC);
    }

    private static void drawBoardRow(PrintStream out, int row, boolean whitePerspective) {
        int rank = whitePerspective ? BOARD_SIZE - row : row + 1;

        // squares
        out.print(FRAME);                                           // left frame
        for (int col = 0; col < BOARD_SIZE; col++) {
            int file = whitePerspective ? col : BOARD_SIZE - 1 - col;
            String bg = ((rank + file) & 1) == 0 ? LIGHT : DARK;
            String piece = startingPiece(rank, file);
            out.print(bg);
            out.print(pieceColor(piece));
            out.print(piece);
        }

        // rank label at right edge
        out.print(FRAME + LABEL + " " + rank + RESET_TEXT_ITALIC);
        out.println();
    }

    private static String startingPiece(int rank, int file) {
        switch (rank) {
            case 1:  return WHITE_BACK[file];
            case 2:  return WHITE_PAWN;
            case 7:  return BLACK_PAWN;
            case 8:  return BLACK_BACK[file];
            default: return EMPTY;
        }
    }

    private static String pieceColor(String piece) {
        return WHITE_PIECES.contains(piece) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
    }

}
