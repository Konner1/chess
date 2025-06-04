package ui;

import facade.ServerFacade;
import exception.ResponseException;
import model.GameData;

import java.util.*;

import static java.lang.System.out;

public class Postlogin {

    /* ── instance data ───────────────────────────────────────────── */
    private final ServerFacade server;
    private final String authToken;
    private final Scanner scanner = new Scanner(System.in);
    private List<GameData> lastListedGames = new ArrayList<>();

    /* ── ctor ────────────────────────────────────────────────────── */
    public Postlogin(ServerFacade server, String authToken) {
        this.server = server;
        this.authToken = authToken;
    }

    /* ── public entry ─────────────────────────────────────────────── */
    public State run() {
        out.println("♕ Welcome to the game!  Type 'help' for options.");

        while (true) {
            out.print("\n[SIGNED IN] >>> ");
            String[] input = scanner.nextLine().trim().split(" ");
            if (input.length == 0 || input[0].isBlank()) { continue; }

            String cmd = input[0].toLowerCase();

            try {
                switch (cmd) {
                    case "help"    -> printHelp();
                    case "logout"  -> { server.logout(authToken); out.println("Logged out.");     return State.SIGNEDOUT; }
                    case "quit"    -> { out.println("Goodbye!");                                  return null; }

                    case "create"  -> doCreate(input);
                    case "list"    -> doList();
                    case "join"    -> doJoin(input);
                    case "observe" -> doObserve(input);

                    default        -> out.println("Unknown command. Try 'help'.");
                }
            } catch (ResponseException e) {
                out.printf("Error: %s%n", e.getMessage());
            } catch (NumberFormatException e) {
                out.println("Game number must be a valid integer.");
            }
        }
    }

    /* ── command helpers ──────────────────────────────────────────── */
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
        if (input.length != 3) { out.println("Usage: join <game#> <WHITE|BLACK>"); return; }
        int idx = Integer.parseInt(input[1]) - 1;
        if (idx < 0 || idx >= lastListedGames.size()) { out.println("Invalid game number."); return; }

        String color = input[2].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            out.println("Color must be WHITE or BLACK.");
            return;
        }

        int gameID = lastListedGames.get(idx).gameID();
        server.joinGame(gameID, color, authToken);
        out.printf("Joined game %d as %s.%n", gameID, color);
        if(color.equals("WHITE")){
            DrawBoard.print(System.out, true);
        } else{
            DrawBoard.print(System.out, false);
        }

    }

    private void doObserve(String[] input) throws ResponseException {
        if (input.length != 2) { out.println("Usage: observe <game#>"); return; }
        int idx = Integer.parseInt(input[1]) - 1;
        if (idx < 0 || idx >= lastListedGames.size()) { out.println("Invalid game number."); return; }

        int gameID = lastListedGames.get(idx).gameID();

        /* call the real observe endpoint */
        server.observeGame(gameID, authToken);

        out.printf("Observing game %d.%n", gameID);
        DrawBoard.print(System.out, true);
    }


    /* ── help text ───────────────────────────────────────────────── */
    private void printHelp() {
        out.println("Commands:");
        out.println("  create <name>              - Create a new game");
        out.println("  list                       - List all games");
        out.println("  join <#> <WHITE|BLACK>     - Join a game as a player");
        out.println("  observe <#>                - Observe a game");
        out.println("  logout                     - Log out");
        out.println("  quit                       - Quit program");
        out.println("  help                       - Show this menu");
    }
}
