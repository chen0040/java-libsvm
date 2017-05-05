package com.github.chen0040.svmext.utils;

/**
 * Created by memeanalytics on 14/8/15.
 */
public class StdDev {
    public static double apply(double[] values, double mu){
        return Math.sqrt(Variance.apply(values, mu));
    }
}
