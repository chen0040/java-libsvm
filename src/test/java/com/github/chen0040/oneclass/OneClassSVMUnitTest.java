package com.github.chen0040.oneclass;


import com.github.chen0040.data.DataTable;
import com.github.chen0040.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static org.testng.Assert.*;


/**
 * Created by xschen on 2/5/2017.
 */
public class OneClassSVMUnitTest {

   private static final Logger logger = LoggerFactory.getLogger(OneClassSVMUnitTest.class);

   private static final String IS_NOT_ANOMALY = "NEGATIVE";
   private static final String IS_ANOMALY = "POSITIVE";

   /*
   @Test
   public void testFindOutliers(){
      String[] filenames_X = {"X1.txt", "X2.txt"};
      String[] filenames_outliers = { "outliers1.txt", "outliers2.txt"};

      for(int k=0; k < filenames_X.length; ++k){

         String filename_X = filenames_X[k];
         String filename_outliers = filenames_outliers[k];

         DataTable trainingBatch = new DataTable(FileUtils.getResource(filename_X), " ", false);

         OneClassSVM algorithm = new OneClassSVM();

         algorithm.fit(trainingBatch);
         //algorithm.set_nu(); //need to fine sweep the value of nu to tupleAtIndex good result

         List<Integer> predicted_outliers = algorithm.findOutlierPositions(trainingBatch);

         final List<Integer> expected_outliers = FileUtils.readIntegers(filename_outliers, " ", false);

         System.out.println("Expected Outliers:"+expected_outliers.size());
         System.out.println(StringHelper.toString(expected_outliers));

         System.out.println("Predicted Outliers:"+predicted_outliers.size());
         System.out.println(StringHelper.toString(predicted_outliers));

         System.out.println("F1 Score: "+ F1Score.score(expected_outliers, predicted_outliers, trainingBatch.tupleCount()));
      }
   }*/


   private static Random random = new Random();

   public static double rand(){
      return random.nextDouble();
   }

   public static double rand(double lower, double upper){
      return rand() * (upper - lower) + lower;
   }

   public static double randn(){
      double u1 = rand();
      double u2 = rand();
      double r = Math.sqrt(-2.0 * Math.log(u1));
      double theta = 2.0 * Math.PI * u2;
      return r * Math.sin(theta);
   }


   // unit testing based on example from http://scikit-learn.org/stable/auto_examples/svm/plot_oneclass.html#
   @Test
   public void testSimple(){

      DataTable trainingBatch = new DataTable("c1", "c2");
      // add some normal data
      for(int i=0; i < 100; ++i){
         trainingBatch.addRow(IS_NOT_ANOMALY, randn() * 0.3 + 2, randn() * 0.3 + 2);
         trainingBatch.addRow(IS_NOT_ANOMALY, randn() * 0.3 - 2, randn() * 0.3 - 2);
      }

      DataTable crossValidationBatch = new DataTable("c1", "c2");
      // add some validation data
      for(int i=0; i < 20; ++i){
         crossValidationBatch.addRow(IS_NOT_ANOMALY, randn() * 0.3 + 2, randn() * 0.3 + 2);
         crossValidationBatch.addRow(IS_NOT_ANOMALY, randn() * 0.3 - 2, randn() * 0.3 - 2);
      }

      DataTable outliers = new DataTable("c1", "c2");
      // add some outliers data
      for(int i=0; i < 20; ++i){
         outliers.addRow(IS_ANOMALY, rand(-4, 4), rand(-4, 4));
         outliers.addRow(IS_ANOMALY, rand(-4, 4), rand(-4, 4));
      }

      OneClassSVM algorithm = new OneClassSVM();
      algorithm.set_gamma(0.1);
      algorithm.set_nu(0.1);

      algorithm.fit(trainingBatch);

      for(int i = 0; i < crossValidationBatch.rowCount(); ++i){
         String predicted = algorithm.isAnomaly(crossValidationBatch.row(i)) ? IS_ANOMALY : IS_NOT_ANOMALY;
         logger.info("predicted: {}\texpected: {}", predicted, crossValidationBatch.row(i).getLabel());
      }

      for(int i = 0; i < outliers.rowCount(); ++i){
         String predicted = algorithm.isAnomaly(crossValidationBatch.row(i)) ? IS_ANOMALY : IS_NOT_ANOMALY;
         logger.info("predicted: {}\texpected: {}", predicted, outliers.row(i).getLabel());
      }


   }

   /*
   @Test
   public void testEvaluation(){
      String[] filenames_X = {"X1.txt", "X2.txt"};
      String[] filenames_Xval = {"Xval1.txt", "Xval2.txt"};
      String[] filenames_yval = { "yval1.txt", "yval2.txt"};

      for(int k=0; k < filenames_X.length; ++k){

         String filename_X = filenames_X[k];
         String filename_Xval = filenames_Xval[k];
         String filename_yval = filenames_yval[k];

         DataTable trainingBatch = new DataTable(FileUtils.getResource(filename_X), " ", false);

         OneClassSVM algorithm = new OneClassSVM();
         algorithm.fit(trainingBatch);

         DataTable crossValidationBatch = new DataTable(FileUtils.getResource(filename_Xval), " ", false);

         final List<String> crossValidationLabels = new ArrayList<>();
         CSVService.getInstance().readDoc(FileUtils.getResource(filename_yval), null, false, new Function<DomElement, Boolean>() {
            public Boolean apply(DomElement line) {
               String[] values = line.data;
               assertEquals(1, values.length);
               boolean is_anomaly = StringHelper.parseBoolean(values[0]);
               if (is_anomaly) crossValidationLabels.add(IS_ANOMALY);
               else crossValidationLabels.add(IS_NOT_ANOMALY);
               return true;
            }
         }, null);

         assertEquals(crossValidationLabels.size(), crossValidationBatch.tupleCount());

         for(int i = 0; i < crossValidationBatch.tupleCount(); ++i){
            crossValidationBatch.tupleAtIndex(i).setLabelOutput(crossValidationLabels.get(i));
         }

         for(int i = 0; i < crossValidationBatch.tupleCount(); ++i){
            String predicted = algorithm.isAnomaly(crossValidationBatch.tupleAtIndex(i)) ? IS_ANOMALY : IS_NOT_ANOMALY;
            System.out.println("predicted: "+predicted+"\texpected: "+crossValidationLabels.get(i));
         }
      }
   }*/
}
