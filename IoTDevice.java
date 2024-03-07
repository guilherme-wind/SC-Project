import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;

public class IoTDevice{

    public static void main(String[] args){

        String[] auxAddress = args[0].split(":");
        String serverIp = auxAddress[0];

        int devId = Integer.parseInt(args[1]);
        String userId = args [2];

        int serverPort = 0;
        
        if(auxAddress.length == 1){
            serverPort = 12345;
        } else {
            serverPort = Integer.parseInt(auxAddress[1]);
        }

        Socket clientSocket = new Socket(serverIp, serverPort);
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter password");
            String password = sc.nextLine();
            String fromServer = (String) in.readObject();

            System.out.println("Mensagem do servidor: " + fromServer);

        } catch (UnknownHostException e) {
            
        } catch (IOException e) {
        
        } catch (ClassNotFoundException e) {

        }
    }
}