package src.server;

import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

public class IoTServer{

    // Store running threads
    private static List<IoTServerThread> threads;
    public static void main(String[] args) {
        System.out.println("Starting server...");
        threads = new LinkedList<IoTServerThread>();

        // Associates shutdown signal with it's handler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { shutdown(); }));

        try (ServerSocket socket = new ServerSocket(12345)) {
            while (true) {
                System.out.println("Waiting for new connection...");
                IoTServerThread thread = new IoTServerThread(socket.accept());
                threads.add(thread);
                thread.start();
                // TODO store running threads in a pool, so we can exit gracefully
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes all threads and exit.
     */
    private static void shutdown() {
        for (IoTServerThread ioTServerThread : threads) {
            ioTServerThread.interrupt();
        }
        System.exit(0);
    }
}