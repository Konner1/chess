package ui;

import chess.*;
import exception.ResponseException;
import websocket.DisplayHandler;
import websocket.WebSocketFacade;
import websocket.commands.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static java.lang.System.out;

public class GamePlay implements DisplayHandler {
    private final WebSocketFacade ws;
    private final String authToken;
    private final int gameID;
    private ChessGame game;
    private final Scanner scanner = new Scanner(System.in);
    private final ChessGame.TeamColor color;

    public GamePlay(String serverUrl, String authToken, int gameID, ChessGame.TeamColor playerColor) throws ResponseException {
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = (playerColor != null) ? playerColor : ChessGame.TeamColor.WHITE;
        this.ws = new WebSocketFacade(serverUrl, this);

        ws.sendCommand(new ConnectCommand(authToken, gameID));
    }

    public void run() {
        out.println("\n You are now playing a game! Type 'help' for options.");

        while (true) {
            out.print("\n[CHESS GAME] >>> ");
            String[] input = scanner.nextLine().trim().split(" ");
            if (input.length == 0 || input[0].isBlank()) {
                continue;
            }

            String cmd = input[0].toLowerCase();
            try {
                switch (cmd) {
                    case "h", "help"       -> printHelp();
                    case "hl", "highlight" -> doHighlight();
                    case "m", "move"       -> doMove();
                    case "r", "redraw"     -> doRedraw();
                    case "res", "resign"   -> doResign();
                    case "le", "leave"     -> {
                        ws.sendCommand(new LeaveCommand(authToken, gameID));
                        return;
                    }
                    default                -> out.println("Unknown command. Try 'help'.");
                }
            } catch (ResponseException e) {
                out.printf("Error: %s%n", e.getMessage());
            } catch (Exception e) {
                out.println("Invalid input. Try again.");
            }
        }
    }

    private void doResign() throws ResponseException {
        out.print("Are you sure you want to resign? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            ws.sendCommand(new ResignCommand(authToken, gameID));
        } else {
            out.println("Resign cancelled.");
        }
    }

    private void printHelp() {
        out.println("Options:");
        out.println("  'hl', 'highlight' - Highlight legal moves for a piece");
        out.println("  'm', 'move' - Make a move (e.g., 'move e2 e4 [promotion_piece]')");
        out.println("  'r', 'redraw' - Redraw the board");
        out.println("  'res', 'resign' - Resign from the game");
        out.println("  'le', 'leave' - Leave the game");
        out.println("  'h', 'help' - Show this help message");
    }

    private void doHighlight() {
        if (game == null) {
            out.println("Board not yet loaded.");
            return;
        }

        out.print("Enter a position (e.g. 'e2'): ");
        String posInput = scanner.next().trim().toLowerCase();
        scanner.nextLine();

        if (!posInput.matches("[a-h][1-8]")) {
            out.println("Invalid format. Use positions like 'e2'.");
            return;
        }

        ChessPosition position = new ChessPosition(
                posInput.charAt(1) - '0',
                posInput.charAt(0) - ('a' - 1)
        );

        Collection<ChessMove> moves = game.validMoves(position);
        if (moves == null || moves.isEmpty()) {
            out.println("No legal moves from " + posInput);
            return;
        }

        boolean whiteBottom = (color == ChessGame.TeamColor.WHITE) || (color == null);

        DrawBoard.print(
                out,
                game,
                whiteBottom,
                new ArrayList<>(moves)
        );
    }

    private void doMove() {
        out.print("Enter move (e.g. 'e2 e4 [promotion_piece]'): ");
        String line = scanner.nextLine().trim().toLowerCase();
        String[] parts = line.split("\\s+");
        if (parts.length < 2 || parts.length > 3) {
            out.println("Invalid format. Format: 'e2 e4 [promotion_piece]'");
            return;
        }

        String fromInput = parts[0];
        String toInput   = parts[1];

        if (!fromInput.matches("[a-h][1-8]") || !toInput.matches("[a-h][1-8]")) {
            out.println("Invalid squares. Use positions like 'e2' and 'e4'.");
            return;
        }

        try {
            ChessPosition from = new ChessPosition(
                    fromInput.charAt(1) - '0',
                    fromInput.charAt(0) - ('a' - 1)
            );
            ChessPosition to = new ChessPosition(
                    toInput.charAt(1) - '0',
                    toInput.charAt(0) - ('a' - 1)
            );

            String promoToken = parts.length == 3 ? parts[2] : "";
            ChessPiece.PieceType promotion = switch (promoToken) {
                case "queen"  -> ChessPiece.PieceType.QUEEN;
                case "rook"   -> ChessPiece.PieceType.ROOK;
                case "bishop" -> ChessPiece.PieceType.BISHOP;
                case "knight" -> ChessPiece.PieceType.KNIGHT;
                default       -> null;
            };

            ws.sendCommand(new MakeMoveCommand(authToken, gameID,
                    new ChessMove(from, to, promotion)));
        } catch (Exception e) {
            out.println("Invalid input. Format: 'e2 e4 [promotion_piece]'");
        }
    }

    private void doRedraw() {
        DrawBoard.print(System.out, game, color == ChessGame.TeamColor.WHITE);
    }

    @Override
    public void updateBoard(ChessGame newGame) {
        this.game = newGame;
        doRedraw();
    }

    private static final String RED   = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    @Override
    public void notify(String message) {
        System.out.println();
        System.out.println(RED
                + "[NOTIFICATION] " + message
                + RESET);
    }
    @Override
    public void error(String errorMessage) {
        System.out.println("\n[ERROR] " + errorMessage);
    }
}
