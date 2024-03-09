package src.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import src.utils.IoTMessage;
import src.utils.IoTMessageType;
import src.utils.IoTOpcodes;
import src.utils.IoTStream;

public class IoTClientStub {
    private static IoTClientStub instance = null;
    private IoTStream iotStream;

    /**
     * Private constructor, use static method getInstance() instead.
     * @param socket
     */
    private IoTClientStub(Socket socket) {
        iotStream = new IoTStream(socket);
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
        if (instance != null)
            return instance;
        try {
            Socket socket = new Socket(serverIp, serverPort);
            instance = new IoTClientStub(socket);
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
        request.setOpCode(IoTOpcodes.VALIDATE_USER);
        request.setUserId(user);
        request.setUserPwd(password);

        if (!iotStream.write(request))
            return -2;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -2;
        
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
     *      -2 if socket error occured;
     */
    protected int authenticateDevice(int devId) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.VALIDATE_DEVICE);
        request.setDevId(devId);

        if (!iotStream.write(request))
            return -2;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -2;

        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_DEVID))
            return 0;
        else if (respcode.equals(IoTOpcodes.NOK_DEVID))
            return -1;

        return -2;
    }
}
