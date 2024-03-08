package Utils;

import Utils.IoTOpcodes;

import java.io.File;

/**
 * Class that defines the type of message exchanged between server and client
 */
public class IoTMessage {

    public IoTOpcodes opcode;

    public String userid;

    public int devid;

    public String userpwd;

    public String domain_name;

    public float temp;

    public File img;

    
    
    public IoTMessage() {

    }

}
