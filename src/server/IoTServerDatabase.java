package server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import server.model.Device;
import server.model.Domain;
import server.model.User;
import utils.IoTFileManager;
import utils.IoTIParsable;
import utils.IoTOpcodes;

/**
 * Represents a database that stores user, domain and devices
 * information.
 */
public class IoTServerDatabase {
    
    // Singleton
    private static IoTServerDatabase instance;
    
    private Map<String, Domain> domains;
    private Map<String, User> users;
    private Map<String, Device> devices;

    // TODO
    private String client_program_name;
    private long client_program_size;
    
    // TODO
    private static final Path ROOT = Paths.get(".", "server_files", "metadata");
    
    private static final String USER_TXT_DB = Paths.get(ROOT.toString(), "users.txt").toString();
    private static final String DOMAINS_TXT_DB = Paths.get(ROOT.toString(), "domains.txt").toString();
    private static final String DEVICES_TXT_DB = Paths.get(ROOT.toString(), "devices.txt").toString();
    
    private IoTServerDatabase() {
        this.domains = new HashMap<>();
        this.users = new HashMap<>();
        this.devices = new HashMap<>();
        
        load();
    }
    
    /*
    * Load persisted database files, if any
    */
    private void load() {
        IoTFileManager.loadUsersFromText(this.users);
        IoTFileManager.loadDevicesFromText(this.users, this.devices);
        IoTFileManager.loadDomainsFromText(this.users, this.devices, this.domains);

        Optional<String> progname = IoTFileManager.loadProgramNameFromText();
        client_program_name = progname.isPresent() ? progname.get() : null;

        Optional<Long> progsize = IoTFileManager.loadProgramSizeFromText();
        client_program_size = progsize.isPresent() ? progsize.get() : -1;
    }
    
    public static synchronized IoTServerDatabase getInstance() {
        if (instance == null) {
            instance = new IoTServerDatabase();
        }
        return instance;
    }
    
    public static void setInstance(IoTServerDatabase db) {
        instance = db;
    }

    /**
     * Adds a new user to the database.
     * @param user
     */
    public void addUser(User user) {
        this.users.put(user.getName(), user);
    }

    /**
     * Checks if the database contains user with the given username.
     * @param userName
     * @return
     */
    public Boolean containsUser(String userName) {
        return this.users.containsKey(userName);
    }

    /**
     * Get the user by it's username.
     * @param userName
     *      Username.
     * @return
     *      User or Null if user isn't in the database.
     */
    public User getUser(String userName) {
        return this.users.get(userName);
    }

    /**
     * Adds a new device to the database.
     * @param device
     */
    public void addDevice(Device device) {
        this.devices.put(device.getName(), device);
    }

    public Boolean containsDevice(String deviceName) {
        return this.devices.containsKey(deviceName);
    }

    public Device getDevice(String deviceName) {
        return this.devices.get(deviceName);
    }

    public String getClientProgramName() {
        return this.client_program_name;
    }

    public long getClientProgramSize() {
        return this.client_program_size;
    }

    public Boolean containsDomain(String domainName) {
        return this.domains.containsKey(domainName);
    }

    public void addDomain(Domain domain) {
        this.domains.put(domain.getName(), domain);
    }

    public Domain getDomain(String domainName) {
        return this.domains.get(domainName);
    }


    public IoTOpcodes createDomain(User as, String domainName) {
        if (this.domains.containsKey(domainName))
            return IoTOpcodes.NOK_ALREADY_EXISTS;

        this.domains.put(domainName, new Domain(domainName, as));
        return IoTOpcodes.OK_ACCEPTED;
    }

    /**
     * @deprecated
     *      Use {@link Device#writeTemperature(String)} instead
     * @param device
     * @param temperature
     * @return
     */
    public IoTOpcodes updateTemperature(Device device, String temperature) {
        // device.writeTemperature(temperature);
        IoTFileManager.writeDeviceFile(device, device.getTempFileName(), temperature.getBytes());
        return IoTOpcodes.OK_ACCEPTED;
    }

    /**
     * @deprecated
     *      Use {@link Device#writeImage(String, byte[])} instead
     * @param device
     * @param imagename
     * @param image
     * @return
     */
    public IoTOpcodes updateImage(Device device, String imagename, byte[] image) {
        IoTFileManager.writeDeviceFile(device, imagename, image);
        return IoTOpcodes.OK_ACCEPTED;
    }


    public IoTOpcodes addUserToDomain(User as, String userName, String domainName) {
        if (!this.domains.containsKey(domainName))
            return IoTOpcodes.NOK_NO_DOMAIN;

        if (!this.users.containsKey(userName))
            return IoTOpcodes.NOK_NO_USER;

        Domain domain = this.domains.get(domainName);
        if (!domain.ownedBy(as))
            return IoTOpcodes.NOK_NO_PERMISSIONS;
        
        User user = this.users.get(userName);
        domain.addUser(user);
        return IoTOpcodes.OK_ACCEPTED;
    }

    public Boolean canUserReceiveDataFromDevice(User as, Device device) {
        for (Domain domain : this.domains.values())
            if (domain.contains(device) && domain.contains(as))
                return true;

        return false;
    }

    public IoTOpcodes registerDeviceToDomain(User as, Device device, String domainName) {
        if (!this.domains.containsKey(domainName))
            return IoTOpcodes.NOK_NO_DOMAIN;

        Domain domain = this.domains.get(domainName);
        if (!domain.contains(as))
            return IoTOpcodes.NOK_NO_PERMISSIONS;

        domain.registerDevice(device);
        return IoTOpcodes.OK_ACCEPTED;
    }

    public void onUserUpdate() {
        List<IoTIParsable> objs = this.users.values()
            .stream()
            .map(user -> (IoTIParsable) user)
            .collect(Collectors.toList());

        IoTFileManager.writeObjsToText(USER_TXT_DB, objs);
    }

    public void onDomainUpdate() {
        List<IoTIParsable> objs = this.domains.values()
            .stream()
            .map(domain -> (IoTIParsable) domain)
            .collect(Collectors.toList());

        IoTFileManager.writeObjsToText(DOMAINS_TXT_DB, objs);
    }

    public void onDeviceUpdate() {
        List<IoTIParsable> objs = this.devices.values()
            .stream()
            .map(device -> (IoTIParsable) device)
            .collect(Collectors.toList());
        
        IoTFileManager.writeObjsToText(DEVICES_TXT_DB, objs);
    }
    
}
