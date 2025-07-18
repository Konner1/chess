package server;
import chess.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;


public class Main {
    public static void main(String[] args) throws DataAccessException {

        DatabaseManager.createDatabase();
        DatabaseManager.createTables();
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server server = new Server();
        server.run(3000);
    }


}