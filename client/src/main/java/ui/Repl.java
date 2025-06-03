package ui;

import facade.ServerFacade;

public class Repl {

    private final ServerFacade server;
    private State state;

    public Repl(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.state = State.SIGNEDOUT;
    }

    public void run() {
        while (true) {
            if (state == State.SIGNEDOUT) {
                Prelogin prelogin = new Prelogin(server);
                State result = prelogin.run();

                if (result == null) {
                    System.out.println("Goodbye!");
                    return;
                }

                state = result;
            }

            System.out.println("Successfully signed in. (Postlogin not yet implemented)");
            state = State.SIGNEDOUT;
        }
    }
}

