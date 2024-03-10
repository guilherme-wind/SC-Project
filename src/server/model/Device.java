package src.server.model;


import java.io.File;

import src.utils.IoTPersistance;



public class Device {
    private final String name;
    private final File tempLogFile;
    private final File imgLogFile;

    public Device(String name) {
        this.name = name;
        this.tempLogFile = new File(String.format("%s_temp_log.txt", name));
        this.imgLogFile = new File(String.format("%s_img_log.txt", name));
    }

    public String getName() {
        return this.name;
    }

    public void writeTemperature(String temperature) {
        IoTPersistance.write(temperature, this.tempLogFile, false);
    }

    public void writeImage(String image) {
        IoTPersistance.write(image, this.imgLogFile, false);
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
}
