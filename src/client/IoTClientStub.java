package client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

import utils.IoTMessage;
import utils.IoTMessageType;
import utils.IoTOpcodes;
import utils.IoTPersistance;
import utils.IoTStream;

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
    protected static synchronized IoTClientStub getInstance(String serverIp, int serverPort) {
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
     * @return <ul>
     *      <li> 1 if new user is created successfully;
     *      <li> 0 if existing user authenticated successfully;
     *      <li> -1 if password provided is wrong;
     *      <li> -2 if socket error occured;
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
     * @return <ul>
     *      <li> 0 if authenticated successfully;
     *      <li> -1 if authentication failed;
     *      <li> -2 if socket error occured;
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
     * @return <ul>
     *      <li> 0 if authenticated successfully;
     *      <li> -1 if authentication failed;
     *      <li> -2 if socket error occured;
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

    /**
     * Tells the server that the client is closing to free
     * all resources allocated to the session.
     * @return <ul>
     *      <li> 0 if the server acknowlegdes and allows the
     *           termination correctly;
     *      <li> -1 if the server acknowlegdes the termination
     *           of the client but doesn't allow;
     *      <li> -2 if socket or response semantic error occured;
     */
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
     * @return <ul>
     *      <li> 0 if created successfully;
     *      <li> -1 if domain with the same name already exists;
     *      <li> -2 if socket error occured;
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
     *      <li> 1 if the user is already in the domain;
     *      <li> 0 if added successfully;
     *      <li> -1 if the user doesn't exist;
     *      <li> -2 if the domain doesn't exist;
     *      <li> -3 if the current user isn't the owner of the domain(no permissions);
     *      <li> -4 if socket error occured;
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
     * @return <ul>
     *      <li> 1 if the device is already registered in the domain;
     *      <li> 0 if registered successfully;
     *      <li> -1 if the domain doesn't exist;
     *      <li> -2 if the current user doesn't have permissions;
     *      <li> -3 if socket error occured;
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
     * @return <ul>
     *      <li> 0 if sent successfully;
     *      <li> -1 if socket error occured;
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
     * @param filename
     *      Image file name.
     * @return <ul>
     *      <li> 0 if sent successfully;
     *      <li> -1 if error occured while reading the file;
     *      <li> -2 if socket or response semantic error occured;
     */
    protected int sendImage(String filepath) {
        System.out.println("Received filepath: " + filepath);
        byte[] img = IoTPersistance.read(filepath);
        if (img == null)
            return -1;
        String filename = Paths.get(filepath).getFileName().toString();
        long filesize = new File(filepath).length();

        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.SEND_IMAGE);
        request.setImageName(filename);
        request.setImageSize(filesize);
        request.setImage(img);

        if (!iotStream.write(request))
            return -2;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -2;

        IoTOpcodes respcode = response.getOpcode();
        if (respcode.equals(IoTOpcodes.OK_ACCEPTED))
            return 0;
        return -2;
    }

    /**
     * Retrieves temperature values from all devices in
     * the current domain.
     * @param domainName
     *      Domain name from where temperatures are retrieved.
     * @return <ul>
     *      <li> 0 if received values successfully;
     *      <li> -1 if the current user doesn't have permissions;
     *      <li> -2 if the domain doesn't exist;
     *      <li> -3 if the domain hasn't no temperature data;
     *      <li> -4 if socket or response semantic error occured;
     */
    protected int getTemperaturesInDomain(String domainName) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.GET_TEMP);
        request.setDomainName(domainName);

        if (!iotStream.write(request))
            return -3;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -3;

        IoTOpcodes respcode = response.getOpcode();
        
        switch (respcode) {
            case NOK_NO_PERMISSIONS:
            return -1;
            
            case NOK_NO_DOMAIN:
            return -2;
            
            case OK_ACCEPTED:
            break;
            
            default:
            return -3;
        }

        Map<String,Float> temps = response.getTemps();
        if (temps == null)
            return -3;
        
        StringJoiner sj = new StringJoiner(",");
        for (Map.Entry<String, Float> entry : temps.entrySet()) {
            String devtemp = String.format("Device %s - Temp %f", entry.getKey(), entry.getValue());
            sj.add(devtemp);
        }
        File file = new File("domain_" + domainName + "_temps.txt");
        IoTPersistance.write(sj.toString(), file, false);
                
        return 0;
    }

    /**
     * Retrieves image from a device of a user.
     * @param userId
     *      User id.
     * @param devId
     *      Device id.
     * @return <ul>
     *      <li> 1 if the device hasn't uploaded any image yet;
     *      <li> 0 if image received successfully;
     *      <li> -1 if the current user doesn't have permissions;
     *      <li> -2 if the user doesn't exist;
     *      <li> -3 if the device doesn't exist;
     *      <li> -4 if socket or response semantic error occured;
     */
    protected int getUserImage(String userId, int devId) {
        IoTMessageType request = new IoTMessage();
        request.setOpCode(IoTOpcodes.GET_USER_IMAGE);
        request.setUserId(userId);
        request.setDevId(devId);

        if (!iotStream.write(request))
            return -4;

        IoTMessageType response = (IoTMessageType) iotStream.read();
        if (response == null)
            return -4;

        IoTOpcodes respcode = response.getOpcode();
        switch (respcode) {
            case NOK_NO_PERMISSIONS:
            return -1;
            
            case NOK_NO_USER:
            return -2;
            
            case NOK_NO_DEVICE:
            return -3;

            case NOK_NO_DATA:
            return 1;
            
            case OK_ACCEPTED:
            break;
            
            default:
            return -4;
        }
        
        long imagesize = response.getImageSize();
        if (imagesize <= 0)
            return -4;
        byte[] imagedata = response.getImage();
        if (imagedata == null)
            return -4;
        String imagename = response.getImageName();
        if (imagename == null)
            return -4;

        // Choose the smallest value to truncate the byte array
        byte[] image = Arrays.copyOf(imagedata, Math.min(Math.toIntExact(imagesize), imagedata.length));

        IoTPersistance.write(image, new File(imagename), false);

        return 0;
    }

    /**
     * Tells the server to terminate and 
     * closes the connection.
     */
    protected int close() {
        int status = terminateProgram();
        iotStream.close();
        return status;
    }
}