package server.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import utils.IoTFileManager;
import utils.IoTIParsable;



public class Device implements IoTIParsable {

    // String template for parsing
    private final String DEVICE_TEMP = "{Owner=%s,DevID=%s,ImageName=%s}";

    private Boolean isActive;
    private final User owner;
    private final int devId;
    private final String name;
    private String imgFileName = null;
    private String tempFileName;

    public Device(User owner, int devId) {
        this.isActive = false;
        this.owner = owner;
        this.devId = devId;
        this.name = String.format("%s:%s", owner.getName(), devId);
        this.tempFileName = String.format("%s_dev_%s_temp_log.txt", owner.getName(), devId);

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

    public String getTempFileName() {
        return this.tempFileName;
    }

    public Optional<String> getImgFileName() {
        if (this.imgFileName == null)
            return Optional.empty();
        return Optional.of(this.imgFileName);
    }

    public void setImgFileName(String filename) {
        this.imgFileName = filename;
    }

    /**
     * Registers the temperature.
     * @param temperature
     *      Temperature reading.
     * @return
     *      True if registered successfully, false
     *      otherwise.
     */
    public Boolean writeTemperature(String temperature) {
        return IoTFileManager.writeDeviceFile(this, this.tempFileName, temperature.getBytes()) == 0 ? true : false;
    }

    /**
     * Replacement of the original <code>writeImage(byte[] image)</code>
     * function.
     * Writes the image file to device's directory.
     * @param imagename
     *      Name of the image, not the path.
     * @param image
     *      Bytes of the image.
     * @return
     *      True if wrote successfully, false otherwise.
     */
    public Boolean writeImage(String imagename, byte[] image) {
        if (imagename == null || image == null)
            return false;
        setImgFileName(imagename);
        return IoTFileManager.writeDeviceFile(this, imagename, image) == 0 ? true : false;
    }

    /**
     * Get the latest temperature reading reported
     * by the device.
     * @return
     *      Temperature reading or nothing if the device
     *      hasn't sent any readings yet or an error while
     *      trying to get the reading.
     */
    public Optional<Float> readTemperature() {
        return IoTFileManager.readDeviceTemp(this);
    }

    /**
     * Get the lastest image sent to the device.
     * @return
     *      Bytes of the image or nothing if the device
     *      hasn't uploaded any image yet or an error
     *      occured while trying to read the image.
     */
    public Optional<byte[]> readImage() {
        return IoTFileManager.readDeviceImg(this);
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
     * @param users
     *      Map containing users.
     * @param serial
     *      Device string representation.
     * @return
     *      A new Device object or null if the string doesn't
     *      matches the correct format.
     */
    public static Device parseFromSerial(Map<String, User> users, String serial) {
        if (serial == null)
            return null;
        
        int start_brace_index = serial.indexOf("{");
        if (start_brace_index < 0)
            return null;

        int end_brace_index = serial.lastIndexOf("}");
        if (end_brace_index == -1)
            return null;
        
        // Extract content between curly brackets {}
        String content = serial.substring(start_brace_index+1, end_brace_index);

        List<String> tokens = IoTIParsable.separateStrByChar(',', content);
        if (tokens.size() < 3)
            return null;
        

        User user = null;
        int devid = -1;
        String imagename = null;

        for (String string : tokens) {
            String[] params = string.split("=", 2);
            if (params.length != 2)
                continue;
            

            if (params[0].compareToIgnoreCase("OWNER") == 0) {
                User tempUser = User.parseFromSerial(params[1]);
                user = users.get(tempUser.getName());
                if (user == null)
                    return null;
            } else if (params[0].compareToIgnoreCase("DEVID") == 0) {
                try {
                    devid = Integer.parseInt(params[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if (params[0].compareToIgnoreCase("IMAGENAME") == 0) {
                if (!params[1].equalsIgnoreCase("NULL"))
                    imagename = params[1];
            }
        }
        if (user == null || devid < 0)
            return null;

        Device device = new Device(user, devid);
    
        if (imagename != null)
            device.setImgFileName(imagename);

        return device;
    }

    @Override
    public String parseToSerial() {
        return String.format(DEVICE_TEMP, owner.parseToSerial(), 
                             Integer.toString(devId),
                             this.imgFileName == null ? "null" : this.imgFileName);
    }
}
