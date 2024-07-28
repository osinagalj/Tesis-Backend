package com.ecofy.app.algorithms;

import com.ecofy.CapturedImage;
import com.ecofy.app.model.Algorithm;

public abstract class AlgorithmFilter {

    public static AlgorithmFilter of(Algorithm algorithm) {
        switch (algorithm) {
            case LEE:
                return new LeeFilter();
            case ROBUST_LEE:
                return new LeeRobustFilter();
            case MEDIAN:
                return new MedianFilterHuang();
        }
        return null;
    }

    public abstract CapturedImage execute(int [][] image);
}
