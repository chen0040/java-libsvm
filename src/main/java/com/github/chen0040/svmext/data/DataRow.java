package com.github.chen0040.svmext.data;


import java.util.List;


/**
 * Created by xschen on 28/4/2017.
 */
public interface DataRow {
   double target();
   String categoricalTarget();

   double[] toArray();

   void setCell(String columnName, double value);

   List<String> getColumnNames();

   List<String> getTargetColumnNames();

   List<String> getCategoricalTargetColumnNames();

   double getCell(String key);

   double getTargetCell(String columnName);

   String getCategoricalTargetCell(String columnName);

   void setTargetCell(String columnName, double value);

   void setCategoricalTargetCell(String columnName, String label);

   void setColumnNames(List<String> inputColumns);

   void setTargetColumnNames(List<String> outputColumns);

   void setCategoricalTargetColumnNames(List<String> outputColumns);

   DataRow makeCopy();

   void copy(DataRow that);

   String targetColumnName();

   String categoricalTargetColumnName();
}
