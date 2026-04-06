package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    private ChessGame game;
    private boolean isWhite;

    public LoadGameMessage(ChessGame game, boolean isWhite) {
        super(ServerMessage.ServerMessageType.LOAD_GAME);
        this.game = game;
        this.isWhite = isWhite;
    }

    public ChessGame getGame() {
        return game;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

}
