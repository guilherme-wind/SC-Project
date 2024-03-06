public class IoTServer{
    private string port;

    public IoTServer(String port){
    this.port = port === null ? "12345" : port;
    }
}