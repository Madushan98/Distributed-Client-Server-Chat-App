package server.app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ConnectionHandler extends Thread {

        Logger logger = Logger.getLogger(ConnectionHandler.class.getName());
        public final int userId;
        public String connectionName;

        // Socket and stream
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


    }

