package src.Utils;

import java.io.File;

/**
 * Class that defines the type of message exchanged between server and client
 */
public class IoTMessage implements IoTMessageType{

    public IoTOpcodes opcode;
    
    public String userid;
        
    public String userpwd;

    public int devid;

    public String program_name;

    public int program_size;

    public String domain_name;

    public float temp;

    public File img;

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
    public void setProgramSize(int prgsize) {
        this.program_size = prgsize;
    }

    @Override
    public int getProgramSize() {
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

    public float getTemp() {
        return this.temp;
    }

}
