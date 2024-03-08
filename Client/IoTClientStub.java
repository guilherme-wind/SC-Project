package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class IoTClientStub {

    private Socket clientSocket;
    private static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;

    private IoTClientStub(String serverIp, int serverPort) {
        try {
            clientSocket = new Socket(serverIp, serverPort);
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }
    }

    protected static IoTClientStub getInstance() {
        return null;
    }
}
