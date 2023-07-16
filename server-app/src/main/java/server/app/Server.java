package server.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Server {

    static Logger logger = Logger.getLogger(Server.class.getName());

    // The server socket
    private ServerSocket serverSocket;

    // The port number to listen on
    private int portNumber;

    // The ID for the server
    public static final int SERVER_ID = -1;

    // Connected user count
    private static int userIdCounter = 0;

    public Server(int portNumber) {
        this.portNumber = portNumber;
        try {
            this.serverSocket = new ServerSocket(portNumber);
        } catch( IOException ioe ) {
            logger.warning("Error while attempting to open server on port \n");
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Start accepting connections from clients
     */
    public void startAccepting() {
        String hostname = this.serverSocket.getInetAddress().getHostName();
        String message = String.format("Main Server -> The server is now listening on %s %d %n", hostname, this.portNumber);
        logger.info(message);

        while ( true ) {
            try {
                Socket newClient = serverSocket.accept();
                int newUserId = userIdCounter++;
                // Create a handler for that client
                ConnectionHandler client = new ConnectionHandler(newClient,newUserId);
                client.start();


            } catch( IOException ioe ) {
                String errorMessage = String.format("Error attempting to accept client on port %d %n", portNumber);
                logger.warning(errorMessage);
                ioe.printStackTrace();
            }finally {
                logger.info("Closing the server socket");
            }
        }
    }



}
