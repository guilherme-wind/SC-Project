package src.client;

import java.io.*;
import java.util.NoSuchElementException;

public class IoTDevice {
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

        System.out.println(String.format("-> /authUser %s : %s", userId, pwd));
        int status = stub.authenticateUser(userId, pwd);
        System.out.println(String.format("<- %d", status));
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

        System.out.println(String.format("-> /authDev %s : %s", userId, devId));
        int status = stub.authenticateDevice(devId);
        System.out.println(String.format("<- %d", status));
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
        System.out.println(String.format("-> /authProg [%s, %d]", PROGRAM_NAME, PROGRAM_SIZE));
        int status = stub.authenticateProgram(PROGRAM_NAME, PROGRAM_SIZE);
        System.out.println(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Error authenticating program!");
        }
        return status;
    }

    /**
     * Continuesly promps for command and
     * invokes respective method.
     */
    private static void userInvoke() {
        try {
            while (true) {
                System.out.println("Menu de Comandos:");
                System.out.println("  CREATE <dm>");
                System.out.println("  ADD <user> <dm>");
                System.out.println("  RD <dm>");
                System.out.println("  ET <float>");
                System.out.println("  EI <filename.jpg>");
                System.out.println("  RT <dm>");
                System.out.println("  RI <user-id>:<dev_id>");
                System.out.println("  Exit");
                System.out.println("Digite um comando:\n");

                String userInput = cli.getUserInput();

                if (userInput.isEmpty()) {
                    System.out.println("No command provided. Please enter a valid command");
                    continue;
                }

                String[] tokens = userInput.split(" ");

                if (tokens[0].compareToIgnoreCase("CREATE") == 0) {
                    if (tokens.length == 1) {
                        cli.printErr("missing <domain name>\n");
                        continue;
                    }

                    if (tokens.length > 2) {
                        cli.printErr("too many arguments\n");
                        continue;
                    }

                    // Lógica para o comando CREATE

                } else if (tokens[0].compareToIgnoreCase("ADD") == 0) {
                    if (tokens.length == 1) {
                        cli.printErr("missing <user> and <domain name>\n");
                        continue;
                    }

                    if (tokens.length == 2) {
                        cli.printErr("missing arguments, we´re expecting 2 arguments: <user> and <domain name>\n");
                        continue;
                    }

                    if (tokens.length > 3) {
                        cli.printErr("too many arguments\n");
                        continue;
                    }

                    // Lógica para o comando ADD

                } else if (tokens[0].compareToIgnoreCase("RD") == 0) {
                    if (tokens.length == 1) {
                        cli.printErr("missing <domain name>\n");
                        continue;
                    }

                    if (tokens.length > 2) {
                        cli.printErr("too many arguments\n");
                        continue;
                    }

                    // Lógica para o comando RD

                } else if (tokens[0].compareToIgnoreCase("ET") == 0) {
                    if (tokens.length == 1) {
                        cli.printErr("missing <temperature> (in float)\n");
                        continue;
                    }

                    if (tokens.length > 2) {
                        cli.printErr("too many arguments\n");
                        continue;
                    }

                    // Lógica para o comando ET

                } else if (tokens[0].compareToIgnoreCase("EI") == 0) {
                    if (tokens.length == 1) {
                        cli.printErr("missing <file.jpg>\n");
                        continue;
                    }

                    if (tokens.length > 2) {
                        cli.printErr("too many arguments\n");
                        continue;
                    }

                    // Lógica para o comando EI

                } else if (tokens[0].compareToIgnoreCase("RT") == 0) {
                    if (tokens.length == 1) {
                        cli.printErr("missing <domain name>\n");
                        continue;
                    }

                    if (tokens.length > 2) {
                        cli.printErr("too many arguments\n");
                        continue;
                    }

                    // Lógica para o comando RT

                } else if (tokens[0].compareToIgnoreCase("RI") == 0) {
                    if (tokens.length == 1) {
                        cli.printErr("missing <user id> and <device id>\n");
                        continue;
                    }

                    if (tokens.length == 2) {
                        cli.printErr("missing arguments, we´re expecting 2 arguments: <user id> and <device id>\n");
                        continue;
                    }

                    if (tokens.length > 3) {
                        cli.printErr("too many arguments\n");
                        continue;
                    }

                    // Lógica para o comando RI
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Exit");
        }
    }


    public static void main(String[] args) throws IOException {

        // Initialize cli
        cli = IoTCLI.getInstance();

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