package com.github.chen0040.utils;


/**
 * Created by xschen on 1/5/2017.
 */
public class NumberUtils {
   public static int toInt(double value){
      return (int)value;
   }

   public static boolean isZero(Double val) {
      return Math.abs(val) < 0.0000000000000000000001;
   }

}
