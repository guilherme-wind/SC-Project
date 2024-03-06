public class IoTDevice{
    private String serverAddress;
    private int devId;
    private String userId;

    public IoTDevice(String serverAddress, int devId, String userId){
        this.serverAddress = serverAddress; // Falta verificar se contem o port ou não
        this.devId = devId;
        this.userId = userId;

    }
}