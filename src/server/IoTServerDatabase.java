package src.server;

import java.util.HashMap;
import java.util.Map;

import src.server.model.Device;
import src.server.model.Domain;
import src.server.model.User;
import src.utils.IoTOpcodes;

/**
 * Represents a database that stores user, domain and devices
 * information.
 */
public class IoTServerDatabase {
    private static IoTServerDatabase instance;

    private final Map<String, Domain> domains;
    private final Map<String, User> users;
    private final Map<String, Device> devices;

    private IoTServerDatabase() {
        this.domains = new HashMap<>();
        this.users = new HashMap<>();
        this.devices = new HashMap<>();
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

    public static IoTServerDatabase getInstance() {
        if (instance == null) {
            instance = new IoTServerDatabase();
        }
        return instance;
    }

    public IoTOpcodes createDomain(User as, String domainName) {
        if (this.domains.containsKey(domainName))
            return IoTOpcodes.NOK_ALREADY_EXISTS;

        this.domains.put(domainName, new Domain(domainName, as));

        return IoTOpcodes.OK_ACCEPTED;
    }

    public IoTOpcodes updateTemperature(Device device, String temperature) {
        device.writeTemperature(temperature);
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

    public IoTOpcodes createDevice(String name) {
        Device device = new Device(name);
        this.devices.put(device.getName(), device);
        return IoTOpcodes.OK_ACCEPTED;
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
    
}
