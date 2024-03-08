package Client;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class IoTDevice{

    public static void main(String[] args) throws IOException {

        // Command line argument validation
        int args_verify = verifyCmdArgs(args);
        if (args_verify > 0) {
            System.out.println("Incorrect command line arguments!");
        }

        // Extract socket
        String[] socket = args[0].split(":", 2);
        String serverIp = socket[0];
        int serverPort = 0;
        if(socket.length == 1){
            // Use default port
            serverPort = 12345;
        } else {
            serverPort = Integer.parseInt(socket[1]);
        }

        // Extract devide id
        int devId = Integer.parseInt(args[1]);

        // Extract user id
        String userId = args[2];

        

        try {
            Socket clientSocket = new Socket(serverIp, serverPort);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter password");
            String password = sc.nextLine();
            String fromServer = (String) in.readObject();

            System.out.println("Mensagem do servidor: " + fromServer);

        } catch (UnknownHostException e) {
            //sexo anala é bom e eu gosto
        } catch (IOException e) {
         //hahahahaa
        } catch (ClassNotFoundException e) {

        }
    }

    /**
     * Verifies if the command line arguments complies with
     * the format <socket> <dev-id> <user-id>.
     * @param args
     *      Command line arguments
     * @return
     *      0 if all the inputs are correct;
     *      -1 if number of arguments is wrong;
     *      -2 if socket is different to <ip>:<port> or <ip>, only supporting IPv4;
     *      -3 if dev-id is not digit-only;
     *      -4 if user-id doesn't meet the expected format;
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
                Integer.parseInt(socket[1]);
            } catch (NumberFormatException e) {
                return -2;
            }
        }
        
        // Verify <dev-id>
        try {
            Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return -3;
        }

        // Verify <user-id>
        // TODO not used for now
        return 0;
    }
}