package com.unicen.app.algorithms;

import com.unicen.CapturedImage;

public class LeeRobustFilter extends AlgorithmFilter{

    public static CapturedImage execute(CapturedImage item, int radius) {
        int H = item.getHeight();
        int W = item.getWidth();

        CapturedImage aux = new CapturedImage(H, W, item.getDepth(), item.getChannels());

        int b = 256; // Rango de valores en el histograma
        int v;
        int nc = H * W;

        // Crear el histograma global
        int[] globalHistogram = new int[b];
        for (int i = 0; i < b; i++) {
            globalHistogram[i] = 0;
        }

        // Inicializar el histograma global con valores de la imagen
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                v = item.getValueImage(row, col);
                globalHistogram[v]++;
            }
        }

        int q1Global = statisticOrder(globalHistogram, nc, b, 0.25);
        int q3Global = statisticOrder(globalHistogram, nc, b, 0.75);
        double interQGlobal = q3Global - q1Global;

        // Crear el histograma local
        int[][] localHistogram = new int[H][b];
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < b; col++) {
                localHistogram[row][col] = 0;
            }
        }

        int icol = 0;

        // Llenar el histograma local con valores iniciales
        for (int row = 0; row < H; row++) {
            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    if ((row + i >= 0) && ((0 <= (j + icol)) && (row + i < H) && (icol + j < W)))
                        localHistogram[row][item.getValueImage(row + i, icol + j)]++;
                }
            }
        }

        double y;

        // Algoritmo de Huang
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                nc = numberCells(item, row, col, radius);

                int q1Local = statisticOrder(localHistogram, row, nc, b, 0.25);
                int median = statisticOrder(localHistogram, row, nc, b, 0.5);
                int q3Local = statisticOrder(localHistogram, row, nc, b, 0.75);
                double interQLocal = q3Local - q1Local;

                // Actualizar el histograma
                for (int i = -radius; i <= radius; i++) {
                    if ((row + i >= 0) && (col - radius >= 0) && (row + i < H) && (col - radius < W))
                        localHistogram[row][item.getValueImage(row + i, col - radius)]--;

                    if ((row + i >= 0) && (col + radius + 1 >= 0) && (row + i < H) && (col + radius + 1 < W))
                        localHistogram[row][item.getValueImage(row + i, col + radius + 1)]++;
                }

                y = median + interQLocal / interQGlobal * (item.getValueImage(row, col) - median);

                aux.setValueImage(row, col, (int) y);
            }
        }

        return aux;
    }



}
