package com.unicen;

import com.unicen.app.algorithms.LeeFilter;
import org.opencv.core.Mat;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Test algoritmos");

        LeeFilter leeFilter = new LeeFilter();
        leeFilter.execute();

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


        // Function returns the rank vector of the set of observations
        public double[] rankify(double[] array) {
            int size = array.length;
            double[] rank = new double[size];

            for (int i = 0; i < size; i++) {
                int r = 1, s = 1;

                // Count no of smaller elements in 0 to i-1
                for (int j = 0; j < i; j++) {
                    if (array[j] < array[i])
                        r++;
                    if (array[j] == array[i])
                        s++;
                }

                // Count no of smaller elements in i+1 to N-1
                for (int j = i + 1; j < size; j++) {
                    if (array[j] < array[i])
                        r++;
                    if (array[j] == array[i])
                        s++;
                }

                // Use Fractional Rank formula fractional_rank = r + (n-1)/2
                rank[i] = r + (s - 1) * 0.5;
            }

            return rank;
        }

        public double getPearsonCoefficient(double[][] im1, double[][] im2) {
            int H = im1.length;
            int W = im1[0].length;
            int size = H * W;

            double[] X = new double[size];
            double[] Y = new double[size];

            int pos = 0;
            for (int row = 0; row < H; row++) {
                for (int col = 0; col < W; col++) {
                    X[pos] = im1[row][col];
                    Y[pos] = im2[row][col];
                    pos++;
                }
            }

            // Get ranks of vector X
            double[] rank_x = rankify(X);
            // Get ranks of vector y
            double[] rank_y = rankify(Y);

            double sum_X = 0, sum_Y = 0, sum_XY = 0, squareSum_X = 0, squareSum_Y = 0;
            for (int i = 0; i < size; i++) {
                // sum of elements of array X.
                sum_X = sum_X + rank_x[i];
                // sum of elements of array Y.
                sum_Y = sum_Y + rank_y[i];
                // sum of X[i] * Y[i].
                sum_XY = sum_XY + rank_x[i] * rank_y[i];
                // sum of square of array elements.
                squareSum_X = squareSum_X + rank_x[i] * rank_x[i];
                squareSum_Y = squareSum_Y + rank_y[i] * rank_y[i];
            }

            // use formula for calculating correlation coefficient.
            double corr = (size * sum_XY - sum_X * sum_Y) / (Math.sqrt((size * squareSum_X - sum_X * sum_X) * (size * squareSum_Y - sum_Y * sum_Y)));

            return corr;
        }


        public CapturedImage convert24to8bits(Mat image) {
            CapturedImage item = new CapturedImage(image.cols(), image.rows(), 8, 1);

            for (int row = 0; row < item.getHeight(); row++) {
                for (int col = 0; col < item.getWidth(); col++) {
                    double[] pixelValues = image.get(row, col);
                    int value = (int) Math.round((pixelValues[0] + pixelValues[1] + pixelValues[2]) / 3);
                    item.setValueImage(row, col, value);
                }
            }

            return item;
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

        public  Integer CV_32S = 2; //TODO
        //TODO do it for me C++ to Java





    /***********************************************************************************************
     *
     *                                                AUXILIARY
     *
     ***********************************************************************************************/
    /*
    public int numberCells(CapturedImage item, int row, int col, int dist) {
        int h = item.getHeight();
        int w = item.getWidth();

        if (row >= dist && col >= dist && row < h - dist && col < w - dist)
            return (2 * dist + 1) * (2 * dist + 1);
        else if (row <= dist && col <= dist)
            return (dist + 1) * (dist + 1) + (dist + 1) * col + (dist + 1) * row + (row * col);
        else if (row >= h - dist - 1 && col >= w - dist - 1)
            return (dist + 1) * (dist + 1) + (dist + 1) * (w - 1 - col) + (dist + 1) * (h - 1 - row) + ((h - 1 - row) * (w - 1 - col));
        else if (row <= dist && col >= w - dist - 1)
            return (dist + 1) * (dist + 1) + (dist + 1) * (w - 1 - col) + (dist + 1) * row + (row * (w - 1 - col));
        else if (row >= h - dist - 1 && col <= dist)
            return (dist + 1) * (dist + 1) + (dist + 1) * col + (dist + 1) * (h - 1 - row) + ((h - 1 - row) * col);
        else if (row <= dist && col >= dist && col < w - dist)
            return (dist * 2 + 1) * (dist * 2 + 1) - (dist * 2 + 1) * (dist - row);
        else if (row > dist && col > dist && col < w - dist)
            return (dist * 2 + 1) * (dist * 2 + 1) - ((dist * 2 + 1) * (dist - h + 1 + row));
        else if (col <= dist && row >= dist && col < dist)
            return (dist * 2 + 1) * (dist * 2 + 1) - (dist * 2 + 1) * (dist - col);
        else if (col >= w - dist && row > dist && row < h - dist)
            return (dist * 2 + 1) * (dist * 2 + 1) - ((dist * 2 + 1) * (dist - w + 1 + col));
        else
            return (2 * dist + 1) * (2 * dist + 1);
    }

    public int statisticOrder(int[] histogram, int nc, int b, double k) {
        int i = 0;
        int sum = 0;
        while ((i < b) && (sum <= k * nc)) {
            sum += histogram[i];
            i = i + 1;
        }
        return i - 1;
    }

    public int statisticOrder(int[][] histogram, int row, int nc, int b, double k) {
        int i = 0;
        int sum = 0;
        while ((i < b) && (sum <= k * nc)) {
            sum += histogram[row][i];
            i = i + 1;
        }
        return i - 1;
    }


*/



}
