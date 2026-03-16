package client;

import chess.*;

public class ClientMain {

    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client running");
        UserInterface userInterface = new UserInterface(args[0], Integer.parseInt(args[1]));
        userInterface.startMenu();
    }
}
