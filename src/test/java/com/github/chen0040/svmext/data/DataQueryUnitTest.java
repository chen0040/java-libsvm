package com.github.chen0040.svmext.data;

import com.github.chen0040.svmext.utils.FileUtils;
import com.github.chen0040.svmext.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Created by xschen on 2/5/2017.
 */
public class DataQueryUnitTest {

   private Logger logger = LoggerFactory.getLogger(DataQueryUnitTest.class);

   @Test
   public void test_csv() throws IOException {

      int column_use = 3;
      int column_livch = 4;
      int column_age = 5;
      int column_urban = 6;

      boolean skipFirstLine = true;
      String columnSplitter = ",";
      InputStream inputStream = FileUtils.getResource("contraception.csv");
      DataFrame frame = DataQuery.csv(columnSplitter, skipFirstLine)
              .from(inputStream)
              .selectColumn(column_livch).asInput("livch")
              .selectColumn(column_age).asInput("age")
              .selectColumn(column_age).transform(age -> Math.pow(StringUtils.parseDouble(age), 2)).asInput("age^2")
              .selectColumn(column_urban).transform(label -> label.equals("Y") ? 1.0 : 0.0).asInput("urban")
              .selectColumn(column_use).transform(label -> label.equals("Y") ? 1.0 : 0.0).asOutput("use")
              .build();

      for(int i=0; i < 10; ++i){
         logger.info("row[{}]: {}", i, frame.row(i));
      }

      logger.info("row count: {}", frame.rowCount());

      List<InputDataColumn> columnList = frame.getInputColumns();

      for (int i=0; i < columnList.size(); ++i){
         logger.info("column: {}", columnList.get(i).summary());
      }

   }

   @Test
   public void test_libsvm() throws IOException {
      DataFrame frame = DataQuery.libsvm().from(FileUtils.getResource("heart_scale.txt")).build();

      for(int i=0; i < 10; ++i){
         logger.info("row[{}]: {}", i, frame.row(i));
      }

      logger.info("row count: {}", frame.rowCount());
   }
}
