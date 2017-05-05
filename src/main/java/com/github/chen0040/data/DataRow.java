package com.github.chen0040.data;


import java.util.List;


/**
 * Created by xschen on 28/4/2017.
 */
public interface DataRow {
   double target();

   double[] toArray();

   void setCell(String columnName, double value);

   List<String> getColumnNames();

   List<String> getTargetColumnNames();

   double getCell(String key);

   double getTargetCell(String columnName);

   void setTargetCell(String columnName, double value);

   void setColumnNames(List<String> inputColumns);

   void setTargetColumnNames(List<String> outputColumns);
}
