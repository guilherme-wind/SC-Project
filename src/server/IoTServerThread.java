package server;

import java.net.Socket;

import server.model.Session;
import utils.ConsoleColors;
import utils.IoTCLI;
import utils.IoTMessageType;
import utils.IoTStream;

public class IoTServerThread extends Thread {
    private Boolean running = false;
    private IoTStream ioTStream;
    private IoTCLI cli;
    private String color;

    public IoTServerThread(Socket socket) {
        this.ioTStream = new IoTStream(socket);
        this.running = true;
        this.cli = IoTCLI.getInstance();
        this.color = ConsoleColors.Service.getInstance().getRandomUnusedColor("Regular");
    }

    /**
     * Waits for client requests and forwards them the handler.
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
            cli.print(
                String.format("Received message %s!", receivedMessage), session.toString(), color
            );
            
            // response
            IoTMessageType responseMessage = handler.process(receivedMessage, session, dbContext);
            cli.print(
                String.format("Processed message and response will be %s", responseMessage), session.toString(), color
            );

            if (responseMessage != null) {
                cli.print("Sending!", session.toString(), color);
                if (this.ioTStream.write(responseMessage))
                    cli.print("Sent!", session.toString(), color);
            }
        }
    }    
}