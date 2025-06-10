package ui;

import facade.ServerFacade;
import exception.ResponseException;
import model.GameData;
import chess.*;

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
        out.println("♕ Welcome to the game!  Type 'help' for options.");

        while (true) {
            out.print("\n[SIGNED IN] >>> ");
            String[] input = scanner.nextLine().trim().split(" ");
            if (input.length == 0 || input[0].isBlank()) { continue; }

            String cmd = input[0].toLowerCase();

            try {
                switch (cmd) {
                    case "h", "help"    -> printHelp();
                    case "l", "logout"  -> { server.logout(authToken); out.println("Logged out.");     return State.SIGNEDOUT; }
                    case "q", "quit"    -> { out.println("Goodbye!");                                  return null; }

                    case "c", "create"  -> doCreate(input);
                    case "li", "list"    -> doList();
                    case "j", "join"    -> doJoin(input);
                    case "o", "observe" -> doObserve(input);

                    default        -> out.println("Unknown command. Try 'help'.");
                }
            } catch (ResponseException e) {
                out.printf("Error: %s%n", e.getMessage());
            } catch (NumberFormatException e) {
                out.println("Game number must be a valid integer.");
            }
        }
    }

    private void doCreate(String[] input) throws ResponseException {
        if (input.length != 2) { out.println("Usage: create <gameName>"); return; }
        server.createGame(input[1], authToken);
        out.println("Game created.");
    }

    private void doList() throws ResponseException {
        GameData[] games = server.listGames(authToken);
        if (games.length == 0) { out.println("No games available."); return; }

        lastListedGames = Arrays.asList(games);
        for (int i = 0; i < games.length; i++) {
            GameData g = games[i];
            out.printf("%d. %s (White: %s, Black: %s)%n",
                    i + 1,
                    g.gameName(),
                    g.whiteUsername() != null ? g.whiteUsername() : "none",
                    g.blackUsername()  != null ? g.blackUsername()  : "none");
        }
    }

    private void doJoin(String[] input) throws ResponseException {
        if (input.length != 3) {
            out.println("Usage: join <game#> <WHITE|BLACK>");
            return;
        }

        GameData game = getGameByIndex(input[1]);
        if (game == null) {
            return;
        }

        String color = input[2].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            out.println("Color must be WHITE or BLACK.");
            return;
        }

        server.joinGame(game.gameID(), color, authToken);
        out.printf("Joined game %d as %s.%n", game.gameID(), color);

        ChessGame.TeamColor teamColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        GamePlay gamePlay = new GamePlay(server.getServerUrl(), authToken, game.gameID(), teamColor);
        gamePlay.run();

    }


    private void doObserve(String[] input) throws ResponseException {
        if (input.length != 2) {
            out.println("Usage: observe <game#>");
            return;
        }

        GameData game = getGameByIndex(input[1]);
        if (game == null) {
            return;
        }

        int gameID = game.gameID();

        server.observeGame(gameID, authToken);
        out.printf("Observing game %d.%n", gameID);

        GamePlay gamePlay = new GamePlay(server.getServerUrl(), authToken, game.gameID(), null); // null means observer
        gamePlay.run();
    }

    private GameData getGameByIndex(String inputIndex) throws ResponseException {
        int idx = Integer.parseInt(inputIndex) - 1;
        if (idx < 0) {
            out.println("Invalid game number.");
            return null;
        }

        GameData[] games = server.listGames(authToken);
        if (idx >= games.length) {
            out.println("Invalid game number.");
            return null;
        }
        lastListedGames = Arrays.asList(games);

        return games[idx];
    }



    private void printHelp() {
        out.println("Options:");
        out.println("  'c', 'create' <NAME> - Create a new game");
        out.println("  'li', 'list' - List all games");
        out.println("  'j', 'join' <#> <WHITE|BLACK> - Join a game as a player");
        out.println("  'o' 'observe' <#> - Observe a game");
        out.println("  'l', 'logout' - Log out");
    }
}
