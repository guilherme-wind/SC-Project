package src.server.model;

import src.utils.IoTAuth;

/**
 * Represents a session between a device and 
 * server, contains data regarding the 
 * authentication state.
 */
public class Session {
    private Device device;
    private User user;
    private IoTAuth authState;

    public Session() {
        this.device = null;
        this.user = null;
        this.authState = IoTAuth.NONE;
    }

    /**
     * Defines the device used in the session
     * @param device
     */
    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return this.device;
    }

    /**
     * Defines the user in the session
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public IoTAuth getAuthState() {
        return this.authState;
    }

    public void setAuthState(IoTAuth state) {
        this.authState = state;
    }

    /**
     * Terminates a session and puts 
     * device active state to off.
     */
    public void close() {
        if (this.device != null) {
            this.device.turnOff();
        }
    }
    
}


