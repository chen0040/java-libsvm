package com.github.chen0040.svmext.evaluators;

import java.io.Serializable;
import java.util.Map;


/**
 * Created by xschen on 10/9/2016.
 */
public class BinaryClassifierEvaluator {

   private int truePositive = 0;
   private int trueNegative = 0;

   private int falsePositive = 0;
   private int falseNegative = 0;

   private double precision;
   private double fallout; // Fall-out, or the false positive rate, is the number of false positive divided by the total number of negatives.
   private double accuracy;
   private double recall;
   private double specificity;
   private double sensitivity; // note that recalls is that same as sensitivity by definition
   private double misclassificationRate;

   private double f1Score;
   private int totalPredictedAnomalyDayCount;
   private int simulatedAnomalyTimeWindowCount;
   private Map<String, Integer> predictedAnomalyDayCountByDayOfWeek;
   private long startTime = 0L;
   private long endTime = 0L;

   private boolean isValid = false;



   public BinaryClassifierEvaluator(){

   }

   public BinaryClassifierEvaluator(int truePositive,
           int trueNegative,
           int falsePositive,
           int falseNegative){
      this.truePositive = truePositive;
      this.trueNegative = trueNegative;
      this.falsePositive = falsePositive;
      this.falseNegative = falseNegative;

      update();
   }

   public void reset(){
      trueNegative = 0;
      truePositive = 0;
      falsePositive = 0;
      falseNegative = 0;
      isValid = false;

      precision = 0;
      recall = 0;
      specificity = 0;
      misclassificationRate = 0;
      f1Score = 0;
   }

   public void evaluate(boolean actual, boolean predicted) {
      if(predicted){
         assertTruePositive(actual, predicted);
      } else {
         assertTrueNegative(actual, predicted);
      }
   }

   private void assertTruePositive(boolean actual, boolean predicted){
      if(actual == predicted){
         truePositive++;
      } else {
         falsePositive++;
      }
   }

   private void assertTrueNegative(boolean actual, boolean predicted){
      if(actual == predicted){
         trueNegative++;
      } else {
         falseNegative++;
      }
   }

   private void update(){
      this.precision = (double)(truePositive) / (truePositive + falsePositive);

      this.sensitivity = (double)(truePositive) / (truePositive + falseNegative);
      this.specificity = (double)(trueNegative) / (trueNegative + falsePositive);

      // recall = sensitivity
      this.recall = (double)(truePositive) / (truePositive + falseNegative);
      this.accuracy = (double)(truePositive + trueNegative) / (truePositive + trueNegative + falsePositive + falseNegative);

      // fallout = 1 - specificity
      this.fallout = (double)(falsePositive) / (falsePositive + trueNegative);


      this.misclassificationRate = (double)(falsePositive + falseNegative) / (truePositive + trueNegative + falsePositive + falseNegative);

      this.f1Score = 2 * (precision * recall) / (precision + recall);
      isValid = true;
   }


   public void setTotalPredictedAnomalyDayCount(int totalPredictedAnomalyDayCount) {
      this.totalPredictedAnomalyDayCount = totalPredictedAnomalyDayCount;
   }


   public int getTotalPredictedAnomalyDayCount() {
      return totalPredictedAnomalyDayCount;
   }


   public void setSimulatedAnomalyTimeWindowCount(int simulatedAnomalyTimeWindowCount) {
      this.simulatedAnomalyTimeWindowCount = simulatedAnomalyTimeWindowCount;
   }


   public int getSimulatedAnomalyTimeWindowCount() {
      return simulatedAnomalyTimeWindowCount;
   }


   public void setPredictedAnomalyDayCountByDayOfWeek(Map<String, Integer> predictedAnomalyDayCountByDayOfWeek) {
      this.predictedAnomalyDayCountByDayOfWeek = predictedAnomalyDayCountByDayOfWeek;
   }


   public Map<String, Integer> getPredictedAnomalyDayCountByDayOfWeek() {
      return predictedAnomalyDayCountByDayOfWeek;
   }


   public int getTruePositive() {
      return truePositive;
   }


   public void setTruePositive(int truePositive) {
      this.truePositive = truePositive;
   }


   public int getTrueNegative() {
      return trueNegative;
   }


   public void setTrueNegative(int trueNegative) {
      this.trueNegative = trueNegative;
   }


   public int getFalsePositive() {
      return falsePositive;
   }


   public void setFalsePositive(int falsePositive) {
      this.falsePositive = falsePositive;
   }


   public int getFalseNegative() {
      return falseNegative;
   }


   public void setFalseNegative(int falseNegative) {
      this.falseNegative = falseNegative;
   }


   public double getPrecision() {
      if(!isValid) {
         update();
      }
      return precision;
   }


   public void setPrecision(double precision) {
      this.precision = precision;
   }


   public double getAccuracy() {
      if(!isValid) {
         update();
      }
      return accuracy;
   }


   public void setAccuracy(double accuracy) {
      this.accuracy = accuracy;
   }


   public double getRecall() {
      if(!isValid) {
         update();
      }
      return recall;
   }


   public double getSpecificity() {
      if(!isValid) {
         update();
      }
      return specificity;
   }


   public double getF1Score() {
      if(!isValid) {
         update();
      }
      return f1Score;
   }


   public double getSensitivity() {
      if(!isValid) {
         update();
      }
      return sensitivity;
   }


   public void setSensitivity(double sensitivity) {
      this.sensitivity = sensitivity;
   }


   public double getMisclassificationRate() {
      if(!isValid) {
         update();
      }
      return misclassificationRate;
   }


   public void setMisclassificationRate(double misclassificationRate) {
      this.misclassificationRate = misclassificationRate;
   }


   public double getFallout() {
      return fallout;
   }


   public void report() {


      System.out.println(getSummary());
   }
   
   public String getSummary() {
      StringBuilder sb = new StringBuilder();
      sb.append("accuracy: ").append(getAccuracy());
      sb.append("\nmis-classification: ").append(getMisclassificationRate());
      sb.append("\nf1-score: ").append(getF1Score());
      sb.append("\nduration (seconds): ").append(durationInSeconds());

      return sb.toString();
   }


   public void startTimer() {
      startTime = System.currentTimeMillis();
   }

   public void stopTimer() {
      endTime = System.currentTimeMillis();
   }

   public long durationInSeconds() {
      return (endTime - startTime) / 1000;
   }
}
