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
        while (this.running && this.ioTStream.ready()) {
            IoTMessageType receivedMessage = (IoTMessageType) this.ioTStream.read();
            System.out.println(String.format("Received message %s!", receivedMessage));
            return;
        }
    }    
}