package com.github.chen0040.oneclass;


import com.github.chen0040.data.DataFrame;
import com.github.chen0040.data.DataQuery;
import com.github.chen0040.data.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Random;


/**
 * Created by xschen on 2/5/2017.
 */
public class OneClassSVMUnitTest {

   private static final Logger logger = LoggerFactory.getLogger(OneClassSVMUnitTest.class);



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

      DataQuery.DataFrameQueryBuilder schema = DataQuery.blank()
              .newInput("c1")
              .newInput("c2")
              .newOutput("anomaly")
              .end();

      Sampler.DataSampleBuilder negativeSampler = new Sampler()
              .forColumn("c1").generate((name, index) -> randn() * 0.3 + (index % 2 == 0 ? -2 : 2))
              .forColumn("c2").generate((name, index) -> randn() * 0.3 + (index % 2 == 0 ? -2 : 2))
              .forColumn("anomaly").generate((name, index) -> 0.0)
              .end();

      DataFrame trainingData = schema.build();

      trainingData = negativeSampler.sample(trainingData, 200);

      System.out.println(trainingData.head(10));

      DataFrame crossValidationData = schema.build();

      crossValidationData = negativeSampler.sample(crossValidationData, 40);

      DataFrame outliers = schema.build();

      outliers = new Sampler()
              .forColumn("c1").generate((name, index) -> rand(-4, 4))
              .forColumn("c2").generate((name, index) -> rand(-4, 4))
              .forColumn("anomaly").generate((name, index) -> 1.0)
              .end().sample(outliers, 40);

      final double threshold = 0.5;
      OneClassSVM algorithm = new OneClassSVM();
      algorithm.set_gamma(0.1);
      algorithm.set_nu(0.1);
      algorithm.thresholdSupplier = () -> 0.0;

      algorithm.fit(trainingData);

      for(int i = 0; i < crossValidationData.rowCount(); ++i){
         boolean predicted = algorithm.isAnomaly(crossValidationData.row(i));
         logger.info("predicted: {}\texpected: {}", predicted, crossValidationData.row(i).target() > threshold);
      }

      for(int i = 0; i < outliers.rowCount(); ++i){
         boolean predicted = algorithm.isAnomaly(outliers.row(i));
         logger.info("outlier predicted: {}\texpected: {}", predicted, outliers.row(i).target() > threshold);
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
