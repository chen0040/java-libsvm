package com.github.chen0040.svmext.data;


import org.assertj.core.api.AssertionsForClassTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.testng.Assert.*;


/**
 * Created by xschen on 7/5/2017.
 */
public class BasicDataRowUnitTest {
   private static final Logger logger = LoggerFactory.getLogger(BasicDataRowUnitTest.class);

   @Test
   public void test_row_with_inputs(){
      DataRow row = new BasicDataRow();

      row.setCell("input1", 1.0);
      row.setCell("input2", 2.0);
      row.setCell("input3", 0.0);

      assertThat(row.getColumnNames()).size().isEqualTo(3);
      AssertionsForClassTypes.assertThat(row.getCell("input1")).isEqualTo(1.0);
      AssertionsForClassTypes.assertThat(row.getCell("input2")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row.getCell("input3")).isEqualTo(0.0);

      AssertionsForClassTypes.assertThat(row.toArray()).contains(1.0, 0);
      AssertionsForClassTypes.assertThat(row.toArray()).contains(2.0, 1);
      AssertionsForClassTypes.assertThat(row.toArray()).contains(0.0, 2);
   }

   @Test
   public void test_row_set_column_names(){
      DataRow row = new BasicDataRow();

      row.setCell("input1", 1.0);
      row.setCell("input2", 2.0);

      row.setColumnNames(Arrays.asList("input1", "input2", "inputX"));

      assertThat(row.getColumnNames()).size().isEqualTo(3);
      AssertionsForClassTypes.assertThat(row.getCell("input1")).isEqualTo(1.0);
      AssertionsForClassTypes.assertThat(row.getCell("input2")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row.getCell("inputX")).isEqualTo(0.0);
   }

   @Test
   public void test_row_set_target_column_names(){
      DataRow row = new BasicDataRow();

      row.setTargetCell("output1", 1.0);
      row.setTargetCell("output2", 2.0);

      row.setTargetColumnNames(Arrays.asList("output1", "output2", "outputX"));

      assertThat(row.getTargetColumnNames()).size().isEqualTo(3);
      AssertionsForClassTypes.assertThat(row.getTargetCell("output1")).isEqualTo(1.0);
      AssertionsForClassTypes.assertThat(row.getTargetCell("output2")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row.getTargetCell("outputX")).isEqualTo(0.0);
   }

   @Test
   public void test_row_set_categorical_target_column_names(){
      DataRow row = new BasicDataRow();

      row.setCategoricalTargetCell("output1", "Hello");
      row.setCategoricalTargetCell("output2", "World");

      row.setCategoricalTargetColumnNames(Arrays.asList("output1", "output2", "outputX"));

      assertThat(row.getCategoricalTargetColumnNames()).size().isEqualTo(3);
      AssertionsForClassTypes.assertThat(row.getCategoricalTargetCell("output1")).isEqualTo("Hello");
      AssertionsForClassTypes.assertThat(row.getCategoricalTargetCell("output2")).isEqualTo("World");
      AssertionsForClassTypes.assertThat(row.getCategoricalTargetCell("outputX")).isEqualTo("");
   }

   @Test
   public void test_row_with_single_numerical_target(){
      DataRow row = new BasicDataRow();

      row.setTargetCell("numericalOutput", 2.0);

      assertThat(row.getTargetColumnNames()).size().isEqualTo(1);
      assertThat(row.getCategoricalTargetColumnNames()).size().isEqualTo(0);
      assertThat(row.getTargetColumnNames()).contains("numericalOutput");
      AssertionsForClassTypes.assertThat(row.getTargetCell("numericalOutput")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row.target()).isEqualTo(2.0);
   }

   @Test
   public void test_row_with_two_numerical_outputs(){
      DataRow row = new BasicDataRow();

      row.setTargetCell("output1", 2.0);
      row.setTargetCell("output2", 1.0);

      assertThat(row.getTargetColumnNames()).size().isEqualTo(2);
      assertThat(row.getCategoricalTargetColumnNames()).size().isEqualTo(0);
      assertThat(row.getTargetColumnNames()).contains("output1");
      assertThat(row.getTargetColumnNames()).contains("output2");
      AssertionsForClassTypes.assertThat(row.getTargetCell("output1")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row.getTargetCell("output2")).isEqualTo(1.0);
      AssertionsForClassTypes.assertThat(row.target()).isEqualTo(2.0);
   }

