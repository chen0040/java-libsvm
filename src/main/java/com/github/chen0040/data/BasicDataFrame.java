package com.github.chen0040.data;


import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by xschen on 1/5/2017.
 */
public class BasicDataFrame implements DataFrame {

   private final List<DataRow> rows = new ArrayList<>();
   private final List<InputDataColumn> inputDataColumns = new ArrayList<>();
   private final List<OutputDataColumn> outputDataColumns = new ArrayList<>();
   private boolean locked = false;

   @Override public int rowCount() {
      return rows.size();
   }


   @Override public DataRow row(int i) {
      return rows.get(i);
   }


   @Override public List<InputDataColumn> getInputColumns() {
      return inputDataColumns;
   }


   @Override public List<OutputDataColumn> getOutputColumns() {
      return outputDataColumns;
   }


   @Override public void unlock(){
      locked = false;
   }

   @Override
   public boolean isLocked() {
      return locked;
   }


   @Override public void lock() {
      Map<String, Set<Double>> counts = new HashMap<>();
      Set<String> outputs = new HashSet<>();
      for(DataRow row : rows){
         List<String> keys = row.getColumnNames();
         for(String key: keys) {
            Set<Double> set;

            if(counts.containsKey(key)){
               set = counts.get(key);
            } else {
               set = new HashSet<>();
               counts.put(key, set);
            }

            set.add(row.getCell(key));
         }
         outputs.addAll(row.getTargetColumnNames());
      }

      inputDataColumns.clear();
      for(Map.Entry<String, Set<Double>> entry : counts.entrySet()){
         Set<Double> set = entry.getValue();
         InputDataColumn inputDataColumn = new InputDataColumn();
         inputDataColumn.setColumnName(entry.getKey());
         if(set.size() < rowCount() / 3) {
            inputDataColumn.setLevels(set);
         }
         inputDataColumns.add(inputDataColumn);
      }

      outputDataColumns.clear();
      for(String o : outputs) {
         outputDataColumns.add(new OutputDataColumn(o));
      }

      inputDataColumns.sort((a, b) -> a.getColumnName().compareTo(b.getColumnName()));
      outputDataColumns.sort((a, b) -> a.getColumnName().compareTo(b.getColumnName()));

      List<String> inputColumns = inputDataColumns.stream().map(InputDataColumn::getColumnName).collect(Collectors.toList());
      List<String> outputColumns = outputDataColumns.stream().map(OutputDataColumn::getColumnName).collect(Collectors.toList());

      inputColumns.sort(String::compareTo);
      outputColumns.sort(String::compareTo);

      for(int i=0; i < rowCount(); ++i) {
         DataRow row = row(i);
         row.setColumnNames(inputColumns);
         row.setTargetColumnNames(outputColumns);
      }

      locked = true;
   }


   @Override public DataRow newRow() {
      return new BasicDataRow();
   }


   @Override public void addRow(DataRow row) {
      if(locked) {
         throw new RuntimeException("Data frame is currently locked, please unlock first");
      }
      rows.add(row);
   }


   @Override public String head(int limit) {
      StringBuilder sb = new StringBuilder();
      int max = Math.min(limit, rowCount());
      for(int i=0; i < max; ++i) {
         if(i != 0){
            sb.append("\n");
         }
         sb.append(row(i));
      }
      return sb.toString();
   }

}
