package com.github.chen0040.utils;


/**
 * Created by xschen on 4/5/2017.
 */
public class TupleTwo<T, T2> {
   private final T v1;
   private final T2 v2;

   public TupleTwo(T v1, T2 v2) {
      this.v1 = v1;
      this.v2 = v2;
   }

   public T _1(){
      return v1;
   }

   public T2 _2(){
      return v2;
   }
}
