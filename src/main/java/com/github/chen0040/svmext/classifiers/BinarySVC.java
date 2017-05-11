package com.github.chen0040.svmext.classifiers;


import com.github.chen0040.libsvm.*;
import com.github.chen0040.svmext.Learner;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;

import java.util.Vector;
import java.util.function.Function;

import static com.github.chen0040.libsvm.SupportVectorMachine.svm_predict;
import static com.github.chen0040.libsvm.SupportVectorMachine.svm_set_print_string_function;
import static com.github.chen0040.libsvm.SupportVectorMachine.svm_train;


/**
 * Created by xschen on 6/5/2017.
 */
public class BinarySVC implements Learner {

   private static svm_print_interface svm_print_null = new svm_print_interface()
   {
      public void print(String s) {}
   };
   private svm_parameter parameters;
   private int cross_validation;
   private svm_model model;
   private boolean quiet;

   public void copy(BinarySVC that){
      parameters = that.parameters == null ? null : that.parameters.makeCopy();
      cross_validation = that.cross_validation;
      model = that.model == null ? null : that.model.makeCopy();
      quiet = that.quiet;
   }

   public BinarySVC makeCopy(){
      BinarySVC clone = new BinarySVC();
      clone.copy(this);

      return clone;
   }

   public BinarySVC(){
      init();
   }

   public static svm_print_interface getSvm_print_null() {
      return svm_print_null;
   }

   public static void setSvm_print_null(svm_print_interface svm_print_null) {
      BinarySVC.svm_print_null = svm_print_null;
   }

   public int getCross_validation() {
      return cross_validation;
   }

   public void setCross_validation(int cross_validation) {
      this.cross_validation = cross_validation;
   }

   public svm_model getModel() {
      return model;
   }

   public void setModel(svm_model model) {
      this.model = model;
   }

   public boolean isQuiet() {
      return quiet;
   }

   public void setQuiet(boolean quiet) {
      this.quiet = quiet;
   }

   public SVMType getSVMType(){
      if(parameters.svm_type == svm_parameter.C_SVC){
         return SVMType.C;
      }else{
         return SVMType.nu;
      }
   }

   public void setSVMType(SVMType type){
      switch (type){

         case C:
            parameters.svm_type = svm_parameter.C_SVC;
            break;
         case nu:
            parameters.svm_type = svm_parameter.NU_SVC;
            break;
      }
   }

   private void init(){
      svm_print_interface print_func = null;	// default printing to stdout

      parameters = new svm_parameter();
      // default values
      parameters.svm_type = svm_parameter.C_SVC;
      parameters.kernel_type = svm_parameter.RBF;
      parameters.degree = 3;
      parameters.gamma = 0;	// 1/num_features
      parameters.coef0 = 0;
      parameters.nu = 0.5;
      parameters.cache_size = 100;
      parameters.C = 1;
      parameters.eps = 1e-3;
      parameters.p = 0.1;
      parameters.shrinking = 1;
      parameters.probability = 0;
      parameters.nr_weight = 0;
      parameters.weight_label = new int[0];
      parameters.weight = new double[0];
      cross_validation = 0;

      svm_set_print_string_function(null);
      quiet = false;
   }

   public svm_parameter getParameters(){
      return parameters;
   }

   public void setParameters(svm_parameter parameters) {
      this.parameters = parameters;
   }

   private void info(String info){

   }

   @Override
   public double transform(DataRow row) {
      double[] x0 = row.toArray();
      int n = x0.length;

      SupportVectorMachineNode[] x = new SupportVectorMachineNode[n];
      for(int j=0; j < n; j++)
      {
         x[j] = new SupportVectorMachineNode();
         x[j].index = j+1;
         x[j].value = x0[j];
      }

      return svm_predict(model, x);
   }

   public boolean isInClass(DataRow row) {
      double p = transform(row);
      return p > 0;
   }

   @Override
   public void fit(DataFrame dataFrame) {

      if(this.quiet){
         svm_set_print_string_function(svm_print_null);
      }else{
         svm_set_print_string_function(null);
      }

      Vector<Double> vy = new Vector<Double>();
      Vector<SupportVectorMachineNode[]> vx = new Vector<SupportVectorMachineNode[]>();
      int max_index = 0;

      int m = dataFrame.rowCount();


      for(int i=0; i < m; ++i)
      {
         DataRow row = dataFrame.row(i);

         double[] x0 = row.toArray();
         int n = x0.length;

         vy.add(row.target() > 0.5 ? 1.0 : -1.0);

         SupportVectorMachineNode[] x = new SupportVectorMachineNode[n];
         for(int j=0; j < n; j++)
         {
            x[j] = new SupportVectorMachineNode();
            x[j].index = j+1;
            x[j].value = x0[j];
         }

         if(n>0) max_index = Math.max(max_index, x[n-1].index);

         vx.addElement(x);
      }

      svm_problem prob = new svm_problem();
      prob.l = m;
      prob.x = new SupportVectorMachineNode[m][];
      for(int i=0;i<m;i++)
         prob.x[i] = vx.elementAt(i);
      prob.y = new double[m];
      for(int i=0;i<m;i++)
         prob.y[i] = vy.elementAt(i);

      if(parameters.gamma == 0 && max_index > 0)
         parameters.gamma = 1.0/max_index;


      model = svm_train(prob, parameters);
   }

   public enum SVMType{
      C,
      nu
   }
}
