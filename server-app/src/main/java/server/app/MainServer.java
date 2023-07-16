package server.app;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class MainServer {

    static Logger logger = Logger.getLogger(MainServer.class.getName());

    public static void main(String[] args) {
        logger.info("Enter the port number to listen on: ");
        Scanner serverPort = new Scanner(System.in);  // Create a Scanner object
        try {
            int portNumber = serverPort.nextInt();
            Server server = new Server(portNumber);
            server.startAccepting();
        } catch (NumberFormatException e) {
            logger.info("Invalid port number");
            System.exit(1);
        }
    }

}
