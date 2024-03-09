package src.server;

import java.net.Socket;

import src.utils.IoTMessageType;
import src.utils.IoTStream;

public class IoTServerThread extends Thread {
    private Boolean running = false;
    private IoTStream ioTStream;

    public IoTServerThread(Socket socket) {
        this.ioTStream = new IoTStream(socket);
        this.running = true;
    }

    @Override
    public void run() {
        IoTServerRequestHandler handler = IoTServerRequestHandler.getInstance();
        while (this.running && this.ioTStream.ready()) {
            IoTMessageType receivedMessage = (IoTMessageType) this.ioTStream.read();
            System.out.println(String.format("Received message %s!", receivedMessage));
            if (receivedMessage == null)
                return;
            

            // reponse
            IoTMessageType responseMessage = handler.process(receivedMessage);
            System.out.println("Processed message and response will be " + responseMessage);
            if (responseMessage != null) {
                System.out.println("Sending!");
                if (this.ioTStream.write(responseMessage))
                    System.out.println("Sent!");
            }            
        }
    }    
}