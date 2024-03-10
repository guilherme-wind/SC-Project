package src.utils;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import src.server.model.User;

public class IoTFileManager {

    // Not used for now
    private Scanner sc;

    public IoTFileManager() {
    }

    /**
     * Reads content from plain text file and loads users.
     * Text file must have format
     * username:password
     * @param filePath
     * @param users
     *      Map to where data will be loaded.
     * @return
     *      0 if loaded successfully;
     *      -1 if file doesn't exist;
     *      -2 if no permissions to access file;
     *      -3 if arguments are invalid;
     */
    public int loadUsersFromText(String filePath, Map<String, User> users) {
        if (filePath == null || users == null)
            return -3;

        File file = new File(filePath);
        if (!file.exists() || file.isDirectory())
            return -1;
        if (!file.canRead())
            return -2;

        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String[] tokens = fileScanner.nextLine().split(":", 2);
                if (tokens.length < 2)
                    continue;
                
                User user = new User(tokens[0], tokens[1]);
                users.put(tokens[0], user);
            }

            fileScanner.close();
        } catch (Exception e) {
            return -1;
        }
        
        return 0;
    }

    /**
     * Writes map content to plain text file, in case file doesn't exist,
     * will create one.
     * @param filePath
     * @param users
     * @return
     *      0 if wrote sucessfully;
     *      -1 if no permissions to write in the path.
     */
    public int writeUsersToText(String filePath, Map<String, User> users) {
        return 0;
    }
}
