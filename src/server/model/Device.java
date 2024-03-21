package server.model;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import utils.IoTIParsable;
import utils.IoTPersistance;



public class Device implements IoTIParsable {

    private static final Path ROOT = Paths.get(".", "server_files", "user_files");

    // String template for parsing
    private final String DEVICE_TEMP = "%s|%s";

    private Boolean isActive;
    private final User owner;
    private final int devId;
    private final String name;
    private final File tempLogFile;
    private final File imgLogFile;

    public Device(User owner, int devId) {
        this.isActive = false;
        this.owner = owner;
        this.devId = devId;
        this.name = String.format("%s:%s", owner.getName(), devId);
        String tempLogFileName = String.format("%s_dev_%s_temp_log.txt", owner.getName(), devId);
        this.tempLogFile = Paths.get(ROOT.toString(), tempLogFileName).toFile();
        String imgLogFileName = String.format("%s_dev_%s_img.jpeg", owner.getName(), devId);
        this.imgLogFile = Paths.get(ROOT.toString(), imgLogFileName).toFile();
    }

    public void setActive() {
        this.isActive = true;
    }

    public void turnOff() {
        this.isActive = false;
    }

    public Boolean isActive() {
        return this.isActive;
    }


    public User getOwner() {
        return this.owner;
    }

    public int getDevId() {
        return this.devId;
    }

    public String getName() {
        return this.name;
    }

    /**
     * @deprecated
     * @param temperature
     * @return
     */
    public Boolean writeTemperature(String temperature) {
        return IoTPersistance.write(temperature, this.tempLogFile, false);
    }

    /**
     * @deprecated
     * @param image
     * @return
     */
    public Boolean writeImage(byte[] image) {
        return IoTPersistance.write(image, this.imgLogFile, false);
    }

    /**
     * @deprecated
     * @return
     */
    public byte[] readTemperature() {
        return IoTPersistance.read(this.tempLogFile);
    }

    /**
     * @deprecated
     * @return
     */
    public byte[] readImage() {
        return IoTPersistance.read(this.imgLogFile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return this.name.equals(device.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Creates a Device object from the given string,
     * which must has the following format:
     * <user-parsed-descr>|<dev-id>
     * @param serial
     *      Device string representation.
     * @return
     *      A new Device object or null if the string doesn't
     *      matches the correct format.
     */
    public static Device parseFromSerial(String serial) {
        if (serial == null)
            return null;

        String[] tokens = serial.split("\\|", 2);
        if (tokens.length < 2)
            return null;
        
        User user = User.parseFromSerial(tokens[0]);
        if (user == null)
            return null;

        int devid;
        try {
            devid = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            return null;
        }

        Device device = new Device(user, devid);

        return device;
    }

    @Override
    public String parseToSerial() {
        return String.format(DEVICE_TEMP, owner.parseToSerial(), Integer.toString(devId));
    }
}
