package com.unicen.app.algorithms;

import com.unicen.CapturedImage;

public class LeeRobustFilter extends AlgorithmFilter{

    public  CapturedImage execute(int [][] image){
        return null;
    }
    public CapturedImage leeRobustPercentileVersion(CapturedImage item, int radius, int perInf, int perSup) {
        int H = item.getHeight();
        int W = item.getWidth();

        CapturedImage aux = new CapturedImage(W, H, item.getDepth(), item.getChannels());

        int b = 256;
        int v;

        int nc = H * W;

        // Create global histogram
        int[] global_histogram = new int[b];
        // Inicializar global histogram con 0
        for (int i = 0; i < b; i++)
            global_histogram[i] = 0;

        // Inicializar global histogram con valores de la imagen
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                v = item.getValueImage(row, col);
                global_histogram[v]++;
            }
        }

        double pI = (double) perInf / 100;
        double pS = (double) perSup / 100;
        double pm = (pI + pS) / 2;

        int perInf_global = statisticOrder(global_histogram, nc, b, pI);
        int perSup_global = statisticOrder(global_histogram, nc, b, pS);
        double inter_Q_global = perSup_global - perInf_global;

        // Create local histogram
        int[][] local_histogram = new int[H][b];
        // inicializar local histogram con 0
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < b; col++) {
                local_histogram[row][col] = 0;
            }
        }

        // inicializar local histogram con valores inicales
        int icol = 0;
        for (int row = 0; row < H; row++) {
            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    if ((row + i >= 0) && ((icol + j >= 0) && (row + i < H) && (icol + j < W)))
                        local_histogram[row][item.getValueImage(row + i, icol + j)]++;
                }
            }
        }
        double y;
        // Algoritmo de Huang
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {

                nc = numberCells(item, row, col, radius);

                int perInf_local = statisticOrder(local_histogram, row, nc, b, pI);
                int median = statisticOrder(local_histogram, row, nc, b, pm);
                int perSup_local = statisticOrder(local_histogram, row, nc, b, pS);
                double inter_Q_local = perSup_local - perInf_local;

                // Actualizar histograma
                for (int i = -radius; i <= radius; i++) {
                    // Eliminado de columna
                    if ((row + i >= 0) && ((col - radius >= 0) && (row + i < H) && (col - radius < W)))
                        local_histogram[row][item.getValueImage(row + i, col - radius)]--;
                    // Agregando de columna
                    if ((row + i >= 0) && ((col + radius + 1 >= 0) && (row + i < H) && (col + radius + 1 < W)))
                        local_histogram[row][item.getValueImage(row + i, col + radius + 1)]++;
                }
                y = median + inter_Q_local / inter_Q_global * (item.getValueImage(row, col) - median);

                aux.setValueImage(row, col, (int) y);
            }
        }
        return aux;
    }

    private int statisticOrder(int[] histogram, int n, int b, double p) {
        double sum = 0;
        for (int i = 0; i < b; i++) {
            sum += histogram[i];
            if (sum >= n * p)
                return i;
        }
        return 0;
    }

    private int statisticOrder(int[][] histogram, int row, int n, int b, double p) {
        double sum = 0;
        for (int i = 0; i < b; i++) {
            sum += histogram[row][i];
            if (sum >= n * p)
                return i;
        }
        return 0;
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
