package client;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

import utils.IoTCLI;


public class IoTDevice {
    private static final String USAGE = "USAGE: IoTDevice <serverAddress> <dev-id> <user-id>";
    private static final int DEFAULT_SERVERPORT = 12345;


    // Simple UI
    private static IoTCLI cli;

    private static IoTClientStub stub;

    private static IoTClientHandler handler;

    private static int devId;
    private static String userId;
   
    
    //NAO TIREM A MAIN DAQUI, FIQUEI 5min A PROCURA DELA
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
        if (initialize(args) < 0) {
            cli.printErr("Failed initializing device!");
            close();
        }
        
        // perform user auth
        if (performUserAuth() < 0) {
            cli.printErr("Failed authenticating user!");
            close();
        }
        
        // perform device auth
        if (performDeviceAuth(true) < 0) {
            cli.printErr("Failed authenticating device!");
            close();
        }
        
        // perform program auth
        if (performProgramAuth() < 0) {
            cli.printErr("Failed authenticating program!");
            close();
        }
        
        handler.userInvoke();
        
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
            return -1;
        }
        
        // Initialize handler
        handler = IoTClientHandler.getInstance(cli, stub);
        
        if (handler == null) {
            return -1;
        }
        return 0;
    }
    
    /**
     * Gets the name of the jar file and it's size
     * and authenticates with the server.
     * @see
     *      Untested!
     * @return
     *      0 if authenticated successfully;
     *      -1 if authentication failed;
     */
    private static int performProgramAuth() {
        URL res = IoTDevice.class.getProtectionDomain().getCodeSource().getLocation();
        File file = null;
        try {
            file = new File(res.toURI());
        } catch (URISyntaxException e) {
            return -1;
        }
        long size = file.length();
        String name = file.getName();

        cli.print(String.format("-> /authProg [%s, %d]", name, size));
        int status = stub.authenticateProgram(name, size);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Error authenticating program!");
        }
        return status;
    }
    
    /**
     * Authenticates user with ther server.
     * If the password is incorrect will continue to 
     * prompt until the authentication finishes.
     * @return <ul>
     *      <li> 0 if authenticated successfully;
     *      <li> -1 if authentication failed;
     */
    private static int performUserAuth() {
        boolean authenticated = false;
        while (!authenticated) {
            // retrieve pass
            cli.print("Please introduce password: ");
            String pwd = cli.getUserInput();
            if (pwd == null)
                return -1;
        
            cli.print(String.format("-> /authUser %s : %s", userId, pwd));
            int status = stub.authenticateUser(userId, pwd);
            cli.print(String.format("<- %d", status));
            switch (status) {
                case 1:
                    authenticated = true;
                    cli.print("New user created.");
                    continue;

                case 0:
                    authenticated = true;
                    cli.print("User authentication successful.");
                    continue;

                case -1:
                    cli.printErr("User authentication failed!");
                    break;
                
                case -2:
                    cli.printErr("Network error!");
                    return -1;
            
                default:
                    break;
            }
            
        }
        return 0;
    }
    
    private static int performDeviceAuth(boolean askNewDevId) {
        boolean authenticated = false;
        while (!authenticated) {
            cli.print(String.format("-> /authDev %s : %s", userId, devId));
            int status = stub.authenticateDevice(devId);
            cli.print(String.format("<- %d", status));
            if (status == 0) {
                authenticated = true;
                break;
            }
            switch (status) {
                case -1:
                    cli.printErr("Failed to valide device id!");
                    break;
                case -2:
                    cli.printErr("Network error!");
                    break;
                default:
                    break;
            }
            if (askNewDevId && status == -1) {
                cli.print("Please introduce a different devId: ");
                String devIdStr = cli.getUserInput();
                if (devIdStr == null) {
                    return -1;
                }
                try {
                    devId = Integer.parseInt(devIdStr);
                } catch (NumberFormatException e) {
                    cli.printErr("DevId should only contain digits!");
                }
            } else {
                return -1;
            }
        }

        return 0;
    }
    


    /**
     * Releases resources, closes connection and 
     * terminates program.
     */
    public static void close() {
        if (handler != null)
            handler.close();
        else {
            if (stub != null)
                stub.close();
            if (cli != null)
                cli.close();
        }
        System.exit(0);
    }
}