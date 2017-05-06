package com.github.chen0040.svmext.classifiers;


import com.github.chen0040.svmext.data.DataFrame;
import com.github.chen0040.svmext.data.DataQuery;
import com.github.chen0040.svmext.evaluators.ClassifierEvaluator;
import com.github.chen0040.svmext.utils.FileUtils;
import com.github.chen0040.svmext.utils.TupleTwo;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.*;


/**
 * Created by xschen on 6/5/2017.
 */
public class OneVsOneSVCUnitTest {
   @Test
   public void test_iris() throws IOException {
      InputStream irisStream = FileUtils.getResource("iris.data");
      DataFrame irisData = DataQuery.csv(",", false)
              .from(irisStream)
              .selectColumn(0).asInput("Sepal Length")
              .selectColumn(1).asInput("Sepal Width")
              .selectColumn(2).asInput("Petal Length")
              .selectColumn(3).asInput("Petal Width")
              .selectColumn(4).transform(label -> label).asOutput("Iris Type")
              .build();

      TupleTwo<DataFrame, DataFrame> parts = irisData.shuffle().split(0.9);

      DataFrame trainingData = parts._1();
      DataFrame crossValidationData = parts._2();

      System.out.println(crossValidationData.head(10));

      OneVsOneSVC multiClassClassifier = new OneVsOneSVC();
      multiClassClassifier.fit(trainingData);

      ClassifierEvaluator evaluator = new ClassifierEvaluator();

      for(int i=0; i < crossValidationData.rowCount(); ++i) {
         String predicted = multiClassClassifier.classify(crossValidationData.row(i));
         String actual = crossValidationData.row(i).categoricalTarget();
         System.out.println("predicted: " + predicted + "\tactual: " + actual);
         evaluator.evaluate(actual, predicted);
      }

      evaluator.report();
   }
}
