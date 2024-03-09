package src.Utils;

public enum IoTOpcodes {

    // ---------------------------
    //      Authentication
    // ---------------------------
    // Client request operations
    VALIDATE_DEVICE, 
    VALIDADE_USER, 
    VALIDATE_PROGRAM,

    // Server responses
    WRONG_PWD,
    OK_NEW_USER,
    OK_USER,
    NOK_DEVID,
    OK_DEVID,
    NOK_TESTED,
    OK_TESTED,

    // ---------------------------
    //        Operations
    // ---------------------------
    // Client request operations
    CREATE_DOMAIN,
    ADD_USER_DOMAIN,
    REGISTER_DEVICE_DOMAIN,
    SEND_TEMP,
    SEND_IMAGE,

    // Server responses
    OK_ACCEPTED,
    NOK_NO_PERMISSIONS,
    NOK_NO_DOMAIN,
    NOK_NO_USER,
    NOK_ALREADY_EXISTS
}
