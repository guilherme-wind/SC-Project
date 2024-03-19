package client;

import java.nio.charset.StandardCharsets;

import utils.IoTPersistance;

/**
 * Interacts with user and deals with
 * input invoke.
 */
public class IoTClientHandler {

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
    protected static IoTClientHandler getInstance(IoTCLI cli, IoTClientStub stub) {
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

        cli.print(String.format("-> /create %s", domainName));
        int status = stub.createDomain(domainName);
        cli.print(String.format("<- %d", status));
        if (status < 0) {
            cli.printErr("Failed to create domain!");
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
        if (status < 0) {
            cli.printErr("Failed to add user to domain!");
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
        if (status < 0) {
            cli.printErr("Failed to register device!");
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
        if (status < 0) {
            cli.printErr("Failed to send the temperature!");
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
        byte[] data = stub.getTemperaturesInDomain(targetDomainName);
        if (data == null) {
            cli.printErr("Failed to receive the latest remperature measurements!");
            return;
        }
        String dataStr = new String(data, StandardCharsets.UTF_8);
        cli.print(String.format("<- %s", dataStr));
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

        if (args.length == 2) {
            cli.printErr("missing arguments, we're expecting 2 arguments: <user id> and <device id>\n");
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
