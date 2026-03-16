package client;

import java.util.Scanner;

public class UserInterface {

    private static ServerFacade serverFacade;
    private static Scanner scanner = new Scanner(System.in);

    public UserInterface(String hostname, int port) {
        serverFacade = new ServerFacade(hostname, port);
    }

    public void startMenu() {
        boolean running = true;
        while (running) {
            System.out.println("Enter a command (type help for a list of commands): ");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "quit" -> {
                    running = executeQuit();
                }
                case "help" -> executeHelp();
                case "login" -> executeLogin();
                case "register" -> executeRegister();
            }
        }
    }

    private boolean executeQuit() {
        System.out.println("Quitting chess client...");
        return false;
    }

    private void executeHelp() {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeRegister() {

    }
}
