package server;

import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

public class IoTServer{

    // Store running threads
    private static List<IoTServerThread> threads;
    private static ServerSocket socket;
    public static void main(String[] args) {
        System.out.println("Starting server...");
        threads = new LinkedList<IoTServerThread>();
        Thread main = Thread.currentThread();

        // Associates shutdown signal with it's handler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { shutdown(main); }));

        try {
            socket = new ServerSocket(12345);
            while (true) {
                System.out.println("Waiting for new connection...");
                IoTServerThread thread = new IoTServerThread(socket.accept());
                System.out.println("New connection!");
                threads.add(thread);
                thread.start();
            }
        } catch (Exception e) {
            try {
                socket.close();
            } catch (Exception e1){

            }
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Closes all threads and exit.
     */
    private static void shutdown(Thread main) {
        for (IoTServerThread ioTServerThread : threads) {
            ioTServerThread.interrupt();
        }
        main.interrupt();
        System.out.println("Went to mewing, byebye!");
    }
}