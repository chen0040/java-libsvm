package com.github.chen0040.svmext.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by xschen on 1/5/2017.
 * A data row consists two types of columns:
 *
 * column: a column represents an input column for which values are numeric
 * target column: a target column represents an output column for which values are numeric
 */
public class BasicDataRow implements DataRow {

   private final Map<String, Double> targets = new HashMap<>();
   private final Map<String, Double> values = new HashMap<>();
   private final List<String> columns = new ArrayList<>();
   private final List<String> targetColumns = new ArrayList<>();

   @Override public double target() {
      return getTargetCell(targetColumnName());
   }

   @Override
   public double getTargetCell(String columnName){
      return targets.getOrDefault(columnName, 0.0);
   }



   @Override
   public void setTargetCell(String columnName, double value) {
      if(value == 0.0) {
         targets.remove(columnName);
      }
      targets.put(columnName, value);
   }


   @Override public void setColumnNames(List<String> inputColumns) {
      columns.clear();
      columns.addAll(inputColumns);
   }


   @Override public void setTargetColumnNames(List<String> outputColumns) {
      targetColumns.clear();
      targetColumns.addAll(outputColumns);
   }


   @Override public DataRow makeCopy() {
      DataRow clone = new BasicDataRow();
      clone.copy(this);
      return clone;
   }


   @Override public void copy(DataRow that) {

      targets.clear();
      values.clear();
      columns.clear();
      targetColumns.clear();

      for(String c : that.getTargetColumnNames()){
         targets.put(c, that.getTargetCell(c));
      }

      for(String c : that.getColumnNames()) {
         values.put(c, that.getCell(c));
      }

      setColumnNames(that.getColumnNames());
      setTargetColumnNames(that.getTargetColumnNames());
   }


   @Override public String targetColumnName() {
      return getTargetColumnNames().get(0);
   }


   @Override public double[] toArray() {
      List<String> cols = getColumnNames();

      double[] result = new double[cols.size()];
      for(int i=0; i < cols.size(); ++i) {
         result[i] = getCell(cols.get(i));
      }
      return result;
   }

   private void buildColumns(){
      List<String> cols = values.keySet().stream().collect(Collectors.toList());
      cols.sort(String::compareTo);
      columns.addAll(cols);
   }

   private void buildTargetColumns(){
      List<String> cols = targets.keySet().stream().collect(Collectors.toList());
      cols.sort(String::compareTo);
      targetColumns.addAll(cols);
   }


   @Override public void setCell(String columnName, double value) {
      if(value == 0.0) {
         values.remove(columnName);
      }

      values.put(columnName, value);
   }


   @Override public List<String> getColumnNames() {
      if(columns.size() < values.size()) {
         buildColumns();
      }
      return columns;
   }

   @Override
   public List<String> getTargetColumnNames() {
      if(targetColumns.size() < targets.size()){
         buildTargetColumns();
      }
      return targetColumns;
   }


   @Override public double getCell(String key) {
      return values.getOrDefault(key, 0.0);
   }

   @Override
   public String toString(){
      StringBuilder sb = new StringBuilder();
      List<String> keys = getColumnNames();
      for(int i=0; i < keys.size(); ++i){
         if(i != 0){
            sb.append(", ");
         }
         sb.append(keys.get(i)).append(":").append(getCell(keys.get(i)));
      }
      sb.append(" => ");
      keys = getTargetColumnNames();
      for(int i=0; i < keys.size(); ++i){
         if(i != 0){
            sb.append(", ");
         }
         sb.append(keys.get(i)).append(":").append(getTargetCell(keys.get(i)));
      }
      return sb.toString();
   }
}
