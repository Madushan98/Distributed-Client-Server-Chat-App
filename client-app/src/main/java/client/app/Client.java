package client.app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Logger;

public class Client {

    Logger logger = Logger.getLogger(Client.class.getName());

    private Socket socket;
    private final String hostname;
    private final int portNumber;
    private final String clientName;

    private ObjectOutputStream writeToServer;

    private ObjectInputStream readFromServer;

    public Client(String clientName, String hostname, int portNumber) {
        this.clientName = clientName;
        this.portNumber = portNumber;
        this.hostname = hostname;
    }

public void establishConnection() {
        // Open the connection to the server
        try {
            this.socket = new Socket(hostname, portNumber);
            this.writeToServer = new ObjectOutputStream(this.socket.getOutputStream());
            this.readFromServer = new ObjectInputStream(this.socket.getInputStream());

            // Start the reader thread
            ClientConnection clientConnection = new ClientConnection(this.readFromServer);
            clientConnection.start();

        } catch( UnknownHostException uhe ) {
            String message = String.format("Could not connect to %s:%d %n", hostname, portNumber);
            logger.warning(message);
            uhe.printStackTrace();
        } catch( IOException ioe ) {
            String message = String.format("Error opening streams to %s:%d %n", hostname, portNumber);
            logger.warning(message);
            ioe.printStackTrace();
        }
    }

    public void startAccepting() {
        establishConnection();
        Scanner userInput = new Scanner(System.in);
        while(true) {
            String message = userInput.nextLine();
            try {
                writeToServer.writeObject(message);
            } catch( IOException ioe ) {
                System.err.println("There was an error while writing to the server!");
                ioe.printStackTrace();
                System.exit(1);
            }
        }
    }

    public String receiveMessage() {
        try {
            return (String) readFromServer.readObject();
        } catch( IOException ioe ) {
            logger.warning("There was an error while reading from the server!");
            ioe.printStackTrace();
            System.exit(1);
        } catch( ClassNotFoundException cnfe ) {
            String message = String.format("Invalid message read from %s:%d %n", hostname, portNumber);
            logger.warning(message);
            cnfe.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        try {
            this.socket.close();
            logger.info("Connection closed");
        } catch( IOException ioe ) {
            System.err.println("There was an error while closing the connection!");
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    public void sendMessage(String input) {
        try {
            writeToServer.writeObject(input);
        } catch( IOException ioe ) {
            System.err.println("There was an error while writing to the server!");
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    private class ClientConnection extends  Thread {
        private ObjectInputStream serverConnection;

        public ClientConnection(ObjectInputStream serverConnection) {
            this.serverConnection = serverConnection;
        }

        public void run() {
            while(true) {
                try {
                    String message = (String) serverConnection.readObject();
                    System.out.println(message);
                } catch( IOException ioe ) {
                    System.err.println("There was an error while reading from the server!");
                    ioe.printStackTrace();
                    System.exit(1);
                } catch( ClassNotFoundException cnfe ) {
                    System.err.printf("Invalid message read from %s:%d\n", hostname, portNumber);
                    cnfe.printStackTrace();
                }
            }
        }

    };


}
