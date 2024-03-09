package src.server;

import java.net.ServerSocket;

public class IoTServer{
    public static void main(String[] args) {
        System.out.println("Starting server...");
        try (ServerSocket socket = new ServerSocket(12345)) {
            while (true) {
                System.out.println("Waiting for new connection...");
                IoTServerThread thread = new IoTServerThread(socket.accept());
                thread.start();
                // TODO store running threads in a pool, so we can exit gracefully
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}