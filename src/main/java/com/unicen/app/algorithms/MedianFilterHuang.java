package com.unicen.app.algorithms;

import com.unicen.CapturedImage;

public class MedianFilterHuang extends AlgorithmFilter{

    public static CapturedImage execute(CapturedImage item, double k, int dist) {
        // Create a new CapturedImage instance
        CapturedImage aux = new CapturedImage(item.getHeight(), item.getWidth(), item.getDepth(), item.getChannels());

        // Create array histogram
        int[][] histogram = new int[item.getHeight()][256];

        // Initialize histogram with 0
        for (int row = 0; row < item.getHeight(); row++) {
            for (int col = 0; col < 256; col++) {
                histogram[row][col] = 0;
            }
        }
        // Initialize histogram with initial values
        int icol = 0;
        for (int row = 0; row < item.getHeight(); row++) {
            for (int i = -dist; i <= dist; i++) {
                for (int j = -dist; j <= dist; j++) {
                    if((row+i >= 0)&&((icol+j >= 0)&&(row+i < item.getHeight())&&(icol+j < item.getWidth())))
                        try{
                            histogram[row][item.getValueImage(row + i, icol + j)]++;
                        }
                        catch (Exception e){
                            System.out.println(e);
                        }
                    }
                }
        }


        // Algoritmo de Huang
        int nc = 0;
        for (int row = 0; row < item.getHeight(); row++) {
            for (int col = 0; col < item.getWidth(); col++) {
                // Calcular la mediana
                int i = 0, suma = 0;

                nc = numberCells(item, row, col, dist);

                while ((i < 256) && (suma <= k * nc)) {
                    suma += histogram[row][i];
                    i++;
                }


                // Actualizar el histograma (eliminar columna y agregar la nueva columna)
                for (int j = -dist; j <= dist; j++) {
                    if ((row + j >= 0) && (row + j < item.getHeight()) && (col - dist >= 0) && (col - dist < item.getWidth())) {
                        histogram[row][item.getValueImage(row + j, col - dist)]--;
                    }

                    if ((row + j >= 0) && (row + j < item.getHeight()) && (col + dist + 1 >= 0) && (col + dist + 1 < item.getWidth())) {
                        histogram[row][item.getValueImage(row + j, col + dist + 1)]++;
                    }
                }


                aux.setValueImage(row, col, i - 1);
            }
        }

        return aux;
    }

}
