package src.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
     * the content use {@link IoTFileManager#addObjectToText}.
     * @param filePath
     *      Path to the text file.
     * @param objs
     *      Map of parsable objects.
     * @return
     *      0 if wrote sucessfully;
     *      -1 if a directory with the same name already exists;
     *      -2 if no permissions to write in the path;
     *      -3 if arguments are invalid;
     */
    public synchronized int writeObjsToText(String filePath, Map<String, IoTIParsable> objs) {
        if (filePath == null || objs == null)
            return -3;

        File file = new File(filePath);
        if (file.isDirectory())
            return -1;
        
        // Always tries to delete the file
        file.delete();
        
        // Creates new file
        try {
            if (!file.createNewFile()) {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        // Writes to file
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for (Map.Entry<String, IoTIParsable> entry : objs.entrySet()) {
                bw.write(entry.getValue().parseToSerial());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    /**
     * Appends an object representation to plain text file, will keep
     * the previous content of the file.
     * @param filePath
     *      Path to the text file.
     * @param obj
     *      Parsable object.
     * @return
     *      0 if completed successfully;
     *      -1 if the given path is a directory;
     *      -2 if IO error occured;
     *      -3 if arguments are invalid;
     */
    public int addObjectToText(String filePath, IoTIParsable obj) {
        if (filePath == null || obj == null)
            return -3;

        File file = new File(filePath);
        if (file.isDirectory())
            return -1;

        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(obj.parseToSerial());
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
        return 0;
    }
}
