package src.utils;

public interface IoTIParsable {
    
    /**
     * Gets a string representation of the object.
     * @return
     *      String representation of the object.
     */
    public String parseToSerial();

    /**
     * Parses a serial representation into an object.
     * @param serial
     *      The serial representation to parse.
     * @return
     *      An instance of the implementing class.
     */
    public static IoTIParsable parseFromSerial(String serial) {
        // Implementation specific to each class implementing IoTIParsable
        return null;
    }

    
}
