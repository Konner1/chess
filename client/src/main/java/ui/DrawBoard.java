package ui;

import chess.*;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ui.EscapeSequences.*;

public class DrawBoard {

    private static final String EMPTY = "   ";

    public static void print(PrintStream out, ChessGame game, boolean whitePerspective) {
        print(out, game, whitePerspective, null);
    }

    public static void print(PrintStream out, ChessGame game, boolean whitePerspective, List<ChessMove> highlightMoves) {
        out.print(ERASE_SCREEN);
        out.println(SET_TEXT_ITALIC + "Chess Game >>>" + RESET_TEXT_ITALIC);

        int[] rows = whitePerspective ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        int[] cols = whitePerspective ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};

        Set<ChessPosition> highlightSquares = new HashSet<>();
        if (highlightMoves != null) {
            for (ChessMove move : highlightMoves) {
                highlightSquares.add(move.getEndPosition());
            }
        }

        printFileLabels(out, cols);
        ChessBoard board = game.getBoard();

        for (int row : rows) {
            setBorder(out);
            out.print(" " + row + " ");
            for (int col : cols) {
                ChessPosition pos = new ChessPosition(row, col);
                boolean isHighlight = highlightSquares.contains(pos);
                boolean light = (row + col) % 2 == 0;

                setSquareColor(out, light, isHighlight);

                ChessPiece piece = board.getPiece(pos);
                out.print(piece != null ? getPieceSymbol(piece) : EMPTY);
            }
            setBorder(out);
            out.print(" " + row + " ");
            out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
            out.println();
        }

        printFileLabels(out, cols);
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private static void setSquareColor(PrintStream out, boolean light, boolean highlight) {
        if (highlight) {
            String bg = light
                    ? SET_BG_COLOR_DARK_YELLOW
                    : SET_BG_COLOR_YELLOW;

            out.print(bg + SET_TEXT_COLOR_BLACK);
        } else if (light) {
            out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_BLACK);
        } else {
            out.print(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
        }
    }

    private static void setBorder(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE);
    }

    private static void printFileLabels(PrintStream out, int[] cols) {
        setBorder(out);
        out.print("    ");
        for (int col : cols) {
            out.print(" " + (char)('a' + col - 1) + " ");
        }
        out.print("  ");
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println();
    }

    private static String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case PAWN -> WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case ROOK -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN -> BLACK_PAWN;
            };
        };
    }
}

