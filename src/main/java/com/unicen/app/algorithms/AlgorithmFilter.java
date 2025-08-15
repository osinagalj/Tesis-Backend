package com.unicen.app.algorithms;

import com.unicen.CapturedImage;
import com.unicen.app.model.Algorithm;

public abstract class AlgorithmFilter {

    public static AlgorithmFilter of(Algorithm algorithm) {
        switch (algorithm) {
            case ROBUST_LEE:
                return new LeeRobustFilter();
            case MEDIAN:
                return new MedianFilterHuang();
        }
        return null;
    }

    protected static int statisticOrder(int[] histogram, int n, int b, double p) {
        double sum = 0;
        for (int i = 0; i < b; i++) {
            sum += histogram[i];
            if (sum >= n * p)
                return i;
        }
        return 0;
    }

    protected static int statisticOrder(int[][] histogram, int row, int n, int b, double p) {
        double sum = 0;
        for (int i = 0; i < b; i++) {
            sum += histogram[row][i];
            if (sum >= n * p)
                return i;
        }
        return 0;
    }

    protected static int numberCells(CapturedImage item, int row, int col, int radius) {
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
