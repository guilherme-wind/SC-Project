package utils;

public enum IoTOpcodes {
    // ---------------------------
    //      Authentication
    // ---------------------------
    // Client request operations
    VALIDATE_DEVICE("VALIDATE_DEVICE"), 
    VALIDATE_USER("VALIDATE_USER"), 
    VALIDATE_PROGRAM("VALIDATE_PROGRAM"),

    // Server responses
    WRONG_PWD("WRONG_PWD"),
    OK_NEW_USER("OK_NEW_USER"),
    OK_USER("OK_USER"),
    NOK_DEVID("NOK_DEVID"),
    OK_DEVID("OK_DEVID"),
    NOK_TESTED("NOK_TESTED"),
    OK_TESTED("OK_TESTED"),

    // ---------------------------
    //        Operations
    // ---------------------------
    // Client request operations
    EXIT("EXIT"),
    CREATE_DOMAIN("CREATE_DOMAIN"),
    ADD_USER_DOMAIN("ADD_USER_DOMAIN"),
    REGISTER_DEVICE_DOMAIN("REGISTER_DEVICE_DOMAIN"),
    SEND_TEMP("SEND_TEMP"),
    SEND_IMAGE("SEND_IMAGE"),
    GET_TEMP("GET_TEMP"),
    GET_USER_IMAGE("GET_USER_IMAGE"),

    // Server responses
    OK_ACCEPTED("OK_ACCEPTED"),
    NOK("NOK"),
    NOK_NO_PERMISSIONS("NOK_NO_PERMISSIONS"),
    NOK_NO_DOMAIN("NOK_NO_DOMAIN"),
    NOK_NO_USER("NOK_NO_USER"),
    NOK_NO_DEVICE("NOK_NO_DEVICE"),
    NOK_ALREADY_EXISTS("NOK_ALREADY_EXISTS"),
    NOK_NO_DATA("NOK_NO_DATA"),
    NOK_BAD_FORMAT("NOK_BAD_FORMAT");

    private final String code;
    private IoTOpcodes(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
