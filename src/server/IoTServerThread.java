package src.server;

import java.net.Socket;

import src.server.model.Session;
import src.utils.IoTMessageType;
import src.utils.IoTStream;

public class IoTServerThread extends Thread {
    private Boolean running = false;
    private IoTStream ioTStream;

    public IoTServerThread(Socket socket) {
        this.ioTStream = new IoTStream(socket);
        this.running = true;
    }

    /**
     * Waits for client requests and forwards them to upper layers.
     */
    @Override
    public void run() {
        IoTServerRequestHandler handler = IoTServerRequestHandler.getInstance();
        IoTServerDatabase dbContext = IoTServerDatabase.getInstance();
        Session session = new Session();        
        while (this.running && this.ioTStream.ready() && !Thread.currentThread().isInterrupted()) {

            IoTMessageType receivedMessage = (IoTMessageType) this.ioTStream.read();
            if (receivedMessage == null)
                return;
            System.out.println(String.format("Received message %s!", receivedMessage));
            
            // response
            IoTMessageType responseMessage = handler.process(receivedMessage, session, dbContext);
            System.out.println("Processed message and response will be " + responseMessage);
            if (responseMessage != null) {
                System.out.println("Sending!");
                if (this.ioTStream.write(responseMessage))
                    System.out.println("Sent!");
            }
        }
        System.out.println("Mewing: Bye bye");
    }    
}