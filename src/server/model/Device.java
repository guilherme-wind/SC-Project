package src.server.model;


import java.io.File;

import src.utils.IoTIParsable;
import src.utils.IoTPersistance;



public class Device implements IoTIParsable{
    private Boolean isActive;
    private final User owner;
    private final int devId;
    private final String name;
    private final File tempLogFile;
    private final File imgLogFile;

    public Device(User owner, int devId) {
        this.isActive = true;
        this.owner = owner;
        this.devId = devId;
        this.name = String.format("%s:%s", owner.getName(), devId);
        this.tempLogFile = new File(String.format("%s_dev_%s_temp_log.txt", owner.getName(), devId));
        this.imgLogFile = new File(String.format("%s_dev_%s_img.jpeg", owner.getName(), devId));
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

    public Boolean writeTemperature(String temperature) {
        return IoTPersistance.write(temperature, this.tempLogFile, false);
    }

    public Boolean writeImage(byte[] image) {
        return IoTPersistance.write(image, this.imgLogFile, false);
    }

    public byte[] readTemperature() {
        return IoTPersistance.read(this.tempLogFile);
    }

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

    public static Device parseFromSerial(String serial) {
        // TODO
        return null;
    }

    @Override
    public String parseToSerial() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseToSerial'");
    }
}
