package client;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

    private static ServerFacade serverFacade;
    private static Scanner scanner = new Scanner(System.in);
    private List<GameData> games;

    public UserInterface(String hostname, int port) {
        serverFacade = new ServerFacade(hostname, port);
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
                case "create game" -> createGame();
                case "list games" -> listGames();
                case "play game" -> playGame();
            }
        }
    }

    private void gameMenu() {
        System.out.println("GAME MENU CALLED");
    }

    private boolean executeQuit() {
        System.out.println("Quitting chess client...");
        return false;
    }

    private boolean executeLogout() {
        System.out.println("Logging out...");
        try {
            serverFacade.logout();
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
            serverFacade.login(username, password);
            System.out.println("Successfully logged in as " + username);
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
            serverFacade.register(username, password, email);
            System.out.println("Successfully registered new user: " + username);
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

    private void createGame() {
        System.out.print("Game Name: ");
        String gameName = scanner.nextLine().trim();
        try {
            serverFacade.createGame(gameName);
            System.out.println("Successfully created game: " + gameName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void listGames() {
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

    private void playGame() {
        if (games == null) {
            listGames();
        }
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
        int gameID = games.get(gameInt - 1).gameID();
        try {
            serverFacade.joinGame(team, gameID);
            gameMenu();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
