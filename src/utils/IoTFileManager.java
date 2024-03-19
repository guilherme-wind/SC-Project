package src.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import src.server.model.Device;
import src.server.model.Domain;
import src.server.model.User;

/**
 * Simplify the process to register and load data from
 * persistent storage.
 */
public class IoTFileManager {

    private static IoTFileManager instance = null;

    private IoTFileManager() {
    }

    public static IoTFileManager getInstance() {
        if (instance == null)
            instance = new IoTFileManager();
        return instance;
    }

    /**
     * Checks whether if a file exists and has permission
     * to read and write.
     * @param filePath
     *      Path to the file.
     * @return
     *      True if the file is available, false otherwise.
     */
    public static synchronized boolean isFileAvailable(String filePath) {
        if (filePath == null)
            return false;
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile())
            return false;

        if (!file.canRead() || !file.canWrite())
            return false;
        return true;
    }

    /**
     * Reads content from plain text file and loads users.
     * Text file must have format
     * username:password
     * Will ignore lines that failed to load.
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
    public static synchronized int loadUsersFromText(String filePath, Map<String, User> users) {
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
     * Reads content from plain text file and loads domains and it's associated
     * data.
     * Will ignore lines that failed to load.
     * @param filePath
     *      Path to the file.
     * @param domains
     *      Map to where the data will be loaded.
     * @return
     *      0 if loaded successfully;
     *      -1 if file doesn't exist;
     *      -2 if no permissions to access file;
     *      -3 if arguments are invalid;
     */
    public static synchronized int loadDomainsFromText(String filePath, Map<String, Domain> domains) {
        if (filePath == null || domains == null)
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
                Domain user = Domain.parseFromSerial(line);
                if (user == null)
                    continue;
                domains.put(user.getName(), user);
            }

            fileScanner.close();
        } catch (Exception e) {
            return -1;
        }
        
        return 0;
    }

    /**
     * Reads content from plain text file and loads devices and it's associated
     * data.
     * Will ignore lines that failed to load.
     * @param filePath
     *      Path to the file.
     * @param devices
     *      Map to where the data will be loaded.
     * @return
     *      0 if loaded successfully;
     *      -1 if file doesn't exist;
     *      -2 if no permissions to access file;
     *      -3 if arguments are invalid;
     */
    public static synchronized int loadDevicesFromText(String filePath, Map<String, Device> devices) {
        if (filePath == null || devices == null)
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
                Device user = Device.parseFromSerial(line);
                if (user == null)
                    continue;
                devices.put(user.getName(), user);
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
    public static synchronized int writeObjsToText(String filePath, List<IoTIParsable> objs) {
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
            for (IoTIParsable entry : objs) {
                bw.write(entry.parseToSerial());
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
    public static int addObjectToText(String filePath, IoTIParsable obj) {
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

    public int readImage(String filePath) {
        // TODO
        return 0;
    }

    /**
     * Writes an object in binary to a file, will create one 
     * in case the file doesn't exist, will overwrite the
     * content if the file with the same name already exists.
     * @param filePath
     *      Path to the file.
     * @param object
     *      Object to be written.
     * @return
     *      0 if wrote successfully;
     *      -1 if failed to write to file;
     */
    public static synchronized int writeObjectToFile(String filePath, Object object) {
        try {
            FileOutputStream file = new FileOutputStream(filePath);
            ObjectOutputStream writer = new ObjectOutputStream(file);
            writer.writeObject(object);
            writer.close();
            file.close();
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    /**
     * Reads an object in binary from a file.
     * @param filePath
     *      Path to the file.
     * @return
     *      Read object or nothing if error occured.
     */
    public static synchronized Optional<Object> readObjectFromFile(String filePath) {
        Object obj;
        try {
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream reader = new ObjectInputStream(file);
            obj = reader.readObject();
            reader.close();
            file.close();
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.of(obj);
    }
}
