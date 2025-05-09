package utils;

import java.util.Map;

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

    public void setTemps(Map<String,Float> temps);

    public Map<String,Float> getTemps();

    public void setImageName(String name);

    public String getImageName();

    public void setImage(byte[] image);

    public byte[] getImage();

    public void setImageSize(long size);

    public long getImageSize();

    /**
     * Set general data.
     * Should use {@link IoTMessageType#getImage()} 
     * to set the content of image!
     * @deprecated
     *      Avoid use this method! Use {@link IoTMessageType#setImage()}
     *      instead!
     * @return
     */
    @Deprecated
    public void setData(byte[] data);

    /**
     * Get general data.
     * Should use {@link IoTMessageType#getImage()} 
     * to get the content of image!
     * @deprecated
     *      Avoid use this method! Use {@link IoTMessageType#getImage()}
     *      instead!
     * @return
     */
    @Deprecated
    public byte[] getData();
}
