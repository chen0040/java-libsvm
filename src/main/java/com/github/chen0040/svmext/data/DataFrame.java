package com.github.chen0040.svmext.data;


import java.util.List;


/**
 * Created by xschen on 28/4/2017.
 */
public interface DataFrame {
   int rowCount();

   DataRow row(int i);

   List<InputDataColumn> getInputColumns();

   List<OutputDataColumn> getOutputColumns();

   void unlock();

   boolean isLocked();

   void lock();

   DataRow newRow();

   void addRow(DataRow row);

   String head(int limit);
}
