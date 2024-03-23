package utils;

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
                                        "\tEXIT";
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
     *      User input string or null if interruption
     *      was caught.
     */
    public String getUserInput() {
        String input;
        try {
            input = sc.nextLine();
        } catch (Exception e) {
            return null;
        }
        return input;
    }

    public void printMenu() {
        System.out.println(MENU);
    }

    public void print(String msg) {
        System.out.println(msg);
    }

    public void printSuc(String sucmsg) {
        
    }

    public void printErr(String errmsg) {
        System.out.println(ConsoleColors.RED_BOLD + "Error: " + ConsoleColors.RESET + errmsg);
    }

    public void printLog(String logmsg) {
        System.out.println("Log: " + logmsg);
    }

    public void printWebOutput(String msg) {
        System.out.println("->" + msg);
    }

    public void printWebInput(String msg) {
        System.out.println("<-" + msg);
    }

    /**
     * Closes the CLI
     */
    public void close() {
        sc.close();
    }

    /**
     * Code from StackOverflow, too lasy to add them one by one manually.
     * Source: @see <a href="https://stackoverflow.com/a/45444716">Original post</a>
     */
    public class ConsoleColors {
        // Reset
        public static final String RESET = "\033[0m";  // Text Reset
    
        // Regular Colors
        public static final String BLACK = "\033[0;30m";   // BLACK
        public static final String RED = "\033[0;31m";     // RED
        public static final String GREEN = "\033[0;32m";   // GREEN
        public static final String YELLOW = "\033[0;33m";  // YELLOW
        public static final String BLUE = "\033[0;34m";    // BLUE
        public static final String PURPLE = "\033[0;35m";  // PURPLE
        public static final String CYAN = "\033[0;36m";    // CYAN
        public static final String WHITE = "\033[0;37m";   // WHITE
    
        // Bold
        public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
        public static final String RED_BOLD = "\033[1;31m";    // RED
        public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
        public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
        public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
        public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
        public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
        public static final String WHITE_BOLD = "\033[1;37m";  // WHITE
    
        // Underline
        public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
        public static final String RED_UNDERLINED = "\033[4;31m";    // RED
        public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
        public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
        public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
        public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
        public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
        public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE
    
        // Background
        public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
        public static final String RED_BACKGROUND = "\033[41m";    // RED
        public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
        public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
        public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
        public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
        public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
        public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE
    
        // High Intensity
        public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
        public static final String RED_BRIGHT = "\033[0;91m";    // RED
        public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
        public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
        public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
        public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
        public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
        public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE
    
        // Bold High Intensity
        public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
        public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
        public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
        public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
        public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
        public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
        public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
        public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE
    
        // High Intensity backgrounds
        public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
        public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
        public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
        public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
        public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
        public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
        public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
        public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE
    }

}