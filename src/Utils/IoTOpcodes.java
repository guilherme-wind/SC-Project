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
    OK_TESTED
}
