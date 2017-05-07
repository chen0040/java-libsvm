package com.github.chen0040.svmext.data;

import com.github.chen0040.svmext.utils.TupleTwo;
import org.assertj.core.api.AssertionsForClassTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Created by xschen on 7/5/2017.
 */
public class BasicDataFrameUnitTest {

   private static final Logger logger = LoggerFactory.getLogger(BasicDataFrameUnitTest.class);

   @Test
   public void test_add_row(){
      DataFrame dataFrame = new BasicDataFrame();
      DataRow row = dataFrame.newRow();

      assertThat(dataFrame.rowCount()).isEqualTo(0);
      assertThat(dataFrame.isLocked()).isFalse();

      row.setCell("input1", 1.0);
      row.setCell("input2", 2.0);
      row.setTargetCell("output", 2.0);

      dataFrame.addRow(row);

      assertThat(dataFrame.rowCount()).isEqualTo(1);

      row = row.makeCopy();
      dataFrame.addRow(row);

      assertThat(dataFrame.rowCount()).isEqualTo(2);

      row = dataFrame.newRow();

      row.setCell("input3", 3.0);
      row.setCell("input1", 1.0);
      row.setCategoricalTargetCell("output2", "Hello");

      dataFrame.addRow(row);
      assertThat(dataFrame.rowCount()).isEqualTo(3);

      assertThat(dataFrame.getInputColumns()).size().isEqualTo(0);
      assertThat(dataFrame.getOutputColumns()).size().isEqualTo(0);

      dataFrame.lock();

      assertThat(dataFrame.getInputColumns()).size().isEqualTo(3);
      assertThat(dataFrame.getOutputColumns()).size().isEqualTo(2);
      assertThat(dataFrame.getInputColumns().get(0).getColumnName()).isEqualTo("input1");
      assertThat(dataFrame.getInputColumns().get(1).getColumnName()).isEqualTo("input2");
      assertThat(dataFrame.getInputColumns().get(2).getColumnName()).isEqualTo("input3");
      assertThat(dataFrame.getOutputColumns().get(0).getColumnName()).isEqualTo("output");
      assertThat(dataFrame.getOutputColumns().get(1).getColumnName()).isEqualTo("output2");
      assertThat(dataFrame.getOutputColumns().get(0).isCategorical()).isEqualTo(false);
      assertThat(dataFrame.getOutputColumns().get(1).isCategorical()).isEqualTo(true);
      assertThat(dataFrame.isLocked()).isTrue();

      for(int i=0; i < dataFrame.rowCount(); ++i) {
         row = dataFrame.row(i);
         for (InputDataColumn inputColumn : dataFrame.getInputColumns()) {
            logger.info("rows[{}][\"{}\"] = {}",
                    i,
                    inputColumn.getColumnName(),
                    row.getCell(inputColumn.getColumnName()));
         }
         for (OutputDataColumn outputColumn : dataFrame.getOutputColumns()) {
            logger.info("rows[{}].outputs[\"{}\"] = {}",
                    i,
                    outputColumn.getColumnName(),
                    outputColumn.isCategorical() ? row.getCategoricalTargetCell(outputColumn.getColumnName()) : row.getCell(outputColumn.getColumnName()));
         }

      }

      logger.info("head: {}", dataFrame.head(2));

      dataFrame.stream().forEach(r -> logger.info("row: {}", r));
      for(DataRow r : dataFrame) {
         logger.info("row: {}", r);
      }

   }

   @Test
   public void test_shuffle_split(){
      DataFrame dataFrame = new BasicDataFrame();
      DataRow row = dataFrame.newRow();

      assertThat(dataFrame.rowCount()).isEqualTo(0);
      assertThat(dataFrame.isLocked()).isFalse();

      row.setCell("input1", 1.0);
      row.setCell("input2", 2.0);
      row.setTargetCell("output", 2.0);

      dataFrame.addRow(row);

      assertThat(dataFrame.rowCount()).isEqualTo(1);

      assertThat(dataFrame.isLocked());

      row = dataFrame.newRow();

      row.setCell("input3", 3.0);
      row.setCell("input1", 1.0);
      row.setCategoricalTargetCell("output2", "Hello");

      dataFrame.addRow(row);

      dataFrame.lock();

      dataFrame.shuffle();

      TupleTwo<DataFrame, DataFrame> miniFrames = dataFrame.split(0.5);

      AssertionsForClassTypes.assertThat(miniFrames._1().rowCount()).isEqualTo(1);
      AssertionsForClassTypes.assertThat(miniFrames._2().rowCount()).isEqualTo(1);

   }

   private DataFrame create(){
      DataFrame dataFrame = new BasicDataFrame();
      DataRow row = dataFrame.newRow();

      assertThat(dataFrame.rowCount()).isEqualTo(0);
      assertThat(dataFrame.isLocked()).isFalse();

      row.setCell("input1", 1.0);
      row.setCell("input2", 2.0);
      row.setTargetCell("output", 2.0);

      dataFrame.addRow(row);

      assertThat(dataFrame.rowCount()).isEqualTo(1);

      return dataFrame;
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void test_add_row_after_locked_exception(){

      DataFrame dataFrame = create();

      dataFrame.lock();

      assertThat(dataFrame.isLocked());

      DataRow row = dataFrame.newRow();

      row.setCell("input3", 3.0);
      row.setCell("input1", 1.0);
      row.setCategoricalTargetCell("output2", "Hello");


      dataFrame.addRow(row);
   }

   @Test
   public void test_add_row_after_unlocked(){

      DataFrame dataFrame = create();

      dataFrame.lock();

      assertThat(dataFrame.isLocked());

      dataFrame.unlock();

      DataRow row = dataFrame.newRow();

      row.setCell("input3", 3.0);
      row.setCell("input1", 1.0);
      row.setCategoricalTargetCell("output2", "Hello");


      dataFrame.addRow(row);

      assertThat(dataFrame.rowCount()).isEqualTo(2);
   }
}
