package com.github.chen0040.utils;


/**
 * Created by xschen on 1/5/2017.
 */
public class StringUtils {
   public static double parseDouble(String text) {
      try {
         return Double.parseDouble(text);
      } catch(NumberFormatException ex) {
         return 0;
      }
   }

   public static String stripQuote(String sentence){
      if(sentence.startsWith("\"") && sentence.endsWith("\"")){
         return sentence.substring(1, sentence.length()-1);
      }
      return sentence;
   }


   public static boolean isEmpty(String line) {
      return line == null || line.equals("");
   }
}
