package src.utils;

public interface IoTIParsable {
    
    /**
     * Gets a string representation of the object.
     * @deprecated
     *      To serialize object in order to write to 
     *      persistent storage, please use 
     *      {@link IoTFileManager#writeObjectToFile(String, Object)}
     * @return
     *      String representation of the object.
     */
    @Deprecated
    public String parseToSerial();

    /**
     * Parses a serial representation into an object.
     * @deprecated
     *      To serialize object in order to write to 
     *      persistent storage, please use 
     *      {@link IoTFileManager#readObjectFromFile(String)}
     * @param serial
     *      The serial representation to parse.
     * @return
     *      An instance of the implementing class.
     */
    @Deprecated
    public static IoTIParsable parseFromSerial(String serial) {
        // Implementation specific to each class implementing IoTIParsable
        return null;
    }

    
}
