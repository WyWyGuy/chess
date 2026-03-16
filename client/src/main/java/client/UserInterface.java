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

    }

    private void executeLogin() {

    }

    private void executeRegister() {
        
    }
}
