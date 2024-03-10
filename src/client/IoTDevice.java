package src.client;

import java.io.*;
import java.net.URLDecoder;

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

        // Command line argument validation
        int args_verify = verifyCmdArgs(args);
        if (args_verify < 0) {
            cli.printErr("Wrong input arguments!");
            cli.print(USAGE);
            close();
        }

        // Initialize class fields using command line arguments
        int init_res = initialize(args);
        if (init_res < 0) {
            cli.printErr("Error initializing device!");
            close();
        }

        // User authentication
        int auth_user_res = -1;
        while (auth_user_res < 0) {
            cli.print("Please introduce password");
            String pwd = cli.getUserInput();
            // TODO sanitize user input
    
            System.out.println(String.format("-> /authUser %s : %s", userId, pwd));
            auth_user_res = stub.authenticateUser(userId, pwd);
            System.out.println(String.format("<- %d", auth_user_res));
            if (auth_user_res < 0) {
                cli.printErr("Error authenticating user!");
                close();
            }
        }

        // Device authentication
        System.out.println(String.format("-> /authDev %s : %s", userId, devId));
        int auth_dev_res = stub.authenticateDevice(devId);
        System.out.println(String.format("<- %d", auth_dev_res));
        if (auth_dev_res < 0) {
            cli.printErr("Error authenticating device!");
            close();
        }

        // Program authentication
        int auth_prog_res = authenticateProgram();
        if (auth_prog_res < 0) {
            cli.printErr("Error authenticating program!");
            close();
        }

        // Accept commands

        System.out.println("Finished!");
        close();   
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

    /**
     * Gets the name of the class file and it's size
     * and authenticates with the server.
     * NOT WORKING YET!
     * @see
     *      Untested!
     * @return
     *      0 if authenticated successfully;
     *      -1 if authentication failed;
     */
    private static int authenticateProgram() {
        String path = IoTDevice.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(path);
        if (!file.exists())
            return -1;
        long size = file.length();
        String name = file.getName();
        int auth_res = stub.authenticateProgram(name, size);
        if (auth_res < 0)
            return -1;
        return 0;
    }

    /**
     * Releases resources, closes connection and 
     * terminates program.
     */
    public static void close() {
        if (stub != null)
            stub.close();
        if (cli != null)
            cli.close();
        System.exit(0);
    }
}