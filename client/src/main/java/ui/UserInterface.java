package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import client.WebSocketFacade;
import model.AuthData;
import model.GameData;

import java.util.*;

public class UserInterface {

    private static ServerFacade serverFacade;
    private static WebSocketFacade webSocketFacade;
    private static Scanner scanner = new Scanner(System.in);
    private List<GameData> games;
    private String username;
    private ChessDisplay chessDisplay = new ChessDisplay();
    private int port;
    private AuthData auth;
    public ChessGame currState;
    private boolean renderWhite;

    public UserInterface(String hostname, int port) {
        serverFacade = new ServerFacade(hostname, port);
        this.port = port;
    }

    public void startMenu() {
        boolean running = true;
        while (running) {
            System.out.print("Enter a command (type 'help' for a list of commands): ");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "quit" -> {
                    running = executeQuit();
                }
                case "help" -> executeStartHelp();
                case "login" -> executeLogin();
                case "register" -> executeRegister();
            }
        }
    }

    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.print("Enter a command (type 'help' for a list of commands): ");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "logout" -> {
                    running = executeLogout();
                }
                case "help" -> executeMainHelp();
                case "create game" -> executeCreateGame();
                case "list games" -> executeListGames();
                case "play game" -> executePlayGame();
                case "observe game" -> executeObserveGame();
            }
        }
    }

    private void gameMenu(int gameID, boolean isWhite) {
        try {
            webSocketFacade = new WebSocketFacade(this);
            webSocketFacade.connect("ws://localhost:" + port + "/ws");
            webSocketFacade.join(gameID, auth.authToken(), username, isWhite);
        } catch (Exception e) {
            System.out.println("An error occurred while trying to connect to the game");
            e.printStackTrace();
        }
        renderWhite = isWhite;
        boolean running = true;
        while (running) {
            System.out.print("Enter a command (type 'help' for a list of commands): ");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "leave" -> running = executeGameLeave(gameID, auth.authToken(), username, isWhite);
                case "help" -> executeGameHelp();
                case "redraw chess board" -> renderChessBoard(currState);
                case "make move" -> executeMakeMove(gameID, auth.authToken(), username, isWhite);
                case "resign" -> executeResign(gameID, auth.authToken(), username);
                case "highlight legal moves" -> executeHighlightMoves(currState, isWhite);
            }
        }
    }

    private void observeMenu(int gameID) {
        try {
            webSocketFacade = new WebSocketFacade(this);
            webSocketFacade.connect("ws://localhost:" + port + "/ws");
            webSocketFacade.observe(gameID, auth.authToken(), username);
        } catch (Exception e) {
            System.out.println("An error occurred while trying to connect to the game");
            e.printStackTrace();
        }
        boolean running = true;
        renderWhite = true;
        while (running) {
            System.out.print("Enter a command (type 'help' for a list of commands): ");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "leave" -> running = executeObserverLeave(gameID, auth.authToken(), username);
                case "help" -> executeObserverHelp();
                case "redraw chess board" -> renderChessBoard(currState);
                case "highlight legal moves" -> executeHighlightMoves(currState, true);
            }
        }
    }

    private boolean executeQuit() {
        System.out.println("Quitting chess client...");
        return false;
    }

    private boolean executeLogout() {
        System.out.println("Logging out...");
        try {
            serverFacade.logout();
            games = null;
            username = null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void executeStartHelp() {
        System.out.println("Commands:");
        System.out.println("Help - shows this menu");
        System.out.println("Login - login to the chess server");
        System.out.println("Quit - exit the program");
        System.out.println("Register - register a new user on the chess server");
    }

    private void executeLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        try {
            auth = serverFacade.login(username, password);
            System.out.println("Successfully logged in as " + username);
            this.username = username;
            mainMenu();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeRegister() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        try {
            auth = serverFacade.register(username, password, email);
            System.out.println("Successfully registered new user: " + username);
            this.username = username;
            mainMenu();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeMainHelp() {
        System.out.println("Commands:");
        System.out.println("Create Game - create a new chess game");
        System.out.println("Help - shows this menu");
        System.out.println("List Games - list all existing chess games");
        System.out.println("Logout - end the current session");
        System.out.println("Observe Game - join a game as an observer");
        System.out.println("Play Game - join a game as the white or black player");
    }

    private void executeCreateGame() {
        System.out.print("Game Name: ");
        String gameName = scanner.nextLine().trim();
        try {
            serverFacade.createGame(gameName);
            System.out.println("Successfully created game: " + gameName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeListGames() {
        try {
            games = serverFacade.listGames();
            int i = 1;
            System.out.println("GAMES:");
            System.out.println("------------------------");
            for (GameData game : games) {
                System.out.println(i + ") " + game.gameName());
                System.out.println("   - White: " + (game.whiteUsername() != null ? game.whiteUsername() : "Open"));
                System.out.println("   - Black: " + (game.blackUsername() != null ? game.blackUsername() : "Open"));
                System.out.println("");
                i += 1;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executePlayGame() {
        if (noGames()) {
            return;
        }
        int gameInt = getGameInt();
        if (gameInt == -1) {
            return;
        }
        System.out.print("Play as (white/black): ");
        String color = scanner.nextLine().trim().toLowerCase();
        ChessGame.TeamColor team;
        if (color.equals("white")) {
            team = ChessGame.TeamColor.WHITE;
        } else if (color.equals("black")) {
            team = ChessGame.TeamColor.BLACK;
        } else {
            System.out.println("Invalid team color");
            return;
        }
        GameData game = games.get(gameInt - 1);
        int gameID = game.gameID();
        if (team == ChessGame.TeamColor.WHITE && Objects.equals(game.whiteUsername(), username)) {
            gameMenu(gameID, true);
            return;
        } else if (team == ChessGame.TeamColor.BLACK && Objects.equals(game.blackUsername(), username)) {
            gameMenu(gameID, false);
            return;
        }
        try {
            serverFacade.joinGame(team, gameID);
            gameMenu(gameID, (team == ChessGame.TeamColor.WHITE ? true : false));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeObserveGame() {
        if (noGames()) {
            return;
        }
        int gameInt = getGameInt();
        if (gameInt == -1) {
            return;
        }
        int gameID = games.get(gameInt - 1).gameID();
        observeMenu(gameID);
    }

    private boolean noGames() {
        try {
            if (games == null || games.size() != serverFacade.listGames().size()) {
                executeListGames();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (games.isEmpty()) {
            System.out.println("There are no games to join");
            return true;
        }
        return false;
    }

    private int getGameInt() {
        int gameInt;
        int numGames = games.size();
        System.out.print("Enter game number: ");
        String gameNumber = scanner.nextLine().trim();
        try {
            gameInt = Integer.parseInt(gameNumber);
            if (gameInt > numGames || gameInt < 1) {
                throw new Exception("Invalid game number");
            }
        } catch (Exception e) {
            System.out.println("Invalid game number");
            return -1;
        }
        return gameInt;
    }

    private void executeGameHelp() {
        System.out.println("Commands:");
        System.out.println("Help - shows this menu");
        System.out.println("Highlight Legal Moves - render the chess board to show possible moves for a piece");
        System.out.println("Leave - exit the current game");
        System.out.println("Make Move - perform a move");
        System.out.println("Redraw Chess Board - rerender the chess board in its current state");
        System.out.println("Resign - resign from the current game");
    }

    private void executeObserverHelp() {
        System.out.println("Commands:");
        System.out.println("Help - shows this menu");
        System.out.println("Highlight Legal Moves - render the chess board to show possible moves for a piece");
        System.out.println("Leave - exit the current game");
        System.out.println("Redraw Chess Board - rerender the chess board in its current state");
    }

    public void renderChessBoard(ChessGame game) {
        chessDisplay.drawBoard(game, renderWhite, null, new HashSet<>());
    }

    private boolean executeGameLeave(int gameID, String authToken, String username, boolean isWhite) {
        try {
            webSocketFacade.leave(gameID, authToken, username, (isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK));
            return false;
        } catch (Exception e) {
            System.out.println("An error occurred when trying to leave the game");
            e.printStackTrace();
            return true;
        }
    }

    private boolean executeObserverLeave(int gameID, String authToken, String username) {
        try {
            webSocketFacade.leave(gameID, authToken, username, null);
            return false;
        } catch (Exception e) {
            System.out.println("An error occurred when trying to leave the game");
            e.printStackTrace();
            return true;
        }
    }

    private void executeResign(int gameID, String authToken, String username) {
        System.out.print("Are you sure you want to resign? ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            try {
                webSocketFacade.resign(gameID, authToken, username);
            } catch (Exception e) {
                System.out.println("An error occurred when trying to resign");
                e.printStackTrace();
            }
        }
    }

    private void executeHighlightMoves(ChessGame game, boolean isWhite) {
        System.out.print("Display moves for which cell? ");
        String provided = scanner.nextLine().trim().toLowerCase();
        if (validCell(provided)) {
            chessDisplay.renderHighlights(game, isWhite, Integer.parseInt(provided.substring(1)) - 1, provided.charAt(0) - 'a');
        } else {
            System.out.println(provided + " is not a valid cell");
        }
    }

    private void executeMakeMove(int gameID, String authToken, String username, boolean isWhite) {
        System.out.print("Move from where? ");
        String start = scanner.nextLine().trim().toLowerCase();
        if (!validCell(start)) {
            System.out.println(start + " is not a valid cell");
            return;
        }
        System.out.print("Move to where? ");
        String end = scanner.nextLine().trim().toLowerCase();
        if (!validCell(end)) {
            System.out.println(end + " is not a valid cell");
            return;
        }
        int startRow = Integer.parseInt(start.substring(1));
        int startCol = start.charAt(0) - 'a' + 1;
        int endRow = Integer.parseInt(end.substring(1));
        int endCol = end.charAt(0) - 'a' + 1;
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        ChessPiece movingPiece = currState.getBoard().getPiece(startPosition);
        ChessPiece.PieceType promotion = null;
        if (movingPiece != null && movingPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if ((isWhite && endPosition.getRow() == 8) || (!isWhite && endPosition.getRow() == 1)) {
                System.out.print("Promote pawn to? ");
                String promoteTo = scanner.nextLine().trim().toLowerCase();
                if (Set.of("queen", "rook", "knight", "bishop").contains(promoteTo)) {
                    switch (promoteTo) {
                        case "queen":
                            promotion = ChessPiece.PieceType.QUEEN;
                            break;
                        case "rook":
                            promotion = ChessPiece.PieceType.ROOK;
                            break;
                        case "knight":
                            promotion = ChessPiece.PieceType.KNIGHT;
                            break;
                        case "bishop":
                            promotion = ChessPiece.PieceType.BISHOP;
                            break;
                    }
                } else {
                    System.out.println(promoteTo + " is not a valid promotion");
                    return;
                }
            }
        }
        ChessMove moveRequest = new ChessMove(startPosition, endPosition, promotion);
        try {
            webSocketFacade.makeMove(gameID, authToken, username, moveRequest, isWhite);
        } catch (Exception e) {
            System.out.println("An error occurred when trying to perform the move");
            e.printStackTrace();
        }
    }

    private boolean validCell(String provided) {
        boolean valid = provided.length() == 2;
        if ("abcdefgh".indexOf(provided.charAt(0)) == -1) {
            valid = false;
        }
        if ("12345678".indexOf(provided.charAt(1)) == -1) {
            valid = false;
        }
        return valid;
    }
}
