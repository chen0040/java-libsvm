package com.github.chen0040.svmext.utils;


import com.github.chen0040.svmext.data.DataFrame;
import com.github.chen0040.svmext.data.DataRow;
import com.github.chen0040.svmext.data.InputDataColumn;
import com.github.chen0040.svmext.data.OutputDataColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by xschen on 5/5/2017.
 */
public class Scaler {
   private Map<String, Double> means = new HashMap<>();
   private Map<String, Double> sds = new HashMap<>();

   public Scaler makeCopy() {
      Scaler clone = new Scaler();
      clone.copy(this);
      return clone;
   }

   public void copy(Scaler that) {
      means.clear();
      sds.clear();
      means.putAll(that.means);
      sds.putAll(that.sds);
   }


   public void fit(DataFrame frame) {

      means.clear();
      sds.clear();

      List<String> inputColumns = frame.getInputColumns().stream().map(InputDataColumn::getColumnName).collect(Collectors.toList());
      List<String> outputColumns = frame.getOutputColumns().stream().map(OutputDataColumn::getColumnName).collect(Collectors.toList());


      for(String c : inputColumns){

         double[] values = new double[frame.rowCount()];
         for(int i=0; i < frame.rowCount(); ++i){
            double value = frame.row(i).getCell(c);
            values[i] = value;
         }

         double mean = Mean.apply(values);
         means.put(c, mean);

         double sd = StdDev.apply(values, mean);
         sds.put(c, sd);
      }

      for(String c : outputColumns){

         double[] values = new double[frame.rowCount()];
         for(int i=0; i < frame.rowCount(); ++i){
            double value = frame.row(i).getTargetCell(c);
            values[i] = value;
         }

         double mean = Mean.apply(values);
         means.put(c, mean);

         double sd = StdDev.apply(values, mean);
         sds.put(c, sd);
      }
   }

   public double transform(String columnName, double value) {
      double mean = means.getOrDefault(columnName, 0.0);
      double sd = sds.getOrDefault(columnName, 0.0);

      if(sd != 0){
         return (value - mean) / sd;
      } else {
         return value;
      }
   }

   public double inverseTransform(String columnName, double value) {
      double mean = means.getOrDefault(columnName, 0.0);
      double sd = sds.getOrDefault(columnName, 0.0);

      if(sd != 0){
         return value * sd + mean;
      } else {
         return value;
      }
   }

   public DataRow transform(DataRow row) {
      DataRow scaled = row.makeCopy();
      List<String> inputColumns = scaled.getColumnNames();
      for(String c : inputColumns){
         scaled.setCell(c, transform(c, scaled.getCell(c)));
      }

      List<String> outputColumns = scaled.getTargetColumnNames();
      for(String c : outputColumns) {
         scaled.setTargetCell(c, transform(c, scaled.getTargetCell(c)));
      }

      return scaled;
   }

   public DataRow inverseTransform(DataRow row) {
      DataRow scaled = row.makeCopy();
      List<String> inputColumns = scaled.getColumnNames();
      for(String c : inputColumns){
         scaled.setCell(c, inverseTransform(c, scaled.getCell(c)));
      }

      List<String> outputColumns = scaled.getTargetColumnNames();
      for(String c : outputColumns) {
         scaled.setTargetCell(c, inverseTransform(c, scaled.getTargetCell(c)));
      }

      return scaled;
   }
}
