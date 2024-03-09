package src.Client;

import src.Utils.IoTMessage;
import src.Utils.IoTMessageType;
import src.Utils.IoTOpcodes;

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

    /**
     * Authenticates user with the server.
     * @param user
     *      User name.
     * @param password
     *      User password.
     * @return
     *      1 if new user is created successfully;
     *      0 if existing user authenticated successfully;
     *      -1 if password provided is wrong;
     *      -2 if socket error occured;
     */
    protected int authenticateUser(String user, String password) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.VALIDADE_USER);
        request.setUserId(user);
        request.setUserPwd(password);

        IoTMessageType response = null;

        try {
            outputStream.writeObject(request);
            response = (IoTMessageType) inputStream.readObject();
        } catch (Exception e) {
            return -2;
        }
        
        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_USER))
            return 0;
        else if (respcode.equals(IoTOpcodes.OK_NEW_USER))
            return 1;
        else if (respcode.equals(IoTOpcodes.WRONG_PWD))
            return -1;

        return -2;
    }

    /**
     * Authenticates device with the server.
     * @param devId
     *      Device id.
     * @return
     *      0 if authenticated successfully;
     *      -1 if authentication failed;
     */
    protected int authenticateDevice(int devId) {
        return 0;
    }
}
