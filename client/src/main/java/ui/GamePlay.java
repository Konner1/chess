package ui;

import chess.*;
import exception.ResponseException;
import websocket.DisplayHandler;
import websocket.WebSocketFacade;
import websocket.commands.*;

import java.util.ArrayList;
import java.util.List;
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
        this.color = playerColor;
        this.ws = new WebSocketFacade(serverUrl, this);

        ws.sendCommand(new ConnectCommand(authToken, gameID));
    }

    public void run() {
        out.println("\u2655 You are now playing a game! Type 'help' for options.");

        while (true) {
            out.print("\n[CHESS GAME] >>> ");
            String[] input = scanner.nextLine().trim().split(" ");
            if (input.length == 0 || input[0].isBlank()) { continue; }

            String cmd = input[0].toLowerCase();

            try {
                switch (cmd) {
                    case "h", "help"    -> printHelp();
                    case "hl", "highlight" -> doHighlight();
                    case "m", "move"    -> doMove();
                    case "r", "redraw"  -> doRedraw();
                    case "res", "resign" -> {
                        ws.sendCommand(new ResignCommand(authToken, gameID));
                        return;
                    }
                    case "le", "leave"  -> {
                        ws.sendCommand(new LeaveCommand(authToken, gameID));
                        return;
                    }
                    default -> out.println("Unknown command. Try 'help'.");
                }
            } catch (ResponseException e) {
                out.printf("Error: %s%n", e.getMessage());
            } catch (Exception e) {
                out.println("Invalid input. Try again.");
            }
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
        System.out.print("Enter a position (e.g. 'e2'): ");
        String posInput = scanner.next().toLowerCase();
        scanner.nextLine();

        if (!posInput.matches("[a-h][1-8]")) {
            System.out.println("Invalid format. Use positions like 'e2'.");
            return;
        }

        ChessPosition position = new ChessPosition(posInput.charAt(1) - '0', posInput.charAt(0) - ('a' - 1));
        List<ChessMove> legalMoves = new ArrayList<>(game.validMoves(position));

        DrawBoard.print(System.out, game, color == ChessGame.TeamColor.WHITE, legalMoves);
    }

    private void doMove() {
        try {
            System.out.print("From (e.g. 'e2'): ");
            String fromInput = scanner.next().toLowerCase();
            System.out.print("To (e.g. 'e4'): ");
            String toInput = scanner.next().toLowerCase();
            System.out.print("Promotion piece (optional): ");
            String promoInput = scanner.nextLine().toLowerCase().trim();

            ChessPosition from = new ChessPosition(fromInput.charAt(1) - '0', fromInput.charAt(0) - ('a' - 1));
            ChessPosition to = new ChessPosition(toInput.charAt(1) - '0', toInput.charAt(0) - ('a' - 1));

            ChessPiece.PieceType promotion = switch (promoInput) {
                case "queen" -> ChessPiece.PieceType.QUEEN;
                case "rook" -> ChessPiece.PieceType.ROOK;
                case "bishop" -> ChessPiece.PieceType.BISHOP;
                case "knight" -> ChessPiece.PieceType.KNIGHT;
                default -> null;
            };

            ws.sendCommand(new MakeMoveCommand(authToken, gameID, new ChessMove(from, to, promotion)));

        } catch (Exception e) {
            out.println("Invalid input. Format: 'move e2 e4 [promotion_piece]'");
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

    @Override
    public void notify(String message) {
        System.out.println("\n[NOTIFICATION] " + message);
    }

    @Override
    public void error(String errorMessage) {
        System.out.println("\n[ERROR] " + errorMessage);
    }
}
