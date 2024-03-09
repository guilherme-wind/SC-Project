package src.server;

import java.net.ServerSocket;

public class IoTServer{
    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(12345)) {
            while (true) {
                IoTServerThread thread = new IoTServerThread(socket.accept());
                thread.start();
                // TODO store running threads in a pool, so we can exit gracefully
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}