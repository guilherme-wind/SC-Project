package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import server.model.Device;
import server.model.Domain;
import server.model.User;

/**
 * Simplify the process of register and load data from
 * persistent storage, allows writing objects dirctly
 * to files or parse to string represetation before 
 * writing to files.
 */
public class IoTFileManager {

    // Singleton
    private static IoTFileManager instance = null;

    private static final Path SERVER_ROOT = Paths.get(".", "server_files");

    private static final Path SERV_METADATA = Paths.get(SERVER_ROOT.toString(), "metadata");
    
    private static final String USER_TXT_DB = Paths.get(SERV_METADATA.toString(), "users.txt").toString();
    private static final String DOMAINS_TXT_DB = Paths.get(SERV_METADATA.toString(), "domains.txt").toString();
    private static final String DEVICES_TXT_DB = Paths.get(SERV_METADATA.toString(), "devices.txt").toString();
    private static final String PROGRAM_TXT_DB = Paths.get(SERV_METADATA.toString(), "program.txt").toString();

    private static final String SERV_USERDATA = Paths.get(SERVER_ROOT.toString(), "user_files").toString();


    private IoTFileManager() {
    }

    /**
     * @deprecated
     *      Use static methods instead.
     * @return
     *      An instance of IoTFileManager.
     */
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



    // ==========================================================
    //                  DEVICE WRITER/READER
    // ==========================================================
    /**
     * Writes a file related to a device to a , if a file with 
     * the same file name exists, it will be replaced with the 
     * current one.
     * @param device
     *      Device.
     * @param filename
     *      Name of the file to be written.
     * @param filebytes
     *      Bytes of the file to be written.
     * @return <ul>
     *      <li> 0 if file wrote correctly;
     *      <li> -1 if error occured while writing;
     *      <li> -2 if the arguments are invalid;
     */
    public static synchronized int writeDeviceFile(Device device, String filename, byte[] filebytes) {
        if (device == null || filename == null || filebytes == null)
            return -2;
        
        if (device.getOwner() == null || device.getDevId() < 0)
            return -2;
        
        // File path = ./server_files/user_files/username/username:devid/filename
        final String path = Paths.get(SERV_USERDATA.toString(), 
                                    device.getOwner().getName(), 
                                    Integer.toString(device.getDevId()),
                                    filename).toString();
        
        return IoTFileManager.writeFileAsBytes(path, filebytes) < 0 ? -1 : 0;
    }

    /**
     * Reads the image associated with the device.
     * @return
     *      Byte array of the image content or nothing
     *      if the device hasn't received any image,
     *      an exception occured during reading or the
     *      argument is invalid.
     */
    public static synchronized Optional<byte[]> readDeviceImg(Device device) {
        if (device == null)
            return Optional.empty();

        if (!device.getImgFileName().isPresent())
            return Optional.empty();
        
        final String path = Paths.get(SERV_USERDATA.toString(), 
                                device.getOwner().getName(), 
                                Integer.toString(device.getDevId()),
                                device.getImgFileName().get()).toString();
        
        Optional<byte[]> image = IoTFileManager.readFileAsBytes(path);
        if (!image.isPresent())
            return Optional.empty();
        
        return Optional.of(image.get());
    }

    /**
     * Reads the temperature associated with the device.
     * @return
     *      Byte array of the image content or nothing
     *      if the device hasn't received any image,
     *      an exception occured during reading, the
     *      argument is invalid or the reading isn't
     *      a number.
     */
    public static synchronized Optional<Float> readDeviceTemp(Device device) {
        if (device == null)
            return Optional.empty();
        
        final String path = Paths.get(SERV_USERDATA.toString(), 
                                device.getOwner().getName(), 
                                Integer.toString(device.getDevId()),
                                device.getTempFileName()).toString();
        
        Optional<byte[]> tempBytes = IoTFileManager.readFileAsBytes(path);
        if (!tempBytes.isPresent())
            return Optional.empty();
        
        String tempStr = new String(tempBytes.get());
        float temp;
        try {
            temp = Float.parseFloat(tempStr);
        } catch (Exception e) {
            return Optional.empty();
        }
        
        return Optional.of(temp);
    }




