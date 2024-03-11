package src.client;

import java.io.*;
import java.util.NoSuchElementException;

import src.utils.IoTPersistance;

public class IoTDevice {
    // This bozo is going to CLI
    private static final String USAGE = "USAGE: IoTDevice <serverAddress> <dev-id> <user-id>";
    private static final int DEFAULT_SERVERPORT = 12345;

    private static final String PROGRAM_NAME = IoTDevice.class.getSimpleName();
    private static final int PROGRAM_SIZE = (int) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());


    // Simple UI
    private static IoTCLI cli;

    private static IoTClientStub stub;

    private static int devId;
    private static String userId;


    private static int performUserAuth() {
        // retrieve pass
        cli.print("Please introduce password: ");
        String pwd = cli.getUserInput();
        // TODO sanitize user input

        cli.print(String.format("-> /authUser %s : %s", userId, pwd));
        int status = stub.authenticateUser(userId, pwd);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Error authenticating user!");
            if (status == -1)
                return performUserAuth();
        }
        return status;
    }

    private static int performDeviceAuth(boolean askNewDevId) {
        if (askNewDevId) {
            cli.print("Please introduce a different devId: ");
            String devIdStr = cli.getUserInput();
            devId = Integer.parseInt(devIdStr);
        }

        cli.print(String.format("-> /authDev %s : %s", userId, devId));
        int status = stub.authenticateDevice(devId);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Error authenticating device!");
            if (status == -1)
                return performDeviceAuth(true);
        }
        return status;
    }

    /**
     * Authenticates executing program
     * with the server.
     * @return
     *      0 if authenticated successfully;
     *      -1 if failed;
     *      -2 if socket error;
     */
    private static int performProgramAuth() {
        cli.print(String.format("-> /authProg [%s, %d]", PROGRAM_NAME, PROGRAM_SIZE));
        int status = stub.authenticateProgram(PROGRAM_NAME, PROGRAM_SIZE);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Error authenticating program!");
        }
        return status;
    }

    /**
     * This bozo is going to CLI fr
     */
    private static void show_menu() {
        cli.print("Menu de Comandos:");
        cli.print("  CREATE <dm>");
        cli.print("  ADD <user> <dm>");
        cli.print("  RD <dm>");
        cli.print("  ET <float>");
        cli.print("  EI <filename.jpg>");
        cli.print("  RT <dm>");
        cli.print("  RI <user-id>:<dev_id>");
        cli.print("Digite um comando:\n");
    }

    //NAO TIREM A MAIN DAQUI, FIQUEI 5min A PROCURA DELA
    public static void main(String[] args) throws IOException {

        // Initialize cli
        cli = IoTCLI.getInstance();
        try{
            // Command line argument validation
            if (verifyCmdArgs(args) < 0) {
                cli.printErr("Wrong input arguments!");
                cli.print(USAGE);
                close();
            }

            // Initialize class fields using command line arguments
            if (initialize(args) < 0)
                close();

            // perform user auth
            if (performUserAuth() < 0)
                close();

            // perform device auth
            if (performDeviceAuth(false) < 0)
                close();


            // perform program auth
            if (performProgramAuth() < 0)
                close();

            userInvoke();
        } catch (NoSuchElementException e) {
            System.out.println("Exit");
            exitCommand();
        }

        System.out.println("Finished!");
        close();
    }

    private static void createCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <domain name>\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }

        String domainName = args[1];

        cli.print(String.format("-> /create %s", domainName));
        int status = stub.createDomain(domainName);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Failed to create domain!");
        }
    }

    private static void addCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <user> and <domain name>\n");
            return;
        }

        if (args.length == 2) {
            cli.printErr("missing arguments, we are expecting 2 arguments: <user> and <domain name>\n");
            return;
        }

        if (args.length > 3) {
            cli.printErr("too many arguments\n");
            return;
        }

        String userName = args[1];
        String domainName = args[2];

        cli.print(String.format("-> /add [%s, %s]", userName, domainName));
        int status = stub.addUserDomain(userName, domainName);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Failed to add user to domain!");
        }
    }

    private static void rdCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <domain name>\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }

        String device = args[1];
        cli.print(String.format("-> /register device %s", device));
        int status = stub.registerDevice(device);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Failed to register device!");
        }
    } 

    private static void etCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <temperature> (in float)\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }

        float temp = Float.parseFloat(args[1]);
        cli.print(String.format("-> /temperature %s", temp));
        int status = stub.sendTemp(temp);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Failed to send the temperature!");
        }

    }

    private static void eiCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <file.jpg>\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }

        String filename = args[1];
        byte[] img = IoTPersistance.read(filename);
        if (img == null) {
            cli.printErr("Error: File not found.");
            return;
        }

        cli.print(String.format("-> /image %s", filename));
        int status = stub.sendImage(img);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Failed to send the image!");
        }
    }

    private static void rtCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <domain name>\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }

        String targetDomainName = args[1];
        cli.print(String.format("-> /retrieveTemp %s", targetDomainName));
        float[] status = stub.getTemp(targetDomainName);
        if (status == null) {
            cli.printErr("Failed to receive the latest remperature measurements!");
        }
        cli.print(String.format("<- %s", status.toString()));
    }

    private static void riCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <user id> and <device id>\n");
            return;
        }

        if (args.length == 2) {
            cli.printErr("missing arguments, we´re expecting 2 arguments: <user id> and <device id>\n");
            return;
        }

        if (args.length > 3) {
            cli.printErr("too many arguments\n");
            return;
        }

        String userId = args[1];
        int devId = Integer.parseInt(args[2]);
        String user = userId + ":" + args[2];
        cli.print(String.format("-> /receive image %s", user));
        byte[] status = stub.getUserImage(userId, devId);
        if (status == null) {
            cli.printErr("Failed to receive the image!");
        }
        cli.print(String.format("<- %s", status.toString()));
    }

    private static void exitCommand() {
        stub.terminateProgram();
    }

    /**
     * Continuesly promps for command and
     * invokes respective method.
     */
    private static void userInvoke() {
        while (true) {
            show_menu();

            String[] tokens = cli.getUserInput().split(" ");
            switch (tokens[0]) {
                case "CREATE":
                    createCommand(tokens);
                    break;
                case "ADD":
                    addCommand(tokens);
                    break;
                case "RD":
                    rdCommand(tokens);
                    break;
                case "ET":
                    etCommand(tokens);
                    break;
                case "EI":
                    eiCommand(tokens);
                    break;
                case "RT":
                    rtCommand(tokens);
                    break;
                case "RJ":
                    riCommand(tokens);
                    break;
                case "EXIT":
                    exitCommand();
                    return;
                default:
                    cli.printErr("Unable to parse given command.");
                    break;
            }
        }
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

        if (stub == null) {
            cli.printErr("Error initializing device!");
            cli.close();
            return -1;
        }
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