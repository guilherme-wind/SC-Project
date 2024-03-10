package src.server.model;

import java.util.HashSet;
import java.util.Set;

public class Domain {
    private final String name;
    private final User owner;
    private final Set<User> namespace;
    private final Set<Device> devices;

    public Domain(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.namespace = new HashSet<>();
        this.namespace.add(owner);
        this.devices = new HashSet<>();
    }

    public String getName() {
        return this.name;
    }

    public Boolean ownedBy(User user) {
        return this.owner.equals(user);
    }

    public Boolean contains(User user) {
        return this.namespace.contains(user);
    }

    public Boolean contains(Device device) {
        return this.devices.contains(device);
    }

    public boolean addUser(User user) {
        return this.namespace.add(user);
    }

    public boolean registerDevice(Device device) {
        return this.devices.add(device);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domain domain = (Domain) o;
        return this.name.equals(domain.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
