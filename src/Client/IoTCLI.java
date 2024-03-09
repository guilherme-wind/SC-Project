package src.Client;

import java.util.Scanner;

public class IoTCLI {


    // Singleton
    private static IoTCLI instance = null;

    private Scanner sc;
    
    private IoTCLI() {
        sc = new Scanner(System.in);
    }

    public static IoTCLI getInstance() {
        if (instance == null) {
            instance = new IoTCLI();
        }
        return instance;
    }

    /**
     * Returns the user input from command line
     * @return
     */
    public String getUserInput() {
        return sc.nextLine();
    }

    public void print(String msg) {
        System.out.println(msg);
    }

    public void printErr(String errmsg) {
        System.out.println("Error: " + errmsg);
    }

    /**
     * Closes the CLI
     */
    public void close() {
        sc.close();
    }

}
