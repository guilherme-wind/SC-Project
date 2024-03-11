package src.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

public class IoTPersistance {

    public static synchronized Boolean write(String logMessage, File file, Boolean append) {
        Boolean success = false;
        try (OutputStream outputStream = new FileOutputStream(file, append)) {
            outputStream.write(logMessage.getBytes());
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static synchronized byte[] read(File file) {
        try (InputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}