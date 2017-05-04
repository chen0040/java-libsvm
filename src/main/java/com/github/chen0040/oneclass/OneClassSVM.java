package com.github.chen0040.oneclass;


import com.github.chen0040.data.DataRow;
import com.github.chen0040.data.DataTable;
import com.github.chen0040.libsvm.*;

import java.util.Vector;
import java.util.function.Supplier;

import static com.github.chen0040.libsvm.SupportVectorMachine.svm_predict;
import static com.github.chen0040.libsvm.SupportVectorMachine.svm_set_print_string_function;
import static com.github.chen0040.libsvm.SupportVectorMachine.svm_train;


/**
 * Created by xschen on 2/5/2017.
 */
public class OneClassSVM {

   private static svm_print_interface svm_print_null = new svm_print_interface()
   {
      public void print(String s) {}
   };
   private svm_parameter param;
   private int cross_validation;
   private svm_model model;
   private boolean quiet;
   public Supplier<Double> thresholdSupplier;

   public void copy(OneClassSVM that){
      param = that.param == null ? null : that.param.makeCopy();
      cross_validation = that.cross_validation;
      quiet = that.quiet;
      model = that.model == null ? null : that.model.makeCopy();
      if(model != null) model.param = param;
   }

   private double threshold(){
      if(thresholdSupplier == null){
         return 0;
      }else{
         return thresholdSupplier.get();
      }
   }

   public OneClassSVM makeCopy(){
      OneClassSVM clone = new OneClassSVM();
      clone.copy(this);
      return clone;
   }

   public OneClassSVM(){
      svm_print_interface print_func = null;	// default printing to stdout

      param = new svm_parameter();
      // default values
      param.svm_type = svm_parameter.ONE_CLASS;
      param.kernel_type = svm_parameter.RBF;
      param.degree = 3;
      param.gamma = 0;	// 1/num_features
      param.coef0 = 0;
      param.nu = 0.5;
      param.cache_size = 100;
      param.C = 1;
      param.eps = 1e-3;
      param.p = 0.1;
      param.shrinking = 1;
      param.probability = 0;
      param.nr_weight = 0;
      param.weight_label = new int[0];
      param.weight = new double[0];
      cross_validation = 0;

      svm_set_print_string_function(svm_print_null);
      quiet = true;
   }

   public void set_nu(double nu) {
      param.nu = nu;
   }

   public void set_gamma(double gamma){
      param.gamma = gamma;
   }

   public boolean isQuiet() {
      return quiet;
   }

   public void setQuiet(boolean quiet) {
      this.quiet = quiet;
   }

   public svm_parameter getParameters(){
      return param;
   }

   public double transform(DataRow row) {
      double[] x0 = row.cells();
      int n = x0.length;

      SupportVectorMachineNode[] x = new SupportVectorMachineNode[n];
      for(int j=0; j < n; j++)
      {
         x[j] = new SupportVectorMachineNode();
         x[j].index = j+1;
         x[j].value = x0[j];
      }

      double v = svm_predict(model,x);
      return v;
   }

   public boolean isAnomaly(DataRow tuple) {
      double p = transform(tuple);
      return p < threshold();
   }

   public void fit(DataTable table) {

      if(this.quiet){
         svm_set_print_string_function(svm_print_null);
      }else{
         svm_set_print_string_function(null);
      }

      Vector<SupportVectorMachineNode[]> vx = new Vector<>();
      int max_index = 0;

      int m = table.rowCount();
      for(int i=0; i < m; ++i)
      {
         DataRow tuple = table.row(i);

         double[] x0 = tuple.cells();
         int n = x0.length;

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
      for(int i=0;i<prob.l;i++)
         prob.x[i] = vx.elementAt(i);
      prob.y = new double[m];
      for(int i=0;i<prob.l;i++)
         prob.y[i] = 0;

      if(param.gamma == 0 && max_index > 0)
         param.gamma = 1.0/max_index;


      model = svm_train(prob, param);
   }
}
