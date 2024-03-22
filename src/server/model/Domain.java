package server.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import utils.IoTIParsable;

public class Domain implements IoTIParsable {

    private static final String LINE = System.getProperty("line.separator");
    // String template for parsing
    private final String DOMAIN_TEMP = "{Name=%s,Owner=%s,NameSpace=%s,Devices=%s}";

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

    public Map<String,Float> extractTemperatures() {
        Map<String,Float> map = new HashMap<String,Float>();
        for (Device device : this.devices) {
            Optional<Float> lastTemperature = device.readTemperature();                 
            if (lastTemperature.isPresent()) {
                map.put(device.getName(), lastTemperature.get());
            }
        }
        return map;
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

    /**
     * Receives a string representation of a domain and
     * creates an new Domain object or null if the string
     * passed doesn't meets the correct format.
     * The correct format is {Name=xx,Owner=xx,NameSpace=[x,y,...],Devices=[x,y,...]}
     * @param serial
     * @return
     *      An new Domain object or null if the 
     *      string doesn't meet the correct format.
     */
    public static Domain parseFromSerial(String serial) {
        if (serial == null)
            return null;
        
        // Remove lines
        serial.replace(LINE, "");
        serial.replace("\t", "");

        int start_brace_index = serial.indexOf("{");
        if (start_brace_index < 0)
            return null;

        int end_brace_index = serial.lastIndexOf("}");
        if (end_brace_index == -1)
            return null;
        
        // Extract content between curly brackets {}
        String content = serial.substring(start_brace_index+1, end_brace_index);

        // Split by commas, ignoring strings between {} and []
        String[] tokens = content.split(",(?=[^(\\}|\\])]*(?:(\\{|\\[)|$))");

        String name = null, ownerStr = null, namespaceStr = null, devicesStr = null;

        for (int i = 0; i < tokens.length; i++) {
            // Split each token into 2
            String[] args = tokens[i].split("=",2);
            if (args.length < 2)
                continue;
            
            if (args[0].equalsIgnoreCase("NAME")) {
                if (name == null)
                    name = args[1];
            } else if (args[0].equalsIgnoreCase("OWNER")) {
                if (ownerStr == null)
                    ownerStr = args[1];
            } else if (args[0].equalsIgnoreCase("NAMESPACE")) {
                if (namespaceStr == null)
                    namespaceStr = args[1];
            } else if (args[0].equalsIgnoreCase("DEVICES")) {
                if (devicesStr == null)
                    devicesStr = args[1];
            }
        }

        // Create owner user
        User owner = User.parseFromSerial(ownerStr);
        if (owner == null)
            return null;
        
        Domain domain = new Domain(name, owner);
        
        // Add each user in the namespace
        List<String> namespaceList = parseSetFromSerial(namespaceStr);
        if (namespaceList == null)
            return null;
        for (String userStr : namespaceList) {
            User user = User.parseFromSerial(userStr);
            if (user == null)
                return null;
            domain.addUser(user);
        }

        // Add each device to the namespace
        List<String> devicesList = parseSetFromSerial(devicesStr);
        if (devicesList == null)
            return null;
        for (String deviceStr : devicesList) {
            Device device = Device.parseFromSerial(deviceStr);
            if (device == null)
                return null;
            domain.registerDevice(device);
        }

        return domain;
    }

    @Override
    public String parseToSerial() {
        String domain = String.format(DOMAIN_TEMP, 
                        name, 
                        owner.parseToSerial(),
                        parseSetToSerial(namespace),
                        parseSetToSerial(devices));
        return domain;
    }

    /**
     * Parses a set of parsable objects to a string representation.
     * The format is "[x,y,z,...]"
     * @param set
     *      Set of parsable objects.
     * @return
     *      String representation of the given set.
     */
    private String parseSetToSerial(Set<?> set) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        Iterator<IoTIParsable> it = (Iterator<IoTIParsable>) set.iterator();
        while (it.hasNext()) {
            sj.add(it.next().parseToSerial());
        }
        return sj.toString();
    }

    /**
     * Parses a string into a list or null if the 
     * string doesn't match the correct format.
     * The format is "[x,y,z,...]"
     * @param serial
     * @return
     */
    private static List<String> parseSetFromSerial(String serial) {
        int start_brace_index = serial.indexOf("[");
        if (start_brace_index < 0)
            return null;

        int end_brace_index = serial.lastIndexOf("]");
        if (end_brace_index == -1)
            return null;
        
        String content = serial.substring(start_brace_index+1, end_brace_index);

        String[] tokens = content.split(",");

        List<String> strings = new LinkedList<String>();
        for (String string : tokens) {
            strings.add(string);
        }
        
        return strings;
    }
    
}
