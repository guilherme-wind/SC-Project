package server;

import server.model.Device;
import server.model.Domain;
import server.model.Session;
import server.model.User;
import utils.IoTAuth;
import utils.IoTMessage;
import utils.IoTMessageType;
import utils.IoTOpcodes;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class IoTServerRequestHandler {
    private static IoTServerRequestHandler instance;
    private final EnumMap<IoTOpcodes, IoTMessageHandlerFunction> functions;

    private IoTServerRequestHandler() {
        functions = new EnumMap<>(IoTOpcodes.class);
        initializeFunctions();
    }

    public static synchronized IoTServerRequestHandler getInstance() {
        if (instance == null) {
            instance = new IoTServerRequestHandler();
        }
        return instance;
    }

    public IoTMessageType process(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        if (message == null)
            return null;

        IoTOpcodes opcode = message.getOpcode();
        IoTMessageHandlerFunction function = functions.get(opcode);

        if (function != null) {
            return function.apply(message, session, dbContext);
        } else {
            System.out.println("No handler found for opcode: " + opcode);
            return null;
        }
    }

    /**
     * Associates each request opcode with it's 
     * corresponding handler function.
     */
    private void initializeFunctions() {
        functions.put(IoTOpcodes.VALIDATE_USER, this::handleValidateUser);
        functions.put(IoTOpcodes.VALIDATE_DEVICE, this::handleValidateDevice);
        functions.put(IoTOpcodes.VALIDATE_PROGRAM, this::handleValidateProgram);
        functions.put(IoTOpcodes.CREATE_DOMAIN, this::handleCreateDomain);
        functions.put(IoTOpcodes.EXIT, this::handleTerminateProgram);
        functions.put(IoTOpcodes.ADD_USER_DOMAIN, this::handleAddToDomain);
        functions.put(IoTOpcodes.REGISTER_DEVICE_DOMAIN, this::handleRegisterCurrentDeviceToDomain);
        functions.put(IoTOpcodes.SEND_TEMP, this::handleSendTemperature);
        functions.put(IoTOpcodes.SEND_IMAGE, this::handleSendImage);
        functions.put(IoTOpcodes.GET_TEMP, this::handleReceiveTemperature);
        functions.put(IoTOpcodes.GET_USER_IMAGE, this::handleReceiveImage);
    }

    /**
     * Validates users, finds in database user record
     * and compares with the given credencials, or
     * creates a new user record if the user doesn't 
     * exist in the database.
     * @param message
     *      Request message.
     * @param session
     *      Connection session.
     * @param dbContext
     *      Server database.
     * @return
     *      Server response.
     */
    private IoTMessageType handleValidateUser(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String userName = message.getUserId();
        String password = message.getUserPwd();

        IoTMessageType response = new IoTMessage();

        // If the user has already authenticated
        if (!session.getAuthState().equals(IoTAuth.NONE) &&
            session.getUser() != null) {
            response.setOpCode(IoTOpcodes.OK_USER);
            return response;
        }

        if (dbContext.containsUser(userName)) { // user exists
            User user = dbContext.getUser(userName);
            if (password.equals(user.getPassword())) {
                session.setAuthState(IoTAuth.USER);
                session.setUser(user);
                response.setOpCode(IoTOpcodes.OK_USER);
            } 
            else {
                response.setOpCode(IoTOpcodes.WRONG_PWD);
            }
                
        } else { // new user
            User user = new User(userName, password);
            dbContext.addUser(user);
            session.setAuthState(IoTAuth.USER);
            session.setUser(user);
            response.setOpCode(IoTOpcodes.OK_NEW_USER);
            dbContext.onUserUpdate();
        }
        return response;
    }

    private IoTMessageType handleValidateDevice(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        User user = session.getUser();
        int devId = message.getDevId();

        String iotDeviceId = String.format("%s:%d", user.getName(), message.getDevId());

        IoTMessageType response = new IoTMessage();

        // If the user hasn't finished the previous authentication
        if (session.getAuthState().equals(IoTAuth.NONE)) {
            response.setOpCode(IoTOpcodes.NOK_NO_PERMISSIONS);
            return response;
        }

        // If the user already finished the device authentication,
        // i.e., higher than NONE but different to USER means
        // the device auth is already done.
        if (!session.getAuthState().equals(IoTAuth.USER)) {
            response.setOpCode(IoTOpcodes.OK_DEVID);
            return response;
        }

        if (!dbContext.containsDevice(iotDeviceId)) { // new device!
            Device device = new Device(user, devId);
            device.setActive();
            dbContext.addDevice(device);
            session.setAuthState(IoTAuth.USER_DEVICE);
            session.setDevice(device);
            response.setOpCode(IoTOpcodes.OK_DEVID);
            dbContext.onDeviceUpdate();
        } else {
            Device device = dbContext.getDevice(iotDeviceId);
            if (!device.isActive()) {
                device.setActive();
                session.setAuthState(IoTAuth.USER_DEVICE);
                session.setDevice(device);
                response.setOpCode(IoTOpcodes.OK_DEVID);
            }
            else {
                response.setOpCode(IoTOpcodes.NOK_DEVID);
            }
            
        }

        return response;
    }

    private IoTMessageType handleValidateProgram(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String programname = message.getProgramName();
        long programsize = message.getProgramSize();
        

        IoTMessageType response = new IoTMessage();
        if (programname == null || programsize <= 0) {
            response.setOpCode(IoTOpcodes.NOK_TESTED); 
            return response;
        }

        if (!programname.equals(dbContext.getClientProgramName()) ||
            programsize != dbContext.getClientProgramSize()) {
            response.setOpCode(IoTOpcodes.NOK_TESTED); 
            return response;
        }

        response.setOpCode(IoTOpcodes.OK_TESTED);
        session.setAuthState(IoTAuth.COMPLETE);
        return response;
    }

    private IoTMessageType handleCreateDomain(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String domainName = message.getDomainName();

        IoTMessageType response = new IoTMessage();

        if (dbContext.containsDomain(domainName)) {
            response.setOpCode(IoTOpcodes.NOK_ALREADY_EXISTS);
            return response;
        }

        // new domain!
        Domain domain = new Domain(domainName, session.getUser());
        dbContext.addDomain(domain);
        response.setOpCode(IoTOpcodes.OK_ACCEPTED);
        dbContext.onDomainUpdate();

        return response;
    }

    private IoTMessageType handleAddToDomain(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String domainName = message.getDomainName();
        String userName = message.getUserId();
        User user = session.getUser();

        IoTMessageType response = new IoTMessage();
        IoTOpcodes code = dbContext.addUserToDomain(user, userName, domainName);
        response.setOpCode(code);
        if (code == IoTOpcodes.OK_ACCEPTED)
            dbContext.onDomainUpdate();

        return response;
    }

    private IoTMessageType handleRegisterCurrentDeviceToDomain(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String domainName = message.getDomainName();
        User user = session.getUser();

        IoTMessageType response = new IoTMessage();
        IoTOpcodes code = dbContext.registerDeviceToDomain(user, session.getDevice(), domainName);
        response.setOpCode(code);
        if (code == IoTOpcodes.OK_ACCEPTED)
            dbContext.onDomainUpdate();
        return response;
    }

    private IoTMessageType handleSendTemperature(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String temperature = String.valueOf(message.getTemp());
        Device device = session.getDevice();

        IoTMessageType response = new IoTMessage();
        device.writeTemperature(temperature);
        response.setOpCode(IoTOpcodes.OK_ACCEPTED);

        return response;
    }

    private IoTMessageType handleSendImage(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        Device device = session.getDevice();
        
        byte[] image = message.getImage();
        long imgsize = message.getImageSize();
        String imgname = message.getImageName();
        // TODO: verify argument validity

        // Choose the smallest size to create a new image
        byte[] imagebytes = Arrays.copyOf(image, Math.min(Math.toIntExact(imgsize), image.length));
        device.writeImage(imgname, imagebytes);

        IoTMessageType response = new IoTMessage();
        response.setOpCode(IoTOpcodes.OK_ACCEPTED);
        dbContext.onDeviceUpdate();
        dbContext.onDomainUpdate();

        return response;
    }

    private IoTMessageType handleReceiveTemperature(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String domainName = message.getDomainName();
        User user = session.getUser();

        IoTMessageType response = new IoTMessage();
        if (!dbContext.containsDomain(domainName)) {
            response.setOpCode(IoTOpcodes.NOK_NO_DOMAIN);
            return response;
        }

        Domain domain = dbContext.getDomain(domainName);
        if (!domain.contains(user)) {
            response.setOpCode(IoTOpcodes.NOK_NO_PERMISSIONS);
            return response;
        }

        Map<String,Float> temps = domain.extractTemperatures();
        if (temps.size() == 0) {
            response.setOpCode(IoTOpcodes.NOK_NO_DATA);
            return response;
        }

        response.setTemps(temps);

        response.setOpCode(IoTOpcodes.OK_ACCEPTED);

        return response;
    }

    private IoTMessageType handleReceiveImage(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String deviceId = String.format("%s:%s", message.getUserId(), message.getDevId());
        User requestingUser = session.getUser();

        IoTMessageType response = new IoTMessage();
        if (!dbContext.containsDevice(deviceId)) {
            response.setOpCode(IoTOpcodes.NOK_NO_DEVICE);
            return response;
        }

        Device device = dbContext.getDevice(deviceId);
        if (!dbContext.canUserReceiveDataFromDevice(requestingUser, device)) {
            response.setOpCode(IoTOpcodes.NOK_NO_PERMISSIONS);
            return response;
        }

        Optional<byte[]> image = device.readImage();
        if (!image.isPresent()) {
            response.setOpCode(IoTOpcodes.NOK_NO_DATA);
            return response;
        }
        long filesize = image.get().length;

        response.setImageName(device.getImgFileName().get());
        response.setImageSize(filesize);
        response.setImage(image.get());

        response.setOpCode(IoTOpcodes.OK_ACCEPTED);

        return response;
    }

    private IoTMessageType handleTerminateProgram(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        session.close();
        IoTMessageType response = new IoTMessage();
        response.setOpCode(IoTOpcodes.OK_ACCEPTED);
        return response;
    }

    // TODO add more handlers

    @FunctionalInterface
    interface IoTMessageHandlerFunction {
        IoTMessageType apply(IoTMessageType message, Session session, IoTServerDatabase dbContext);
    }
}