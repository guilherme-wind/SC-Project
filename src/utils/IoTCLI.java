package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class IoTCLI {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private static final String LINE = System.getProperty("line.separator");
    private static final String MENU =  "Command menu:" + LINE +
                                        "\tCREATE <domain>" + LINE + 
                                        "\tADD <user> <domain>" + LINE + 
                                        "\tRD <domain>" + LINE +
                                        "\tET <float>" + LINE +
                                        "\tEI <image-path>" + LINE +
                                        "\tRT <domain>" + LINE +
                                        "\tRI <user>:<device>" + LINE +
                                        "\tEXIT";
    private static final String LOG_TEMP = "[%s] %s";

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
     *      User input string or null if interruption
     *      was caught.
     */
    public String getUserInput() {
        String input;
        try {
            if (sc.hasNextLine()) {
                input = sc.nextLine();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return input;
    }
    

    /**
     * Prints the options menu.
     */
    public void printMenu() {
        System.out.println(MENU);
    }

    /**
     * Prints the given message to the console.
     * @param msg
     */
    public void print(String msg) {
        System.out.println(msg);
    }

    /**
     * Prints the given message to the console.
     * @param msg
     */
    public void print(String msg, String prefix, String color) {
        System.out.println(
            String.format(
                "[%s%s%s] %s", color, prefix, ConsoleColors.RESET, msg
            )
        );
    }

    /**
     * Prints the success message to the console.
     * @param sucmsg
     */
    public void printSuc(String sucmsg) {
        
    }

    /**
     * Prints the error message to the console.
     * @param errmsg
     */
    public void printErr(String errmsg) {
        System.out.println(ConsoleColors.RED_BOLD + "Error: " + ConsoleColors.RESET + errmsg);
    }

    /**
     * Prints the log message to the console.
     * @param logmsg
     */
    public void printLog(String logmsg) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(String.format(LOG_TEMP, dtf.format(now), logmsg));
    }

    /**
     * Prints the web output message.
     * @param msg
     */
    public void printWebOutput(String msg) {
        System.out.println("->" + msg);
    }

    /**
     * Prints the web input message.
     * @param msg
     */
    public void printWebInput(String msg) {
        System.out.println("<-" + msg);
    }

    /**
     * Closes the CLI
     */
    public void close() {
    }

}