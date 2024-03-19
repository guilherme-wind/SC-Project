package utils;

import java.io.Serializable;

/**
 * Class that defines the type of message exchanged between server and client
 */
public class IoTMessage implements IoTMessageType, Serializable {

    private IoTOpcodes opcode;
    
    private String userid;
        
    private String userpwd;

    private int devid;

    private String program_name;

    private long program_size;

    private String domain_name;

    private float temp;

    private float[] temps;

    private String img_name;

    private long img_size;

    private byte[] img;

    private byte[] data;

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
    public void setImageName(String name) {
        this.img_name = name;
    }

    @Override
    public String getImageName() {
        return this.img_name;
    }

    @Override
    public void setImageSize(long size) {
        this.img_size = size;
    }

    @Override
    public long getImageSize() {
        return this.img_size;
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
