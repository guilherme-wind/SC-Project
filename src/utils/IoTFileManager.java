package src.utils;

import java.io.File;
import java.io.IOException;
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
     *      Path to the file.
     * @param users
     *      Map to where data will be loaded.
     * @return
     *      0 if loaded successfully;
     *      -1 if file doesn't exist;
     *      -2 if no permissions to access file;
     *      -3 if arguments are invalid;
     */
    public synchronized int loadUsersFromText(String filePath, Map<String, User> users) {
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
                String line = fileScanner.nextLine();
                User user = User.parseFromSerial(line);
                if (user == null)
                    continue;
                users.put(user.getName(), user);
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
     * This method will erase the previous content in the file, to keep
     * the content use {@link IoTFileManager#addUserToText()}.
     * @param filePath
     *      Path to the text file.
     * @param users
     * @return
     *      0 if wrote sucessfully;
     *      -1 if a directory with the same name already exists;
     *      -2 if no permissions to write in the path;
     *      -3 if arguments are invalid;
     */
    public synchronized int writeUsersToText(String filePath, Map<String, User> users) {
        if (filePath == null || users == null)
            return -3;

        File file = new File(filePath);
        if (file.isDirectory())
            return -1;
        
        // Create file
        if (!file.exists()) {
            try {
                boolean res = file.createNewFile();
                if (!res) {
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }

        // TODO Not finished!

        return 0;
    }

    public int addUserToText(String filePath, Map<String, User> users) {
        // TODO
        return 0;
    }
}
