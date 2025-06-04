package ui;

import facade.ServerFacade;
import exception.ResponseException;
import model.GameData;

import java.util.Scanner;

import static java.lang.System.out;

public class Postlogin {
    private final ServerFacade server;
    private final String authToken;
    private final Scanner scanner = new Scanner(System.in);

    public Postlogin(ServerFacade server, String authToken) {
        this.server = server;
        this.authToken = authToken;
    }

    public State run() {
        out.println("♕ Welcome to the game! Type 'help' for options.");

        while (true) {
            out.print("\n[SIGNED IN] >>> ");
            String[] input = scanner.nextLine().trim().split(" ");
            if (input.length == 0 || input[0].isBlank()) continue;

            String command = input[0].toLowerCase();

            try {
                switch (command) {
                    case "help" -> printHelp();
                    case "logout" -> {
                        server.logout(authToken);
                        out.println("Logged out.");
                        return State.SIGNEDOUT;
                    }
                    case "create" -> {
                        if (input.length != 2) {
                            out.println("Usage: create <gameName>");
                            break;
                        }
                        server.createGame(input[1], authToken);
                        out.println("Game created.");
                    }
//                    case "list" -> {
//                        GameData[] games = server.listGames(authToken);
//                        for (int i = 0; i < games.length; i++) {
//                            GameData g = games[i];
//                            out.printf("%d. %s (White: %s, Black: %s)%n",
//                                    i + 1, g.getGameName(), g.getWhiteUsername(), g.getBlackUsername());
//                        }
//                    }
                    case "join" -> {
                        if (input.length != 3) {
                            out.println("Usage: join <gameName>");
                            break;
                        }
                        server.joinGame(input[1], authToken);
                        out.println("Game created.");
                    }

                    case "observe" -> {
                        if (input.length != 2) {
                            out.println("Usage: create <gameName>");
                            break;
                        }
                        server.createGame(input[1], authToken);
                        out.println("Game created.");
                    }

                    case "loggout" -> {
                        if (input.length != 1) {
                            out.println("invalid logout");
                            break;
                        }
                        server.logout(input[1]);
                        out.println("User logged out");
                    }
                    case "quit" -> {
                        out.println("Goodbye!");
                        return null;
                    }
                    default -> out.println("Unknown command. Try 'help'.");
                }
            } catch (ResponseException e) {
                out.printf("Error: %s%n", e.getMessage());
            }
        }
    }

    private void printHelp() {
        out.println("Commands:");
        out.println("  create <Name> - Create a new game");
        out.println("  list - List all games");
        out.println("  join <ID> [WHITE][BLACK] - a game");
        out.println("  observe <ID> - a game");
        out.println("  logout - when you are done");
        out.println("  quit - playing chess");
        out.println("  help - Show this menu");
    }
}

