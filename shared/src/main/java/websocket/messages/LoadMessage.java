package websocket.messages;

import chess.ChessGame;

public class LoadMessage extends ServerMessage{
    public final ChessGame game;

    public LoadMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}
