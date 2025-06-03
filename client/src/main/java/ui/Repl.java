package ui;



import java.util.Scanner;
import facade.ServerFacade;
import exception.*;
import chess.*;
import model.GameData;
import model.AuthData;





public class Repl {
    private State state = State.SIGNEDOUT;
    private String authToken;
    private final ServerFacade server;

    public Repl(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) throws ResponseException {
        String[] tokens = input.strip().split(" ");
        String cmd = tokens[0];

        switch (state) {
            case SIGNEDOUT -> {
                return switch (cmd) {
                    case "help" -> helpSignedOut();
                    case "login" -> {
                        authToken = server.login(tokens[1], tokens[2]).authToken();
                        state = State.SIGNEDIN;
                        yield "Logged in!";
                    }
                    case "register" -> {
                        authToken = server.register(tokens[1], tokens[2], tokens[3]).authToken();
                        state = State.SIGNEDIN;
                        yield "Registered and logged in!";
                    }
                    case "quit" -> "quit";
                    default -> "Unknown command (signed out). Try 'help'.";
                };
            }

            case SIGNEDIN -> {
                return switch (cmd) {
                    case "help" -> helpSignedIn();
                    case "logout" -> {
                        server.logout(authToken);
                        authToken = null;
                        state = State.SIGNEDOUT;
                        yield "Logged out.";
                    }
                    case "create" -> {
                        server.createGame(tokens[1], authToken);
                        yield "Game created.";
                    }
                    case "list" -> {
                        var games = server.listGames(authToken);
                        yield formatGames(games);
                    }

                    default -> "Unknown command (signed in). Try 'help'.";
                };
            }
        }
        return "Invalid state.";
    }

    private String helpSignedOut() {
        return "Available commands: register <user> <pass> <email>, login <user> <pass>, help, quit";
    }

    private String helpSignedIn() {
        return "Available commands: logout, create <game>, list, help";
    }

    private String formatGames(GameData[] games) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < games.length; i++) {
            var g = games[i];
            sb.append(String.format("%d. %s (White: %s, Black: %s)%n",
                    i + 1, g.getGameName(), g.getWhiteUsername(), g.getBlackUsername()));
        }
        return sb.toString();
    }
}
