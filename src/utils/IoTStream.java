package src.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IoTStream {
    public Boolean ready = false;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public IoTStream(Socket socket) {
        this.socket = socket;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            this.ready = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the socket is open.
     * @return
     *      True if it's open;
     *      False if it's closed;
     */
    public Boolean ready() {
        return !this.socket.isClosed();
    }

    /**
     * Closes the socket and it's streams.
     */
    public void close() {
        try {
            if (this.outputStream != null) {
                this.outputStream.close();
            }
            if (this.outputStream != null) {
                this.outputStream.close();
            }
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes an object to the socket.
     * @param message
     *      Object to be sent through socket.
     * @return
     *      True if concluded successfully;
     *      False if exception occured;
     */
    public Boolean write(Object message) {
        Boolean success = false;
        try {
            this.outputStream.writeObject(message);
            this.outputStream.flush();
            success = true;
        } catch (IOException e) {
        }

        return success;
    }

    /**
     * Reads an object from socket.
     * @return
     *      Object or Null if exception occured.
     */
    public Object read() {
        Object object = null;
        try {
            object = this.inputStream.readObject();
        } catch (Exception e) {
        }

        return object;
    }
}
