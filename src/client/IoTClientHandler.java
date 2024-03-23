package client;

import utils.IoTCLI;

/**
 * Interacts with user and deals with
 * input invoke.
 */
public class IoTClientHandler {

    private static String[] ILLEGAL_CHARS = new String[]{"*","<",">",":","/","\\","|","?"};

    // Singleton
    private static IoTClientHandler instance;

    private IoTCLI cli;
    private IoTClientStub stub;
    
    private IoTClientHandler(IoTCLI cli, IoTClientStub stub) {
        this.cli = cli;
        this.stub = stub;
    }
    
    /**
     * Returns an instance of the client handler or null if 
     * the arguments are invalid.
     * @param cli
     *      Command line interface.
     * @param stub
     *      Requests handler.
     * @return
     *      An instance of the handler or null if the 
     *      arguments are invalid.
     */
    protected static synchronized IoTClientHandler getInstance(IoTCLI cli, IoTClientStub stub) {
        if (cli == null || stub == null)
            return null;

        if (instance == null)
            instance = new IoTClientHandler(cli, stub);
        return instance;
    }

    /**
     * Continuesly promps for command and
     * invokes respective method.
     * @return
     *      0 if user requested to exit;
     */
    protected int userInvoke() {
        while (true) {
            cli.printMenu();

            cli.printShell();
            String input = cli.getUserInput();
            if (input == null)
                return 0;

            String[] tokens = input.split(" ");
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
                case "RI":
                    riCommand(tokens);
                    break;
                case "EXIT":
                    return 0;
                default:
                    cli.printErr("Unable to parse given command.");
                    break;
            }
        }
    }

    /**
     * Creates a new domain
     * @param args
     */
    private void createCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <domain name>\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }

        String domainName = args[1];
        
        for (String illegal_chaString : ILLEGAL_CHARS) {
            if (domainName.contains(illegal_chaString)) {
                cli.printErr(String.format("domain name can't contain %s\n", illegal_chaString));
                return;
            }
        }

        cli.print(String.format("-> /create %s", domainName));
        int status = stub.createDomain(domainName);
        cli.print(String.format("<- %d", status));
        switch (status) {
            case 0:
                cli.printSuc("Domain created successfully!");
                break;
            case -1:
                cli.printErr("Domain already exists!");
                break;
            case -2:
                cli.printErr("Network error!");
        
            default:
                break;
        }
    }

    /**
     * Adds an user to a domain
     * @param args
     */
    private void addCommand(String[] args) {
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
        switch (status) {
            case 1:
                cli.printInfo("This user is already in the domain.");
            case 0:
                cli.printSuc("User added successfully!");
                break;
            case -1:
                cli.printErr("User doesn't exist!");
                break;
            case -2:
                cli.printErr("Domain doesn't exist!");
                break;
            case -3:
                cli.printErr("No permissions to register devices in this domain!");
                break;
            case -4:
                cli.printErr("Network error!");
        
            default:
                break;
        }
    }

    /**
     * Registers the current device in a domain
     * @param args
     */
    private void rdCommand(String[] args) {
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
        switch (status) {
            case 1:
                cli.printInfo("This device is already registered in the domain.");
            case 0:
                cli.printSuc("Device registered successfully!");
                break;
            case -1:
                cli.printErr("Domain doesn't exist!");
                break;
            case -2:
                cli.printErr("No permissions to register devices in this domain!");
                break;
            case -3:
                cli.printErr("Network error!");
        
            default:
                break;
        }
    }

    /**
     * Sends a temperature reading to the server
     * @param args
     */
    private void etCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <temperature> (in float)\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }
        
        float temp = 0;
        try {
            temp = Float.parseFloat(args[1].replace(",", "."));
        } catch (NumberFormatException e) {
            cli.printErr("Wrong number format!");
            return;
        }
        cli.print(String.format("-> /temperature %s", temp));
        int status = stub.sendTemp(temp);
        cli.print(String.format("<- %d", status));
        switch (status) {
            case 0:
                cli.printSuc("Sent the temperature measurement successfully!");
                break;
            case -1:
                cli.printErr("Network error!");
                break;
        
            default:
                break;
        }

    }

    /**
     * Sends an image to the server
     * @param args
     */
    private void eiCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <file.jpg>\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }

        cli.print(String.format("-> /image %s", args[1]));
        int status = stub.sendImage(args[1]);
        cli.print(String.format("<- %d", status));
        switch (status) {
            case 0:
                cli.printSuc("Sent the image successfully!");
                break;
            case -1:
                cli.printErr("Failed to send the image!");
                break;
            case -2:
                cli.printErr("Network error!");
                break;
        
            default:
                break;
        }
    }

    /**
     * Receive temperature readings from devices in the domain
     * @param args
     */
    private void rtCommand(String[] args) {
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
        int status = stub.getTemperaturesInDomain(targetDomainName);
        cli.print(String.format("<- %d", status));
        switch (status) {
            case 0:
                cli.printSuc("Received the latest temperature measurements successfully!");
                break;
            case -1:
                cli.printErr("No permissions!");
                break;
            case -2:
                cli.printErr("The domain doesn't exist!");
                break;
            case -3:
                cli.printErr("Network error!");
                break;
        
            default:
                break;
        }
    }

    /**
     * Receive image from a device of a user
     * @param args
     */
    private void riCommand(String[] args) {
        if (args.length == 1) {
            cli.printErr("missing <user id> and <device id>\n");
            return;
        }

        if (args.length > 2) {
            cli.printErr("too many arguments\n");
            return;
        }

        String[] tokens = args[1].split(":", 2);
        if (tokens.length < 2) {
            cli.printErr("missing arguments, we're expecting: <user id>:<device id>\n");
            return;
        }

        String userId = tokens[0];
        int devId = Integer.parseInt(tokens[1]);
        String user = userId + ":" + tokens[1];
        cli.print(String.format("-> /receive image %s", user));
        int status = stub.getUserImage(userId, devId);
        cli.print(String.format("<- %d", status));
        switch (status) {
            case 1:
                cli.printErr("The device hasn't uploaded any image yet!");
                break;
            case 0:
                cli.printSuc("Received the image successfully!");
                break;
            case -1:
                cli.printErr("No permissions!");
                break;
            case -2:
                cli.printErr("The user doesn't exist!");
                break;
            case -3:
                cli.printErr("The device doesn't exist!");
                break;
            case -4:
                cli.printErr("Network error!");
        
            default:
                break;
        }
    }

    /**
     * Closes the handler, terminates
     * the CLI and the stub.
     */
    protected void close() {
        if (cli != null)
            cli.close();

        if (stub != null)
            stub.close();
    }
}
