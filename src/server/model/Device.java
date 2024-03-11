package src.server.model;


import java.io.File;

import src.utils.IoTPersistance;



public class Device {
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
        this.tempLogFile = new File(String.format("%s_temp_log.txt", name));
        this.imgLogFile = new File(String.format("%s_img_log.txt", name));
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

    public Boolean writeImage(String image) {
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
        System.out.println(String.format("My device name is %s and this device name is %s. are they equal? %b", this.name, device.name, this.name.equals(device.name)));
        return this.name.equals(device.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
