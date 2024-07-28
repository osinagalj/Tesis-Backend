package com.ecofy.app.algorithms;

import com.ecofy.CapturedImage;

public class MedianFilterHuang extends AlgorithmFilter{

    public  CapturedImage execute(int [][] image){
        return null;
    }
    public CapturedImage medianFilterHuang(CapturedImage item, double k, int dist) {
        // Create a new CapturedImage instance
        CapturedImage aux = new CapturedImage(item.getWidth(), item.getHeight(), item.getDepth(), item.getChannels());

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
                    if (isValidCoordinate(row + i, icol + j, item.getHeight(), item.getWidth())) {
                        histogram[row][item.getValueImage(row + i, icol + j)]++;
                    }
                }
            }
        }

        // Huang Algorithm
        for (int row = 0; row < item.getHeight(); row++) {
            for (int col = 0; col < item.getWidth(); col++) {
                // Calculate median
                int i = 0;
                int suma = 0;
                int nc = numberCells(item, row, col, dist);

                while (i < 256 && suma <= k * nc) {
                    suma += histogram[row][i];
                    i++;
                }

                // Update histogram
                for (int j = -dist; j <= dist; j++) {
                    // Removing column
                    if (isValidCoordinate(row + j, col - dist, item.getHeight(), item.getWidth())) {
                        histogram[row][item.getValueImage(row + j, col - dist)]--;
                    }
                    // Adding column
                    if (isValidCoordinate(row + j, col + dist + 1, item.getHeight(), item.getWidth())) {
                        histogram[row][item.getValueImage(row + j, col + dist + 1)]++;
                    }
                }

                aux.setValueImage(row, col, i - 1);
            }
        }
        return aux;
    }

    private boolean isValidCoordinate(int row, int col, int height, int width) {
        return row >= 0 && col >= 0 && row < height && col < width;
    }

    private int numberCells(CapturedImage item, int row, int col, int radius) {
        int count = 0;
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if ((row + i >= 0) && ((col + j >= 0) && (row + i < item.getHeight()) && (col + j < item.getWidth())))
                    count++;
            }
        }
        return count;
    }

}
