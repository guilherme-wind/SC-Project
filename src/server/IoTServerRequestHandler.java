package src.server;

import src.server.model.Device;
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
    }

    private IoTMessageType handleValidateUser(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        String userName = message.getUserId();
        String password = message.getUserPwd();

        IoTMessageType response = new IoTMessage();
        if (dbContext.containsUser(userName)) { // user exists
            User user = dbContext.getUser(userName);
            if (password.equals(user.getPassword())) {
                session.setAuthState(IoTAuth.User);
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
            session.setAuthState(IoTAuth.User);
            session.setUser(user);
            response.setOpCode(IoTOpcodes.OK_NEW_USER);
        }
        return response;
    }

    private IoTMessageType handleValidateDevice(IoTMessageType message, Session session, IoTServerDatabase dbContext) {
        User user = session.getUser();
        String iotDeviceId = String.format("%s:%d", user, message.getDevId());

        IoTMessageType response = new IoTMessage();

        if (!dbContext.containsDevice(iotDeviceId)) { // new device!
            Device device = new Device(iotDeviceId);
            dbContext.addDevice(device);
            session.setAuthState(IoTAuth.UserDevice);
            session.setDevice(device);
            response.setOpCode(IoTOpcodes.OK_DEVID);
        } 
        else {
            // TODO review NOK policy.... open and auth?
            response.setOpCode(IoTOpcodes.NOK_DEVID);
        }

        return response;
    }

    // TODO add more handlers

    @FunctionalInterface
    interface IoTMessageHandlerFunction {
        IoTMessageType apply(IoTMessageType message, Session session, IoTServerDatabase dbContext);
    }
}