    // ==========================================================
    //                  CLASS ESPECIFIC LOADERS
    // ==========================================================

    /**
     * Reads content from plain text file and loads users.
     * Text file must have format
     * username:password
     * Will ignore lines that failed to load.
     * @param users
     *      Map to where data will be loaded.
     * @return <ul>
     *      <li> 0 if loaded successfully;
     *      <li> -1 if file doesn't exist;
     *      <li> -2 if no permissions to access file;
     *      <li> -3 if arguments are invalid;
     */
    public static synchronized int loadUsersFromText(Map<String, User> users) {
        if (users == null)
            return -3;

        File file = new File(USER_TXT_DB);
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
     * @param domains
     *      Map to where the data will be loaded.
     * @return <ul>
     *      <li> 0 if loaded successfully;
     *      <li> -1 if file doesn't exist;
     *      <li> -2 if no permissions to access file;
     *      <li> -3 if arguments are invalid;
     */
    public static synchronized int loadDomainsFromText(Map<String, Domain> domains) {
        if (domains == null)
            return -3;
        
        File file = new File(DOMAINS_TXT_DB);
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
     * @param devices
     *      Map to where the data will be loaded.
     * @return
     *      0 if loaded successfully;
     *      -1 if file doesn't exist;
     *      -2 if no permissions to access file;
     *      -3 if arguments are invalid;
     */
    public static synchronized int loadDevicesFromText(Map<String, Device> devices) {
        if (devices == null)
            return -3;
        
        File file = new File(DEVICES_TXT_DB);
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

    public static int loadProgramInfoFromText() {
        // TODO
        return 0;
    }



    // ==========================================================
    //                CLASS ESPECIFIC REGISTERS
    // ==========================================================

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
        
        // Tries to create all directories to the file
        File parent = file.getParentFile();
        if (parent != null) {
            if (!parent.exists() && !parent.mkdirs())
                return -1;
        }

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



    // ==========================================================
    //                  GENERAL PURPOSE READERS
    // ==========================================================

    /**
     * Writes byte array to a file, will create all necessary
     * folders on it's way. If a file with the same name exists,
     * it will be overwritten.
     * @param filePath
     *      Path to the file, including the file name.
     * @param filebytes
     *      Content of the file.
     * @return <ul>
     *      <li> 0 if wrote successfully;
     *      <li> -1 if arguments are invalid;
     *      <li> -2 if failed to create necessary folders to the file;
     *      <li> -3 if exception occured while writing;
     */
    public static synchronized int writeFileAsBytes(String filePath, byte[] filebytes) {
        if (filePath == null || filebytes == null)
            return -1;
        
        File file = new File(filePath);
        
        File parent = file.getParentFile();
        if (parent != null) {
            if (!parent.exists() && !parent.mkdirs())
                return -2;
        }
        
        try {
            FileOutputStream writer = new FileOutputStream(filePath);
            writer.write(filebytes);
            writer.close();
        } catch (Exception e) {
            return -3;
        }
        
        return 0;
    }

    /**
     * Reads a file into a byte array.
     * @param filePath
     * @return
     *      Byte array containing the content of the
     *      file or nothing if one of the following 
     *      errors happend:
     *      <ul>
     *          <li> File doesn't exist;
     *          <li> File is a directory;
     *          <li> No permissions to read and write;
     *          <li> Exception while trying to read;
     */
    public static synchronized Optional<byte[]> readFileAsBytes(String filePath) {
        if (filePath == null)
            return Optional.empty();
        
        if (!IoTFileManager.isFileAvailable(filePath))
            return Optional.empty();
        
        File file = new File(filePath);
        byte[] bytes;
        
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(bytes);
    }

    /**
     * Writes a Java object in binary to a file, will create one 
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
     * Reads a Java object in binary from a file.
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
