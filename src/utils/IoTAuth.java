package src.utils;

public enum IoTAuth {
    // Nothing has been authenticated
    NONE,
    // User has been authenticated
    AUTHEN_USER,
    // Device has been authenticated
    AUTHEN_DEVICE,
    // Program has been authenticated
    AUTHEN_COMPLETE;
}