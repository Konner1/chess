package ui;

import model.*;
import facade.ServerFacade;
import exception.ResponseException;

import java.util.Scanner;

import static java.lang.System.out;

public class Prelogin {

    private final Scanner scanner;
    private final ServerFacade server;

    public Prelogin(ServerFacade server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    public LoginResult run() {
        out.println("♕ Welcome to Chess! Type help to get started. ♕");

        while (true) {
            out.print("\n[LOGGED OUT] >>> ");
            String[] input = scanner.nextLine().trim().split(" ");

            if (input.length == 0 || input[0].isBlank()) {
                continue;
            }

            String command = input[0].toLowerCase();

            try {
                if (command.equals("help")|| command.equals("h")) {
                    printHelp();
                    continue;
                }

                if (command.equals("quit")|| command.equals("q")) {
                    return null;
                }

                if (command.equals("register") || command.equals("r")) {
                    if (input.length != 4) {
                        out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
                        continue;
                    }

                    var success = server.register(input[1], input[2], input[3]);
                    if (success != null) {
                        out.println("Registered and logged in!");
                        return new LoginResult(State.SIGNEDIN, success.authToken());
                    }
                    continue;
                }

                if (command.equals("login")|| command.equals("l")) {
                    if (input.length != 3) {
                        out.println("Usage: login <USERNAME> <PASSWORD>");
                        continue;
                    }

                    var success = server.login(input[1], input[2]);
                    if (success != null) {
                        out.println("Logged in!");
                        return new LoginResult(State.SIGNEDIN, success.authToken());
                    }
                    continue;
                }

                out.println("Unknown command.");
                printHelp();

            } catch (ResponseException e) {
                out.printf("Error: %s%n", e.getMessage());
            }
        }
    }

    private void printHelp() {
        out.println("Options:");
        out.println("  'r', 'register' <USERNAME> <PASSWORD> <EMAIL> - Create a new account");
        out.println("  'l', 'login' <USERNAME> <PASSWORD> - Log into your account");
        out.println("  'q', 'quit' - Exit the game");
        out.println("  'h', 'help' - Show available commands");
    }
}
