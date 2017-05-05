package com.github.chen0040.data;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by xschen on 29/4/2017.
 */
public class InputDataColumn implements Serializable {

   private int sourceColumnIndex;
   private String columnName;
   private Set<Double> levels = new HashSet<>();

   public InputDataColumn(){

   }

   public InputDataColumn(String columnName) {
      this.columnName = columnName;
   }

   public InputDataColumn makeCopy() {
      InputDataColumn clone = new InputDataColumn();

      clone.copy(this);
      return clone;
   }

   public void copy(InputDataColumn that) {
      this.sourceColumnIndex = that.sourceColumnIndex;
      this.columnName = that.columnName;
      this.levels.clear();
      this.levels.addAll(that.levels);
   }


   public void setSourceColumnIndex(int key) {
      this.sourceColumnIndex = key;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public String getColumnName() {
      return columnName;
   }

   public void setLevels(Set<Double> set) {
      levels = set;
   }

   @Override
   public String toString(){
      return columnName;
   }


   public String summary() {
      return columnName + ":discrete=" + levels.size();
   }
}
