package src.server.model;

import src.utils.IoTAuth;

public class Session {
    private Device device;
    private User user;
    private IoTAuth authState;

    public Session() {
        this.device = null;
        this.user = null;
        this.authState = IoTAuth.NONE;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return this.device;
    }

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
    
}


