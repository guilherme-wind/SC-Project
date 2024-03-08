package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class IoTClientStub {

    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /**
     * Private constructor, use static method getInstance() instead.
     * @param socket
     * @param inStream
     * @param outStream
     */
    private IoTClientStub(Socket socket, ObjectInputStream inStream, ObjectOutputStream outStream) {
        clientSocket = socket;
        outputStream = outStream;
        inputStream = inStream;
    }

    /**
     * Returns an instance of the client stub or null in case of error.
     * @param serverIp
     *          Server ip address.
     * @param serverPort
     *          Server port.
     * @return
     *          Instance of client stub or null if error occured during 
     *          intialization.
     */
    protected static IoTClientStub getInstance(String serverIp, int serverPort) {
        IoTClientStub instance = null;
        try {
            Socket clientSocket = new Socket(serverIp, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            instance = new IoTClientStub(clientSocket, inputStream, outputStream);
        } catch (UnknownHostException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
        return instance;
    }

    protected int authenticateUser(String user, String password) {
        
        return 0;
    }
}
