package chess;

import java.util.Collection;
import java.util.HashSet;

public class PieceMovesCalc {
    public static Collection<ChessMove> calcMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        return switch (piece.getPieceType()) {
            case KING -> null;//KingMoveCalculator.getMoves(board, position);
            case QUEEN -> null;//QueenMoveCalculator.getMoves(board, position);
            case BISHOP -> getBishopMoves(board, position);
            case KNIGHT -> null;//KnightMoveCalculator.getMoves(board, position);
            case ROOK -> null;//RookMoveCalculator.getMoves(board, position);
            case PAWN -> null;//PawnMoveCalculator.getMoves(board, position);
        };
    }

    private static Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] direction : directions) {
            int row = position.getRow();
            int column = position.getColumn();
            while (true) {
                row += direction[0];
                column += direction[1];
                if (row < 1 || row > 8 || column < 1 || column > 8) {
                    break;
                }
                ChessPosition endPosition = new ChessPosition(row,column);
                moves.add(new ChessMove(position, endPosition, null));
            }

        }
        return moves;
    }

}
