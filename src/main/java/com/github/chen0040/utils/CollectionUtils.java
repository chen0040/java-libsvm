package com.github.chen0040.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Created by xschen on 1/5/2017.
 */
public class CollectionUtils {
   public static <T> List<T> clone(List<T> that, Function<T, T> transformer) {
      List<T> result = new ArrayList<>();
      for(int i=0; i < that.size(); ++i){
         result.add(transformer.apply(that.get(i)));
      }
      return result;
   }


   public static <T> List<T> toList(T[] that, Function<T, T> transformer) {
      List<T> result = new ArrayList<>();
      for(int i=0; i < that.length; ++i){
         result.add(transformer.apply(that[i]));
      }
      return result;
   }

   public static List<Double> toList(double[] that) {
      List<Double> result = new ArrayList<>();
      for(int i=0; i < that.length; ++i){
         result.add(that[i]);
      }
      return result;
   }
}
