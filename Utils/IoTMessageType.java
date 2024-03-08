package Utils;

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
     * Set the userid of the message.
     * @param userid
     *      User id.
     */
    public void setUserId(String userid);

    /**
     * Returns the user id in the message.
     * @return
     */
    public String getUserId();

    
}
