package server;

import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

import utils.IoTCLI;

public class IoTServer{
    private static final String USAGE = "USAGE: IoTServer <port> (optional)";
    private static final int DEFAULT_SERVER_SOCKET = 12345;

    private static IoTCLI cli;

    // Store running threads
    private static List<IoTServerThread> threads;
    private static Thread main;
    private static ServerSocket socket;
    public static void main(String[] args) {
        // Command line argument validation
        if (verifyCmdArgs(args) < 0) {
            cli.printErr("Wrong input arguments!");
            cli.print(USAGE);
            return;
        }

        // Initialize class fields using command line arguments
        if (initialize(args) < 0) {
            cli.printErr("Failed initializing server!");
            return;
        }

        try {
            while (true) {
                System.out.println("Waiting for new connection...");
                IoTServerThread thread = new IoTServerThread(socket.accept());
                System.out.println("New connection!");
                threads.add(thread);
                thread.start();
            }
        } catch (Exception e) {
            cli.print("Failed to accept incoming connection. Shutting server down...");
            e.printStackTrace();
            shutdown();
            System.exit(0);
        }
    }


    /**
     * Verifies if the command line arguments complies with
     * the format <port> (optional).
     * @param args
     *      Command line arguments.
     * @return
     *      0 if all the inputs are correct;
     *      -1 if listening port isn't a valid value, i.e., [0, 65535].
     */
    private static int verifyCmdArgs(String[] args) {
        if (args.length <= 0)
            return 0;
        
        try {
            int port = Integer.parseInt(args[0]);
            if (port < 0 || port > 65535)
                return -1;
        } catch (NumberFormatException e) {
            return -1;
        }

        return 0;
    }

    /**
     * Initializes the class fields using the command line arguments.
     * @param args
     *      Command line arguments.
     * @return
     *      0 if initialization finished correctly;
     *      -1 if error while initializing;
     */
    private static int initialize(String[] args) {
        try {
            // Initialize cli
            cli = IoTCLI.getInstance();
            cli.printLog("Starting server...");

            // Associates shutdown signal with it's handler
            main = Thread.currentThread();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> { shutdown(); }));
            threads = new LinkedList<IoTServerThread>();

            if (args.length <= 0)
                socket = new ServerSocket(DEFAULT_SERVER_SOCKET);
            else 
                // Creates server socket
                socket = new ServerSocket(
                    Integer.parseInt(args[0])
                );
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * Closes all threads and exit.
     */
    private static void shutdown() {
        for (IoTServerThread ioTServerThread : threads) {
            ioTServerThread.interrupt();
        }

        try {
            socket.close();
        } catch (Exception e1){

        }
        main.interrupt();
        
        System.out.println("Went to mewing, byebye!");
    }
}