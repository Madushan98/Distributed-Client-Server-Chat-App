package server.app;

import eventhandler.data.ChatEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Server {

    static Logger logger = Logger.getLogger(Server.class.getName());
    private ServerSocket serverSocket;
    private final int portNumber;
    private static int userIdCounter = 0;
    private final Map<Integer, ConnectionHandler> clientConnections;
    private List<String> userNames;

    public Server(int portNumber) {
        this.portNumber = portNumber;
        this.clientConnections = new HashMap<>();
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
                clientConnections.put(newUserId, client);
                client.start();

            } catch( IOException ioe ) {
                String errorMessage = String.format("Error attempting to accept client on port %d %n", portNumber);
                logger.warning(errorMessage);
                ioe.printStackTrace();
            }finally {

            }
        }
    }

    /**
     * Send a message to all connected clients
     * @param message
     */
    public void sendMessageToConnections(String message) {
        for( ConnectionHandler client : clientConnections.values() ) {
            client.sendMessage(message);
        }
    }




    private class ConnectionHandler extends Thread {

        Logger logger = Logger.getLogger(ConnectionHandler.class.getName());
        public final int userId;
        public String connectionName;
        public String clientName;
        private Socket connectionSocket;
        private ObjectInputStream readFromConnection;
        private ObjectOutputStream writeToConnection;

        public ConnectionHandler(Socket connectionSocket, int userId) {
            this.userId = userId;
            this.connectionSocket = connectionSocket;

            try {
                this.readFromConnection = new ObjectInputStream(connectionSocket.getInputStream());
                this.writeToConnection = new ObjectOutputStream(connectionSocket.getOutputStream());
            } catch( IOException ioe ) {
                logger.warning("Error while opening streams for client!\n");
                ioe.printStackTrace();
            }
        }

        public void run() {
            while( true ) {
                try {
                        String messageRecieved = (String)this.readFromConnection.readObject();
                        sendMessage(messageRecieved);
                } catch( IOException ioe ) {
                    disconnect();
                    break;
                } catch( ClassNotFoundException cnfe ) {
                    logger.warning("Invalid message class recieved over socket!\n");
                    cnfe.printStackTrace();
                }
            }
        }

        protected  <E extends Serializable> void validateUser(ChatEvent<E> connectionInfo) {
            String clientName = (String)connectionInfo.getContents();

            // Create message indicating either success or failure of validation
            ChatEvent<?> loginResponse;
            if ( userNames.contains(clientName) ) {
                String errorString = "Username already exists\nPlease try again";
                sendMessage(errorString);
            } else {
                this.clientName = clientName;
                joinServer();
                String successLoginResponse = "Welcome to the server " + clientName;
                sendMessageToConnections(successLoginResponse);
            }
        }

        private void joinServer() {
            userNames.add(clientName);

        }

        public void sendMessage(String message) {
            try {
                this.writeToConnection.writeObject(message);
            } catch( IOException ioe ) {
                logger.warning("Error while attempting to send message to client\n");
                ioe.printStackTrace();
            }
        }

        public void disconnect() {
            userNames.remove(this.clientName);
            }

    }

}
