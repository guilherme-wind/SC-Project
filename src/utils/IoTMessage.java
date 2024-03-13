package src.utils;

import java.io.Serializable;

/**
 * Class that defines the type of message exchanged between server and client
 */
public class IoTMessage implements IoTMessageType, Serializable {

    public IoTOpcodes opcode;
    
    public String userid;
        
    public String userpwd;

    public int devid;

    public String program_name;

    public long program_size;

    public String domain_name;

    public float temp;

    public float[] temps;

    public byte[] img;

    public byte[] data;

    @Override
    public void setOpCode(IoTOpcodes opcode) {
        this.opcode = opcode;
    }

    @Override
    public IoTOpcodes getOpcode() {
        return this.opcode;
    }

    @Override
    public void setUserId(String userid) {
        this.userid = userid;
    }

    @Override
    public String getUserId() {
        return this.userid;
    }

    @Override
    public void setUserPwd(String pwd) {
        this.userpwd = pwd;
    }

    @Override
    public String getUserPwd() {
        return this.userpwd;
    }

    @Override
    public void setProgramName(String prgname) {
        this.program_name = prgname;
    }

    @Override
    public String getProgramName() {
        return this.program_name;
    }

    @Override
    public void setProgramSize(long prgsize) {
        this.program_size = prgsize;
    }

    @Override
    public long getProgramSize() {
        return this.program_size;
    }

    @Override
    public void setDevId(int devid) {
        this.devid = devid;
    }

    @Override
    public int getDevId() {
        return this.devid;
    }

    @Override
    public void setDomainName(String domain) {
        this.domain_name = domain;
    }

    @Override
    public String getDomainName() {
        return this.domain_name;
    }

    @Override
    public void setTemp(float temp) {
        this.temp = temp;
    }

    @Override
    public float getTemp() {
        return this.temp;
    }
    
    @Override
    public void setImage(byte[] image) {
        this.img = image;
    }

    @Override
    public void setTemps(float[] temps) {
        this.temps = temps;
    }

    @Override
    public float[] getTemps() {
        return this.temps;
    }

    @Override
    public byte[] getImage() {
        return this.img;
    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return this.opcode.toString();
    }


}
