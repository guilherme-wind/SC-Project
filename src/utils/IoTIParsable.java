package utils;

import java.util.ArrayList;
import java.util.List;

public interface IoTIParsable {
    
    /**
     * Gets a string representation of the object.
     * @return
     *      String representation of the object.
     */
    public String parseToSerial();

    /**
     * Parses a serial representation into an object.
     * @param serial
     *      The serial representation to parse.
     * @return
     *      An instance of the implementing class.
     */
    public static IoTIParsable parseFromSerial(String serial) {
        // Implementation specific to each class implementing IoTIParsable
        return null;
    }

    /**
     * Separate a String by comma, ignoring all strings
     * inside quotes "", curly brackets {}, square brackets [],
     * and normal brackets ().
     * @param input
     *      String to be devided.
     * @return
     *      List of strings separated by commas.
     */
    public static List<String> separateStrByChar(char delimiter, String input) {
        List<String> result = new ArrayList<>();
        int i = 0;
        StringBuilder token = new StringBuilder();
        // If the current pointer is between quotes
        boolean inQuotes = false;
        // How many closing brackets need
        int bracketCount = 0;
        int squareBracketCount = 0;
        int curlyBracketCount = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            if (c == '"' && squareBracketCount == 0 && curlyBracketCount == 0) {
                inQuotes = !inQuotes;
                token.append(c);
            } else if (c == '(' && !inQuotes && squareBracketCount == 0 && curlyBracketCount == 0) {
                bracketCount++;
                token.append(c);
            } else if (c == ')' && !inQuotes && squareBracketCount == 0 && curlyBracketCount == 0) {
                bracketCount--;
                token.append(c);
            } else if (c == '[' && !inQuotes && bracketCount == 0 && curlyBracketCount == 0) {
                squareBracketCount++;
                token.append(c);
            } else if (c == ']' && !inQuotes && bracketCount == 0 && curlyBracketCount == 0) {
                squareBracketCount--;
                token.append(c);
            } else if (c == '{' && !inQuotes && bracketCount == 0 && squareBracketCount == 0) {
                curlyBracketCount++;
                token.append(c);
            } else if (c == '}' && !inQuotes && bracketCount == 0 && squareBracketCount == 0) {
                curlyBracketCount--;
                token.append(c);
            } else if (c == ',' && bracketCount == 0 && squareBracketCount == 0 && curlyBracketCount == 0 && !inQuotes) {
                result.add(token.toString().trim());
                token.setLength(0);
            } else {
                token.append(c);
            }

            i++;
        }

        if (token.length() > 0) {
            result.add(token.toString().trim());
        }

        return result;
    }

    
}
