package src.server;

import src.utils.IoTMessage;
import src.utils.IoTMessageType;
import src.utils.IoTOpcodes;

import java.util.EnumMap;
import java.util.function.Function;

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

    public IoTMessageType process(IoTMessageType message) {
        if (message == null)
            return null;

        IoTOpcodes opcode = message.getOpcode();
        IoTMessageHandlerFunction function = functions.get(opcode);

        if (function != null) {
            return function.apply(message);
        } else {
            System.out.println("No handler found for opcode: " + opcode);
            return null;
        }
    }

    private void initializeFunctions() {
        functions.put(IoTOpcodes.VALIDATE_USER, this::handleValidateUser);
        functions.put(IoTOpcodes.VALIDATE_DEVICE, this::handleValidateDevice);
    }

    private IoTMessageType handleValidateUser(IoTMessageType message) {
        IoTMessageType response = new IoTMessage();
        response.setOpCode(IoTOpcodes.OK_USER);
        return response;
    }

    private IoTMessageType handleValidateDevice(IoTMessageType message) {
        IoTMessageType response = new IoTMessage();
        response.setOpCode(IoTOpcodes.OK_DEVID);
        return response;
    }

    // TODO add more handlers

    @FunctionalInterface
    interface IoTMessageHandlerFunction extends Function<IoTMessageType, IoTMessageType> {
    }
}