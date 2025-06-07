package websocket;

import chess.ChessGame;

public interface DisplayHandler {
    void updateBoard(ChessGame game);
    void notify(String message);
    void error(String errorMessage);
}
