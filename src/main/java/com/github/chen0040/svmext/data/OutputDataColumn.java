package com.github.chen0040.svmext.data;


/**
 * Created by xschen on 5/5/2017.
 */
public class OutputDataColumn {
   private String columnName;
   private boolean categorical;

   public OutputDataColumn(){

   }

   public OutputDataColumn(String columnName, boolean categorical) {
      this.columnName = columnName;
      this.categorical = categorical;
   }

   public String getColumnName(){
      return columnName;
   }

   public boolean isCategorical(){
      return categorical;
   }


   public OutputDataColumn makeCopy() {
      return new OutputDataColumn(columnName, categorical);
   }
}
