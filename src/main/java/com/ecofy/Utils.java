package com.ecofy;

import io.swagger.models.auth.In;

import java.io.IOException;
import java.io.InputStream;

public class Utils {


    // Método para comparar dos InputStreams
    public static boolean compararInputStreams(InputStream inputStream1, InputStream inputStream2) throws IOException {
        // Buffer para almacenar los datos leídos de cada InputStream
        byte[] buffer1 = new byte[1024];
        byte[] buffer2 = new byte[1024];

        int bytesRead1;
        int bytesRead2;

        // Leer y comparar los datos de ambos InputStreams
        while ((bytesRead1 = inputStream1.read(buffer1)) != -1) {
            bytesRead2 = inputStream2.read(buffer2);

            if (bytesRead2 == -1 || !sonBytesIguales(buffer1, buffer2, bytesRead1, bytesRead2)) {
                return false; // Si los datos son diferentes o uno de los InputStreams llega al final antes que el otro
            }
        }

        // Verificar si el segundo InputStream también ha llegado al final
        return inputStream2.read(buffer2) == -1;
    }

    // Método para comparar dos arrays de bytes
    public static  boolean sonBytesIguales(byte[] buffer1, byte[] buffer2, int length1, int length2) {
        if (length1 != length2) {
            return false;
        }

        for (int i = 0; i < length1; i++) {
            if (buffer1[i] != buffer2[i]) {
                return false;
            }
        }

        return true;
    }


    public static void closeInputStream(InputStream inputStream){
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
