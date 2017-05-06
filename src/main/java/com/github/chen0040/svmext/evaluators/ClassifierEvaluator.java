package com.github.chen0040.svmext.evaluators;


import com.github.chen0040.svmext.utils.NumberUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xschen on 04/16/17.
 */
public class ClassifierEvaluator {

   private ConfusionMatrix confusionMatrix = new ConfusionMatrix();
   public void evaluate(String actual, String predicted){
      confusionMatrix.incCount(actual, predicted);
   }

   public List<String> classLabels() {
      return confusionMatrix.getLabels();
   }


   public void reset() {
      confusionMatrix.reset();
   }


   public ConfusionMatrix getConfusionMatrix() {
      return confusionMatrix;
   }


   public void setConfusionMatrix(ConfusionMatrix confusionMatrix) {
      this.confusionMatrix = confusionMatrix;
   }

   public double getAccuracy() {
      double accuracy = 0;

      List<String> list = confusionMatrix.getLabels();

      int correctCount = 0;
      int totalCount = 0;
      for(int i=0; i < list.size(); ++i) {
         String actual = list.get(i);
         for(int j=0; j < list.size(); ++j) {
            String predicted = list.get(j);
            int value = confusionMatrix.getCount(actual, predicted);
            correctCount += (i == j) ? value : 0;
            totalCount += value;
         }
      }

      if(totalCount > 0) {
         accuracy = (double) correctCount / totalCount;
      }

      return accuracy;
   }

   public double getMisclassificationRate(){
      return 1- getAccuracy();
   }

   public int getTruePositiveCount(String classLabel) {
      return confusionMatrix.getCount(classLabel, classLabel);
   }

   public int getFalsePositiveCount(String classLabel) {
      return confusionMatrix.getColumnSum(classLabel) - getTruePositiveCount(classLabel);
   }

   public double avgTruePositive() {
      List<String> labels = classLabels();
      if(labels.isEmpty())  return 0;

      int sum = 0;
      for(String label : labels) {
         sum += getTruePositiveCount(label);
      }
      return (double)sum / labels.size();
   }

   public double avgFalsePositive() {
      List<String> labels = classLabels();
      if(labels.isEmpty())  return 0;

      int sum = 0;
      for(String label : labels) {
         sum += getFalsePositiveCount(label);
      }
      return (double)sum / labels.size();
   }

   // Precision is the proportion of cases correctly identified as belonging to class c
   // among all cases of which the classifier claims that they belong to class c
   public Map<String, Double> getPrecisionByClass() {
      Map<String, Double> result = new HashMap<>();
      List<String> list = classLabels();
      for(int i=0; i < list.size(); ++i) {
         String label = list.get(i);
         int correctCount = confusionMatrix.getCount(label, label);
         int totalPredictedCount = confusionMatrix.getColumnSum(label);
         double precision = 0;
         if(totalPredictedCount > 0){
            precision = (double)correctCount / totalPredictedCount;
         }
         result.put(label, precision);
      }
      return result;
   }

   // Recall is the proportion of cases correctly identified as belonging to class c among all
   // cases that truely belong to class c.
   public Map<String, Double> getRecallByClass(){

      Map<String, Double> result = new HashMap<>();

      List<String> list = classLabels();

      for(int i=0; i < list.size(); ++i) {
         String label = list.get(i);
         int correctCount = confusionMatrix.getCount(label, label);
         int totalTrueCount = confusionMatrix.getRowSum(label);
         double recall = 0;
         if(totalTrueCount > 0) {
            recall = (double)correctCount / totalTrueCount;
         }

         result.put(label,recall);
      }

      return result;
   }

   // fallout is the proportion of cases incorrectly identified as belonging to class c among all
   // cases that truely not belonging to class c.
   // fallout is the false-positive rate.
   public Map<String, Double> getFalloutByClass(){

      Map<String, Double> result = new HashMap<>();

      List<String> list = classLabels();

      for(int i=0; i < list.size(); ++i) {
         String label = list.get(i);

         int totalNegativeCount = 0;

         int falsePositiveCount = 0;
         for(int j=0; j < list.size(); ++j) {
            if(i==j) continue;
            String notTrueLabel = list.get(j);
            falsePositiveCount += confusionMatrix.getCount(notTrueLabel, label);
            totalNegativeCount += confusionMatrix.getRowSum(notTrueLabel);
         }
         double fallout = 0;
         if(totalNegativeCount > 0) {
            fallout = (double)falsePositiveCount / totalNegativeCount;
         }

         result.put(label,fallout);
      }

      return result;
   }

   public Map<String, Double> getF1ScoreByClass() {
      Map<String, Double> precisions = getPrecisionByClass();
      Map<String, Double> recalls = getRecallByClass();

      List<String> labels = classLabels();

      Map<String, Double> result = new HashMap<>();

      for(String label : labels) {
         double precision = precisions.get(label);
         double recall = recalls.get(label);
         if(NumberUtils.isZero(precision+recall)){
            continue;
         }

         double f1score = 2 * (precision * recall) / (precision + recall);
         result.put(label, f1score);
      }

      return result;
   }

   // concept of macro-f1 score can be found here: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.104.8244&rep=rep1&type=pdf
   public double getMacroF1Score() {
      double sum = 0;
      int count = 0;
      Map<String, Double> data = getF1ScoreByClass();
      for(Map.Entry<String, Double> entry : data.entrySet()) {
         sum += entry.getValue();
         count++;
      }
      if(count == 0) return 0;
      return sum / count;
   }

   // concept of micro-f1 score can be found here: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.104.8244&rep=rep1&type=pdf
   public double getMicroF1Score() {
      Map<String, Double> precisions = getPrecisionByClass();
      Map<String, Double> recalls = getRecallByClass();

      List<String> labels = classLabels();

      double precisionAvg = 0;
      double recallAvg = 0;
      for(String label : labels) {
         double precision = precisions.get(label);
         double recall = recalls.get(label);
         precisionAvg += precision;
         recallAvg += recall;
      }

      precisionAvg /= labels.size();
      recallAvg /= labels.size();


      return 2 * (precisionAvg * recallAvg) / (precisionAvg + recallAvg);
   }

   public String getSummary() {
      StringBuilder sb = new StringBuilder();
      sb.append("accuracy: ").append(getAccuracy());
      sb.append("\nmis-classification: ").append(getMisclassificationRate());
      sb.append("\nmacro f1-score: ").append(getMacroF1Score());
      sb.append("\nmicro f1-score: ").append(getMicroF1Score());

      return sb.toString();
   }

   public void report(){
      System.out.println(getSummary());
   }



}
