package src.server;

import src.server.model.Device;
import src.server.model.Domain;
import src.server.model.Session;
import src.server.model.User;
import src.utils.IoTAuth;
import src.utils.IoTMessage;
import src.utils.IoTMessageType;
import src.utils.IoTOpcodes;

import java.util.EnumMap;

public class IoTServerRequestHandler {
    private static IoTServerRequestHandler instance;
    private final EnumMap<IoTOpcodes, IoTMessageHandlerFunction> functions;

    private IoTServerRequestHandler() {
        functions = new EnumMap<>(IoTOpcodes.class);
        initializeFunctions();
    }

    public static IoTServerRequestHandler getInstance() {
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

    private void initializeFunctions() {
        functions.put(IoTOpcodes.VALIDATE_USER, this::handleValidateUser);
        functions.put(IoTOpcodes.VALIDATE_DEVICE, this::handleValidateDevice);
        functions.put(IoTOpcodes.VALIDATE_PROGRAM, this::handleValidateProgram);
        functions.put(IoTOpcodes.CREATE_DOMAIN, this::handleCreateDomain);
    }

    private IoTMessageType handleValidateUser(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String userName = message.getUserId();
        String password = message.getUserPwd();

        IoTMessageType response = new IoTMessage();
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
                
        }
        else { // new user
            User user = new User(userName, password);
            dbContext.addUser(user);
            session.setAuthState(IoTAuth.USER);
            session.setUser(user);
            response.setOpCode(IoTOpcodes.OK_NEW_USER);
        }
        return response;
    }

    private IoTMessageType handleValidateDevice(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        User user = session.getUser();
        int devId = message.getDevId();

        String iotDeviceId = String.format("%s:%d", user.getName(), message.getDevId());

        IoTMessageType response = new IoTMessage();

        if (!dbContext.containsDevice(iotDeviceId)) { // new device!
            Device device = new Device(user, devId);
            dbContext.addDevice(device);
            session.setAuthState(IoTAuth.USER_DEVICE);
            session.setDevice(device);
            response.setOpCode(IoTOpcodes.OK_DEVID);
        } 
        else {
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
        // TODO understand which sort of validation we should perform here
        IoTMessageType response = new IoTMessage();
        response.setOpCode(IoTOpcodes.OK_TESTED);
        session.setAuthState(IoTAuth.COMPLETE);
        return response;
    }

    private IoTMessageType handleCreateDomain(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String domainName = message.getDomainName();

        IoTMessageType response = new IoTMessage();

        if (dbContext.containsDomain(domainName)) {
            response.setOpCode(IoTOpcodes.NOK);
            return response;
        }

        // new domain!
        Domain domain = new Domain(domainName, session.getUser());
        dbContext.addDomain(domain);
        response.setOpCode(IoTOpcodes.OK_ACCEPTED);

        return response;
    }
    // TODO add more handlers

    @FunctionalInterface
    interface IoTMessageHandlerFunction {
        IoTMessageType apply(IoTMessageType message, Session session, IoTServerDatabase dbContext);
    }
}