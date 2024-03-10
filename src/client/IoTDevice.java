package src.client;

import java.io.*;

public class IoTDevice {
    private static final String USAGE = "USAGE: IoTDevice <serverAddress> <dev-id> <user-id>";
    private static final int DEFAULT_SERVERPORT = 12345;

    // Simple UI
    private static IoTCLI cli;

    private static IoTClientStub stub;

    private static int devId;
    private static String userId;

    public static void main(String[] args) throws IOException {

        // Initialize cli
        cli = IoTCLI.getInstance();
        int execution_status = 0;

        // Command line argument validation
        execution_status = verifyCmdArgs(args);
        if (execution_status < 0) {
            cli.printErr("Wrong input arguments!");
            cli.print(USAGE);
            cli.close();
            return;
        }

        // Initialize class fields using command line arguments
        execution_status = initialize(args);
        if (execution_status < 0) {
            cli.printErr("Error initializing device!");
            cli.close();
            return;
        }

        // retrieve pass
        cli.print("Please introduce password");
        String pwd = cli.getUserInput();
        // TODO sanitize user input

        // perform user auth
        System.out.println(String.format("-> /authUser %s : %s", userId, pwd));
        execution_status = stub.authenticateUser(userId, pwd);
        System.out.println(String.format("<- %d", execution_status));
        if (execution_status < 0) {
            cli.printErr("Error authenticating user!");
            cli.close();
            return;
        }

        // perform device auth
        System.out.println(String.format("-> /authDev %s : %s", userId, devId));
        execution_status = stub.authenticateDevice(devId);
        System.out.println(String.format("<- %d", execution_status));
        if (execution_status < 0) {
            cli.printErr("Error authenticating device!");
            cli.close();
            return;
        }

        // perform program auth
        String programName = IoTDevice.class.getSimpleName();
        int programSize = (int) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        System.out.println(String.format("-> /authProg [%s, %d]", programName, programSize));
        execution_status = stub.authenticateProgram(programName, programSize);
        System.out.println(String.format("<- %d", execution_status));
        if (execution_status < 0) {
            cli.printErr("Error authenticating program!");
            cli.close();
            return;
        }       

        System.out.println("Finished!");
        cli.close();    
    }

    /**
     * Verifies if the command line arguments complies with
     * the format <socket> <dev-id> <user-id>.
     * @param args
     *      Command line arguments.
     * @return
     *      0 if all the inputs are correct;
     *      -1 if number of arguments is wrong;
     *      -2 if socket ip doesn't match IPv4 format;
     *      -3 if socket port isn't a valid value, i.e., [0, 65535].
     *      -4 if dev-id is not a number;
     *      -5 if user-id doesn't meet the expected format;
     */
    private static int verifyCmdArgs(String[] args) {
        if (args.length != 3)
            return -1;

        // Verify <socket>
        // Divides the socket into in maximum 2 parts
        String[] socket = args[0].split(":",2);
        // Pattern to recognize IPv4
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        if (!socket[0].matches(PATTERN))
            return -2;
        // In case the port is provided
        if (socket.length == 2) {
            try {
                int port = Integer.parseInt(socket[1]);
                if (port < 0 || port > 65535)
                    return -3;
            } catch (NumberFormatException e) {
                return -3;
            }
        }
        
        // Verify <dev-id>
        try {
            Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return -4;
        }

        // Verify <user-id>
        // TODO not used for now
        return 0;
    }

    /**
     * Initializes the class fields using the command line arguments.
     * @param args
     *      Command line arguments.
     * @return
     *      0 if initialization finished correctly;
     *      -1 if error while initializing connection with server;
     */
    private static int initialize(String[] args) {
        // Extract socket
        String[] socket = args[0].split(":", 2);
        String serverIp = socket[0];
        int serverPort = 0;
        if(socket.length == 1)
            // Use default port
            serverPort = DEFAULT_SERVERPORT; 
        else
            serverPort = Integer.parseInt(socket[1]);
        
        // Extract devide id
        devId = Integer.parseInt(args[1]);

        // Extract user id
        userId = args[2];
        
        // Initialize stub middleware
        stub = IoTClientStub.getInstance(serverIp, serverPort);

        if (stub == null)
            return -1;

        return 0;
    }
}