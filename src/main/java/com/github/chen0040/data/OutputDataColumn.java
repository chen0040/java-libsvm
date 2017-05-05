package com.github.chen0040.data;


/**
 * Created by xschen on 5/5/2017.
 */
public class OutputDataColumn {
   private String columnName;

   public OutputDataColumn(){

   }

   public OutputDataColumn(String columnName) {
      this.columnName = columnName;
   }

   public String getColumnName(){
      return columnName;
   }
}
