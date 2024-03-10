package src.utils;

public enum IoTAuth {
    // Nothing has been authenticated
    NONE,
    // User has been authenticated
    USER,
    // Device has been authenticated
    USER_DEVICE,
    // Program has been authenticated
    COMPLETE;
}