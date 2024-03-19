package client;

import java.util.Scanner;

public class IoTCLI {

    private static final String LINE = System.getProperty("line.separator");
    private static final String MENU =  "Command menu:" + LINE +
                                        "\tCREATE <domain>" + LINE + 
                                        "\tADD <user> <domain>" + LINE + 
                                        "\tRD <domain>" + LINE +
                                        "\tET <float>" + LINE +
                                        "\tEI <image-path>" + LINE +
                                        "\tRT <domain>" + LINE +
                                        "\tRI <user>:<device>" + LINE +
                                        "\tQUIT";
    private static final String ERROR_TEMP = "[Error]";
    private static final String ERROR_MISSING_ARGS = "";


    // Singleton
    private static IoTCLI instance = null;

    private Scanner sc;
    
    private IoTCLI() {
        sc = new Scanner(System.in);
    }

    /**
     * Get an instance of the CLI
     * @return
     */
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

    public void printMenu() {
        System.out.println(MENU);
    }

    public void print(String msg) {
        System.out.println(msg);
    }

    public void printErr(String errmsg) {
        System.out.println("Error: " + errmsg);
    }

    public void printLog(String logmsg) {
        System.out.println("Log: " + logmsg);
    }

    /**
     * Closes the CLI
     */
    public void close() {
        sc.close();
    }

}
