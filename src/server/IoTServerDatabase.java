package src.server;

import java.util.HashMap;
import java.util.Map;

import src.server.model.Device;
import src.server.model.Domain;
import src.server.model.User;
import src.utils.IoTOpcodes;


public class IoTServerDatabase {
    private final Map<String, Domain> domains;
    private final Map<String, User> users;
    private final Map<String, Device> devices;

    public IoTServerDatabase() {
        this.domains = new HashMap<>();
        this.users = new HashMap<>();
        this.devices = new HashMap<>();
    }

    public IoTOpcodes createDomain(User as, String domainName) {
        if (this.domains.containsKey(domainName))
            return IoTOpcodes.NOK_ALREADY_EXISTS;

        this.domains.put(domainName, new Domain(domainName, as));
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

    public IoTOpcodes createDevice(String name, String ownerName) {
        Device device = new Device(name, ownerName);
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
