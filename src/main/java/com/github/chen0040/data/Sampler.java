package com.github.chen0040.data;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


/**
 * Created by xschen on 5/5/2017.
 */
public class Sampler {

   public Sampler(){

   }

   public ColumnBuilder forColumn(String column){
      return new SampleBuilder().forColumn(column);
   }

   public interface RowBuilder {
      ColumnBuilder forColumn(String columnName);
      DataSampleBuilder end();
   }

   public interface ColumnBuilder {
      RowBuilder generate(BiFunction<String, Integer, Double> generator);
   }

   public interface DataSampleBuilder {
      DataFrame sample(DataFrame dataFrame, int count);
   }

   private static class SampleBuilder implements RowBuilder, ColumnBuilder, DataSampleBuilder {

      private int count;
      private String currentColumnName;

      private Map<String, BiFunction<String, Integer, Double>> generators = new HashMap<>();

      public SampleBuilder() {

      }


      @Override public RowBuilder generate(BiFunction<String, Integer, Double> generator) {
         generators.put(currentColumnName, generator);
         return this;
      }


      @Override public ColumnBuilder forColumn(String columnName) {
         currentColumnName = columnName;
         return this;
      }


      @Override public DataSampleBuilder end() {
         return this;
      }


      @Override public DataFrame sample(DataFrame dataFrame, int count) {
         if(generators.isEmpty()) {
            throw new RuntimeException("No column generators are designed to build a row!");
         }

         this.count = count;
         currentColumnName = null;
         dataFrame.unlock();
         Set<String> outputColumns = dataFrame.getOutputColumns().stream().map(OutputDataColumn::getColumnName).collect(Collectors.toSet());

         for(int i=0; i < count; ++i) {
            DataRow newRow = dataFrame.newRow();
            for(Map.Entry<String, BiFunction<String, Integer, Double>> entry : generators.entrySet()) {
               String columnName = entry.getKey();
               BiFunction<String, Integer, Double> generator = entry.getValue();
               if(outputColumns.contains(columnName)){
                  newRow.setTargetCell(columnName, generator.apply(columnName, i));
               } else {
                  newRow.setCell(columnName, generator.apply(columnName, i));
               }
            }

            dataFrame.addRow(newRow);
         }
         dataFrame.lock();

         return dataFrame;
      }
   }
}
