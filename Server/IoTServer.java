package Server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class IoTServer{
    private static Map<String, String> usersMap = new HashMap<>();


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket srvSocket = new ServerSocket(12345);
        System.out.println("Server started. Waiting for clients...");

        while(true){
            Socket cliSocket = srvSocket.accept();
            System.out.println("Client connected: " + cliSocket);
            Threads thread = new Threads(cliSocket);

            }
        }

    }

     class Threads extends Thread {
        private Socket cliSocket;

        Threads(Socket cliSocket){
            this.cliSocket = cliSocket;
        }


        @Override
        public void run() {
            try {
                ObjectOutputStream out = new ObjectOutputStream(cliSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(cliSocket.getInputStream());

                String username = (String) in.readObject();
                String password = (String) in.readObject();
                if(authenticateUser(username, password)) {

                }
            } catch (IOException | ClassNotFoundException e){
                System.err.println("Erro");
            }
        }

    private static boolean authenticateUser(String username, String password) {
        return false;
    }
}