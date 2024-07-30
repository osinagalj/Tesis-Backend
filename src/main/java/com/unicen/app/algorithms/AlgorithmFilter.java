package com.unicen.app.algorithms;

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
}
