package com.github.chen0040.svmext.data;


import java.util.List;
import java.util.stream.Stream;


/**
 * Created by xschen on 28/4/2017.
 */
public interface DataFrame extends Iterable<DataRow> {
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

   void shuffle();

   Stream<DataRow> stream();
}