   @Test
   public void test_row_with_single_categorical_target(){
      DataRow row = new BasicDataRow();

      row.setCategoricalTargetCell("categoricalOutput", "Hello");

      assertThat(row.getTargetColumnNames()).size().isEqualTo(0);
      assertThat(row.getCategoricalTargetColumnNames()).size().isEqualTo(1);
      assertThat(row.getCategoricalTargetColumnNames()).contains("categoricalOutput");
      AssertionsForClassTypes.assertThat(row.getCategoricalTargetCell("categoricalOutput")).isEqualTo("Hello");
      AssertionsForClassTypes.assertThat(row.categoricalTarget()).isEqualTo("Hello");
   }

   @Test
   public void test_row_with_two_categorical_outputs(){
      DataRow row = new BasicDataRow();

      row.setCategoricalTargetCell("output1", "Hello");
      row.setCategoricalTargetCell("output2", "World");

      assertThat(row.getTargetColumnNames()).size().isEqualTo(0);
      assertThat(row.getCategoricalTargetColumnNames()).size().isEqualTo(2);
      assertThat(row.getCategoricalTargetColumnNames()).contains("output1");
      assertThat(row.getCategoricalTargetColumnNames()).contains("output2");
      AssertionsForClassTypes.assertThat(row.getCategoricalTargetCell("output1")).isEqualTo("Hello");
      AssertionsForClassTypes.assertThat(row.getCategoricalTargetCell("output2")).isEqualTo("World");
      AssertionsForClassTypes.assertThat(row.categoricalTarget()).isEqualTo("Hello");
   }

   @Test
   public void test_makeCopy(){
      DataRow row = new BasicDataRow();

      row.setCell("input1", 1.0);
      row.setCell("input2", 2.0);
      row.setCell("input3", 0.0);

      row.setTargetCell("output1", 2.0);
      row.setTargetCell("output2", 1.0);

      row.setCategoricalTargetCell("output1", "Hello");
      row.setCategoricalTargetCell("output2", "World");

      row = row.makeCopy();

      assertThat(row.getColumnNames()).size().isEqualTo(3);
      AssertionsForClassTypes.assertThat(row.getCell("input1")).isEqualTo(1.0);
      AssertionsForClassTypes.assertThat(row.getCell("input2")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row.getCell("input3")).isEqualTo(0.0);


      assertThat(row.getCategoricalTargetColumnNames()).size().isEqualTo(2);
      assertThat(row.getTargetColumnNames()).size().isEqualTo(2);

      assertThat(row.getTargetColumnNames()).contains("output1");
      assertThat(row.getTargetColumnNames()).contains("output2");
      AssertionsForClassTypes.assertThat(row.getTargetCell("output1")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row.getTargetCell("output2")).isEqualTo(1.0);

      assertThat(row.getCategoricalTargetColumnNames()).contains("output1");
      AssertionsForClassTypes.assertThat(row.getCategoricalTargetCell("output1")).isEqualTo("Hello");
      assertThat(row.getCategoricalTargetColumnNames()).contains("output2");
      AssertionsForClassTypes.assertThat(row.getCategoricalTargetCell("output2")).isEqualTo("World");

      DataRow row2 = new BasicDataRow();
      row2.copy(row);

      assertThat(row2.getColumnNames()).size().isEqualTo(3);
      AssertionsForClassTypes.assertThat(row2.getCell("input1")).isEqualTo(1.0);
      AssertionsForClassTypes.assertThat(row2.getCell("input2")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row2.getCell("input3")).isEqualTo(0.0);


      assertThat(row2.getCategoricalTargetColumnNames()).size().isEqualTo(2);
      assertThat(row2.getTargetColumnNames()).size().isEqualTo(2);

      assertThat(row2.getTargetColumnNames()).contains("output1");
      assertThat(row2.getTargetColumnNames()).contains("output2");
      AssertionsForClassTypes.assertThat(row2.getTargetCell("output1")).isEqualTo(2.0);
      AssertionsForClassTypes.assertThat(row2.getTargetCell("output2")).isEqualTo(1.0);

      assertThat(row2.getCategoricalTargetColumnNames()).contains("output1");
      AssertionsForClassTypes.assertThat(row2.getCategoricalTargetCell("output1")).isEqualTo("Hello");
      assertThat(row2.getCategoricalTargetColumnNames()).contains("output2");
      AssertionsForClassTypes.assertThat(row2.getCategoricalTargetCell("output2")).isEqualTo("World");

      logger.info("row: {}", row2);

   }
}
