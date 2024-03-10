package src.utils;

import java.util.Map;


import src.server.model.User;

public class IoTFileManager {
    public IoTFileManager() {
    }

    /**
     * Reads content from plain text file and loads users
     * @param filePath
     * @param users
     *      Map to where data will be loaded.
     * @return
     *      0 if loaded successfully;
     *      -1 if file doesn't exist;
     *      -2 if no permissions to access file;
     */
    public int loadUsersFromText(String filePath, Map<String, User> users) {
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
