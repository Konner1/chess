package ui;

import facade.ServerFacade;
import exception.ResponseException;
import model.GameData;

import java.util.*;

import static java.lang.System.out;

public class Postlogin {
    private final ServerFacade server;
    private final String authToken;
    private final Scanner scanner = new Scanner(System.in);
    private List<GameData> lastListedGames = new ArrayList<>();

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

                    case "list" -> {
                        GameData[] games = server.listGames(authToken);
                        lastListedGames = Arrays.asList(games);
                        for (int i = 0; i < games.length; i++) {
                            var g = games[i];
                            out.printf("%d. %s (White: %s, Black: %s)%n",
                                    i + 1,
                                    g.getGameName(),
                                    g.getWhiteUsername() != null ? g.getWhiteUsername() : "none",
                                    g.getBlackUsername() != null ? g.getBlackUsername() : "none"
                            );
                        }
                    }

                    case "join" -> {
                        if (input.length != 3) {
                            out.println("Usage: join <gameNumber> <WHITE|BLACK>");
                            break;
                        }
                        int index = Integer.parseInt(input[1]) - 1;
                        if (index < 0 || index >= lastListedGames.size()) {
                            out.println("Invalid game number.");
                            break;
                        }
                        String color = input[2].toUpperCase();
                        if (!color.equals("WHITE") && !color.equals("BLACK")) {
                            out.println("Color must be WHITE or BLACK.");
                            break;
                        }
                        int gameID = lastListedGames.get(index).getGameID();
                        server.joinGame(gameID, color, authToken);
                        out.printf("Joined game %d as %s.%n", gameID, color);
                    }

                    case "observe" -> {
                        if (input.length != 2) {
                            out.println("Usage: observe <gameNumber>");
                            break;
                        }
                        int index = Integer.parseInt(input[1]) - 1;
                        if (index < 0 || index >= lastListedGames.size()) {
                            out.println("Invalid game number.");
                            break;
                        }
                        int gameID = lastListedGames.get(index).getGameID();
                        server.joinGame(gameID, null, authToken);  // join as observer (no color)
                        out.printf("Observing game %d.%n", gameID);
                    }

                    case "quit" -> {
                        out.println("Goodbye!");
                        return null;
                    }

                    default -> out.println("Unknown command. Try 'help'.");
                }
            } catch (ResponseException e) {
                out.printf("Error: %s%n", e.getMessage());
            } catch (NumberFormatException e) {
                out.println("Game number must be a valid number.");
            }
        }
    }

    private void printHelp() {
        out.println("Commands:");
        out.println("  create <Name>                 - Create a new game");
        out.println("  list                          - List all games");
        out.println("  join <Game#> <WHITE|BLACK>    - Join a game as a player");
        out.println("  observe <Game#>               - Observe a game");
        out.println("  logout                        - Log out");
        out.println("  quit                          - Quit the client");
        out.println("  help                          - Show this menu");
    }
}

