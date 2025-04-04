package websocket.messages;

import chess.Game; // adjust this import to match your Game class package

public class LoadGameMessage extends ServerMessage {
    public Game game;

    public LoadGameMessage(Game game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
