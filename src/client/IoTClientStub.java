package src.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import src.utils.IoTMessage;
import src.utils.IoTMessageType;
import src.utils.IoTOpcodes;
import src.utils.IoTStream;

public class IoTClientStub {

    // Singleton
    private static IoTClientStub instance = null;

    // Client network connection
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

    /**
     * Authenticates executing with the server.
     * @param programName
     *      Name of current executing program.
     * @param size
     *      Size of current executing program.
     * @return
     *      0 if authenticated successfully;
     *      -1 if authentication failed;
     *      -2 if socket error occured;
     */
    protected int authenticateProgram(String programName, long size) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.VALIDATE_PROGRAM);
        request.setProgramName(programName);
        request.setProgramSize(size);

        if (!iotStream.write(request))
            return -2;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -2;

        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_TESTED))
            return 0;
        else if (respcode.equals(IoTOpcodes.NOK_TESTED))
            return -1;
        
        return -2;
    }

    protected int terminateProgram() {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.EXIT);

        if (!iotStream.write(request))
            return -2;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -2;
        
        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED))
            return 0;
        else if (respcode.equals(IoTOpcodes.NOK))
            return -1;

        return -2;
    }

    /**
     * Creates a domain with the given name.
     * @param domainName
     *      Name of the domain to be created.
     * @return
     *      0 if created successfully;
     *      -1 if domain with the same name already exists;
     *      -2 if socket error occured;
     */
    protected int createDomain(String domainName) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.CREATE_DOMAIN);
        request.setDomainName(domainName);

        if (!iotStream.write(request))
            return -2;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -2;

        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED))
            return 0;
        else if (respcode.equals(IoTOpcodes.NOK_ALREADY_EXISTS))
            return -1;
        return -2;
    }


    /**
     * Adds an user to a domain.
     * @param userName
     *      User id.
     * @param domainName
     *      Domain name.
     * @return
     *      1 if the user is already in the domain;
     *      0 if added successfully;
     *      -1 if the user doesn't exist;
     *      -2 if the domain doesn't exist;
     *      -3 if the current user isn't the owner of the domain(no permissions);
     *      -4 if socket error occured;
     */
    protected int addUserDomain(String userName, String domainName) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.ADD_USER_DOMAIN);
        request.setUserId(userName);
        request.setDomainName(domainName);

        if (!iotStream.write(request))
            return -4;
        
        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -4;

        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED))
            return 0;
        else if (respcode.equals(IoTOpcodes.NOK_NO_USER))
            return -1;
        else if (respcode.equals(IoTOpcodes.NOK_NO_DOMAIN))
            return -2;
        else if (respcode.equals(IoTOpcodes.NOK_NO_PERMISSIONS))
            return -3;
        else if (respcode.equals(IoTOpcodes.NOK_ALREADY_EXISTS))
            return 1;
        return -4;
    }

    /**
     * Registers the current device to the given domain.
     * @param domainName
     *      Domain name.
     * @return
     *      1 if the device is already registered in the domain;
     *      0 if registered successfully;
     *      -1 if the domain doesn't exist;
     *      -2 if the current user doesn't have permissions;
     *      -3 if socket error occured;
     */
    protected int registerDevice(String domainName) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.REGISTER_DEVICE_DOMAIN);
        request.setDomainName(domainName);

        if (!iotStream.write(request))
            return -4;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -4;

        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED))
            return 0;
        else if (respcode.equals(IoTOpcodes.NOK_NO_DOMAIN))
            return -1;
        else if (respcode.equals(IoTOpcodes.NOK_NO_PERMISSIONS))
            return -2;
        else if (respcode.equals(IoTOpcodes.NOK_ALREADY_EXISTS))
            return 1;
        return -3;
    }

    /**
     * Sends temperature value to the server.
     * @param temperature
     *      Temperature value.
     * @return
     *      0 if sent successfully;
     *      -1 if socket error occured;
     */
    protected int sendTemp(float temperature) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.SEND_TEMP);
        request.setTemp(temperature);

        if (!iotStream.write(request))
            return -1;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -1;

        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED))
            return 0;
        return -1;
    }

    /**
     * Sends image to the server.
     * @param image
     *      Image byte array.
     * @return
     *      0 if sent successfully;
     *      -1 if socket error occured;
     */
    protected int sendImage(byte[] image) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.SEND_IMAGE);
        request.setImage(image);

        if (!iotStream.write(request))
            return -1;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -1;

        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED))
            return 0;
        return -1;
    }

    /**
     * Retrieves temperature values from all devices in
     * the current domain.
     * @param domainName
     *      Domain name from where temperatures are retrieved.
     * @return
     *      Float array representing temperature readings.
     *      Null if one of the following errors occured:
     *          - No access permission;
     *          - Domain doesn't exist;
     *          - Socket error;
     */
    protected float[] getTemp(String domainName) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.GET_TEMP);
        request.setDomainName(domainName);

        if (!iotStream.write(request))
            return null;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return null;

        IoTOpcodes respcode = response.getOpcode();
        float[] temps = response.getTemps();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED) &&
            temps != null)
            return temps;
        return null;
    }

    /**
     * Retrieves image from a device of a user.
     * @param userId
     *      User id.
     * @param devId
     *      Device id.
     * @return
     *      Image byte array if retrieved successfully;
     *      Null if one of the following errors occured:
     *          - No access permission;
     *          - User doesn't exist;
     *          - Device doesn't exist;
     *          - Socket error;
     */
    protected byte[] getUserImage(String userId, int devId) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.GET_USER_IMAGE);
        request.setUserId(userId);
        request.setDevId(devId);

        if (!iotStream.write(request))
            return null;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return null;

        IoTOpcodes respcode = response.getOpcode();
        byte[] image = response.getImage();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED) &&
            image != null)
            return image;
        return null;
    }

    protected void close() {
        iotStream.close();
    }
}