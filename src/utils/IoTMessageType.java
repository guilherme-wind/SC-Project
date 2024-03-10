package src.utils;

public interface IoTMessageType {
    
    /**
     * Sets the opcode of the message.
     * @param opcode
     *      Opcode of the request/response.
     */
    public void setOpCode(IoTOpcodes opcode);

    /**
     * Returns the opcode of the message.
     * @return 
     *      Opcode contained in the message.
     */
    public IoTOpcodes getOpcode();

    /**
     * Set the user id of the message.
     * @param userid
     *      User id.
     */
    public void setUserId(String userid);

    /**
     * Returns the user id in the message.
     * @return
     */
    public String getUserId();

    public void setUserPwd(String pwd);

    public String getUserPwd();

    public void setProgramName(String prgname);

    public String getProgramName();

    public void setProgramSize(long prgsize);

    public long getProgramSize();

    public void setDevId(int devid);

    public int getDevId();

    public void setDomainName(String domain);

    public String getDomainName();

    public void setTemp(float temp);

    public float getTemp();

    public void setTemps(float[] temps);

    public float[] getTemps();

    public void setImage(byte[] image);

    public byte[] getImage();
}
