package utils;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;


public class ConsoleColors {
    /**
     * Code from StackOverflow, too lasy to add them one by one manually.
     * Source: @see <a href="https://stackoverflow.com/a/45444716">Original post</a>
     */
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Cursor formats
    public static final String SLOW_BLINK = "\033[5m";   // SLOW BLINK
    public static final String FAST_BLINK = "\033[6m";   // FAST BLINK
    public static final String NO_BLINK = "\033[25m";   // NO BLINK

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


    public static class Service {
        private static Service instance;
        private final Map<String, List<String>> availableColors;
    
        private Service() {
            availableColors = new HashMap<>();
            initializeAvailableColors();
        }
    
        public static synchronized Service getInstance() {
            if (instance == null) {
                instance = new Service();
            }
            return instance;
        }
    
        private void initializeAvailableColors() {
            availableColors.put("Regular", Arrays.asList(
                    ConsoleColors.BLACK, ConsoleColors.RED, ConsoleColors.GREEN,
                    ConsoleColors.YELLOW, ConsoleColors.BLUE, ConsoleColors.PURPLE,
                    ConsoleColors.CYAN, ConsoleColors.WHITE
            ));
            availableColors.put("Bold", Arrays.asList(
                    ConsoleColors.BLACK_BOLD, ConsoleColors.RED_BOLD, ConsoleColors.GREEN_BOLD,
                    ConsoleColors.YELLOW_BOLD, ConsoleColors.BLUE_BOLD, ConsoleColors.PURPLE_BOLD,
                    ConsoleColors.CYAN_BOLD, ConsoleColors.WHITE_BOLD
            ));
            availableColors.put("Underline", Arrays.asList(
                    ConsoleColors.BLACK_UNDERLINED, ConsoleColors.RED_UNDERLINED, ConsoleColors.GREEN_UNDERLINED,
                    ConsoleColors.YELLOW_UNDERLINED, ConsoleColors.BLUE_UNDERLINED, ConsoleColors.PURPLE_UNDERLINED,
                    ConsoleColors.CYAN_UNDERLINED, ConsoleColors.WHITE_UNDERLINED
            ));
            availableColors.put("Background", Arrays.asList(
                    ConsoleColors.BLACK_BACKGROUND, ConsoleColors.RED_BACKGROUND, ConsoleColors.GREEN_BACKGROUND,
                    ConsoleColors.YELLOW_BACKGROUND, ConsoleColors.BLUE_BACKGROUND, ConsoleColors.PURPLE_BACKGROUND,
                    ConsoleColors.CYAN_BACKGROUND, ConsoleColors.WHITE_BACKGROUND
            ));
            availableColors.put("High Intensity", Arrays.asList(
                    ConsoleColors.BLACK_BRIGHT, ConsoleColors.RED_BRIGHT, ConsoleColors.GREEN_BRIGHT,
                    ConsoleColors.YELLOW_BRIGHT, ConsoleColors.BLUE_BRIGHT, ConsoleColors.PURPLE_BRIGHT,
                    ConsoleColors.CYAN_BRIGHT, ConsoleColors.WHITE_BRIGHT
            ));
            availableColors.put("Bold High Intensity", Arrays.asList(
                    ConsoleColors.BLACK_BOLD_BRIGHT, ConsoleColors.RED_BOLD_BRIGHT, ConsoleColors.GREEN_BOLD_BRIGHT,
                    ConsoleColors.YELLOW_BOLD_BRIGHT, ConsoleColors.BLUE_BOLD_BRIGHT, ConsoleColors.PURPLE_BOLD_BRIGHT,
                    ConsoleColors.CYAN_BOLD_BRIGHT, ConsoleColors.WHITE_BOLD_BRIGHT
            ));
            availableColors.put("High Intensity Background", Arrays.asList(
                    ConsoleColors.BLACK_BACKGROUND_BRIGHT, ConsoleColors.RED_BACKGROUND_BRIGHT, ConsoleColors.GREEN_BACKGROUND_BRIGHT,
                    ConsoleColors.YELLOW_BACKGROUND_BRIGHT, ConsoleColors.BLUE_BACKGROUND_BRIGHT, ConsoleColors.PURPLE_BACKGROUND_BRIGHT,
                    ConsoleColors.CYAN_BACKGROUND_BRIGHT, ConsoleColors.WHITE_BACKGROUND_BRIGHT
            ));
        }

        public String getRandomUnusedColor(String category) {
            List<String> colors = availableColors.get(category);
            if (colors != null && !colors.isEmpty()) {
                Random random = new Random();
                int index = random.nextInt(colors.size());
                return colors.get(index);
            }
            return ConsoleColors.RESET; // No available colors in the given category
        }
    }
    
}


