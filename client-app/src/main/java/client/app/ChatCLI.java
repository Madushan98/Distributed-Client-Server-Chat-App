package client.app;

import java.util.Scanner;
import java.util.logging.Logger;

public class ChatCLI {

    private static final Logger logger = Logger.getLogger(ChatCLI.class.getName());


    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        logger.info("Enter Your Name ");
        String userName = userInput.nextLine();
        logger.info("Enter the port number to listen on: ");
        int portNumber = userInput.nextInt();
        String hostname = "localhost";

        try {
            Client client = new Client(userName, hostname, portNumber);
            client.establishConnection();

            // Start a new thread to continuously read incoming messages from the server
            Thread messageReaderThread = new Thread(() -> {
                String message ;
                while ((message = client.receiveMessage()) != null )
                    logger.info("Received message: " + message);
            });
            messageReaderThread.start();

            // Main thread to handle sending messages
            while (true) {
                logger.info("Enter a message to send (or type 'exit' to quit): ");
                String input = userInput.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    client.disconnect();
                    break;
                }
                client.sendMessage(input);
            }

        } catch (NumberFormatException e) {
            logger.info("Invalid port number");
            System.exit(1);
        }
    }
}
