package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PieceMovesCalc {
    public static Collection<ChessMove> calcMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        return switch (piece.getPieceType()) {
            case KING -> getKingMoves(board,position);
            case QUEEN -> getQueenMoves(board,position);
            case BISHOP -> getBishopMoves(board, position);
            case KNIGHT -> getKnightMoves(board,position);
            case ROOK -> getRookMoves(board,position);
            case PAWN -> getPawnMoves(board, position);
        };
    }

    private static Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(position);
        if (myPiece == null) {
            return moves;
        }
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
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
                ChessPiece occupyingPiece = board.getPiece(endPosition);

                if (occupyingPiece == null) {
                    moves.add(new ChessMove(position, endPosition, null));
                }
                else {
                    if (occupyingPiece.getTeamColor() != myPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, endPosition, null)); // Capture
                    }
                    break;
                }
            }
        }
            return moves;
    }

    private static Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> rMoves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(position);
        if (myPiece == null) {
            return rMoves;
        }
        int[][] directions = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        for (int[] rDirection : directions) {
            int row = position.getRow();
            int column = position.getColumn();

            while (true) {
                row += rDirection[0];
                column += rDirection[1];
                if (row < 1 || row > 8 || column < 1 || column > 8) {
                    break;
                }
                ChessPosition endPosition = new ChessPosition(row,column);
                ChessPiece occupyingPiece = board.getPiece(endPosition);

                if (occupyingPiece == null) {
                    rMoves.add(new ChessMove(position, endPosition, null));
                }
                else {
                    if (occupyingPiece.getTeamColor() != myPiece.getTeamColor()) {
                        rMoves.add(new ChessMove(position, endPosition, null)); // Capture
                    }
                    break;
                }
            }
        }
            return rMoves;
    }

    private static Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> qMoves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(position);
        if (myPiece == null) {
            return qMoves;
        }
        int[][] directions = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}, {1,-1}, {-1,-1}, {1,1}, {-1,1}};
        for (int[] qDirection : directions) {
            int row = position.getRow();
            int column = position.getColumn();

            while (true) {
                row += qDirection[0];
                column += qDirection[1];
                if (row < 1 || row > 8 || column < 1 || column > 8) {
                    break;
                }
                ChessPosition endPosition = new ChessPosition(row,column);
                ChessPiece occupyingPiece = board.getPiece(endPosition);

                if (occupyingPiece == null) {
                    qMoves.add(new ChessMove(position, endPosition, null));
                }
                else {
                    if (occupyingPiece.getTeamColor() != myPiece.getTeamColor()) {
                        qMoves.add(new ChessMove(position, endPosition, null)); // Capture
                    }
                    break;
                }
            }
        }
            return qMoves;
    }

    private static Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> nMoves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(position);
        if (myPiece == null) {
            return nMoves;
        }
        int[][] directions = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        for (int[] nDirection : directions) {
            int row = position.getRow();
            int column = position.getColumn();
            row += nDirection[0];
            column += nDirection[1];
            if (row < 1 || row > 8 || column < 1 || column > 8) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(row, column);
            ChessPiece occupyingPiece = board.getPiece(endPosition);
            if (occupyingPiece == null || occupyingPiece.getTeamColor() != myPiece.getTeamColor()) {
                nMoves.add(new ChessMove(position, endPosition, null));
            }
        }
            return nMoves;
    }

    private static Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> kMoves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(position);
        if (myPiece == null) {
            return kMoves;
        }
        int[][] directions = {{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};
        for (int[] kDirection : directions) {
            int row = position.getRow();
            int column = position.getColumn();
            row += kDirection[0];
            column += kDirection[1];
            if (row < 1 || row > 8 || column < 1 || column > 8) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(row, column);
            ChessPiece occupyingPiece = board.getPiece(endPosition);
            if (occupyingPiece == null || occupyingPiece.getTeamColor() != myPiece.getTeamColor()) {
                kMoves.add(new ChessMove(position, endPosition, null));
            }
        }
        return kMoves;
    }

    private static Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> pMoves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(position);
        if (myPiece == null) {
            return pMoves;
        }
        int row = position.getRow();
        int column = position.getColumn();
        int direction;
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }
        int startRow;
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            startRow = 2;
        } else {
            startRow = 7;
        }
        int endRow;
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            endRow = 8;
        } else {
            endRow = 1;
        }

        List<ChessPiece.PieceType> promotionOptions = List.of(
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT
        );
        if (row == startRow){
            ChessPosition endPosition = new ChessPosition(row + direction, column);
            ChessPosition endPosition2 = new ChessPosition(row + (direction *2), column);
            pMoves.add(new ChessMove(position, endPosition, null));
            pMoves.add(new ChessMove(position, endPosition2,null));
        } else{
            ChessPosition endPosition = new ChessPosition(row + direction, column);
            if (endPosition.getRow() == endRow){
                pMoves.add(new ChessMove(position, endPosition,null ));
            }
            pMoves.add(new ChessMove(position, endPosition, null));
        }






        return pMoves;
    }

}
