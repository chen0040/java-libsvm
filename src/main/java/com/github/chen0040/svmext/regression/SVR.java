package com.github.chen0040.svmext.regression;


import com.github.chen0040.libsvm.*;
import com.github.chen0040.svmext.Learner;
import com.github.chen0040.svmext.data.DataFrame;
import com.github.chen0040.svmext.data.DataRow;
import com.github.chen0040.svmext.utils.Scaler;

import java.util.Vector;

import static com.github.chen0040.libsvm.SupportVectorMachine.svm_predict;
import static com.github.chen0040.libsvm.SupportVectorMachine.svm_set_print_string_function;
import static com.github.chen0040.libsvm.SupportVectorMachine.svm_train;


/**
 * Created by xschen on 5/5/2017.
 */
public class SVR implements Learner {
   private static svm_print_interface svm_print_null = new svm_print_interface()
   {
      public void print(String s) {}
   };
   private svm_parameter param;
   private int cross_validation;
   private svm_model model;
   private boolean quiet;

   private final Scaler scaler = new Scaler();
   private String name;


   public void copy(SVR that){

      param = that.param == null ? null : that.param.makeCopy();
      cross_validation = that.cross_validation;
      model = that.model == null ? null : that.model.makeCopy();
      if(model != null) model.param = param;
      quiet = that.quiet;
      scaler.copy(that.scaler);
   }

   public SVR makeCopy(){
      SVR clone = new SVR();
      clone.copy(this);

      return clone;
   }

   public SVR(){
      svm_print_interface print_func = null;	// default printing to stdout

      param = new svm_parameter();
      // default values
      param.svm_type = svm_parameter.NU_SVR;
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
      this.quiet = true;
   }

   public SVMType getSVMType(){
      if(param.svm_type == svm_parameter.EPSILON_SVR){
         return SVMType.epsilon;
      }else{
         return SVMType.nu;
      }
   }

   public void setSVMType(SVMType type){
      switch (type){
         case nu:
            param.svm_type = svm_parameter.NU_SVR;
            break;
         case epsilon:
            param.svm_type = svm_parameter.EPSILON_SVR;
      }
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

   @Override
   public double transform(DataRow row) {

      row = scaler.transform(row);

      double[] x0 = row.toArray();
      int n = x0.length;

      SupportVectorMachineNode[] x = new SupportVectorMachineNode[n];
      for(int j=0; j < n; j++)
      {
         x[j] = new SupportVectorMachineNode();
         x[j].index = j+1;
         x[j].value = x0[j];
      }

      double v = svm_predict(model, x);
      return scaler.inverseTransform(row.targetColumnName(), v);
   }

   @Override
   public void fit(DataFrame frame) {

      if(this.quiet){
         svm_set_print_string_function(svm_print_null);
      }else{
         svm_set_print_string_function(null);
      }



      Vector<Double> vy = new Vector<Double>();
      Vector<SupportVectorMachineNode[]> vx = new Vector<>();
      int max_index = 0;

      scaler.fit(frame);

      int m = frame.rowCount();
      for(int i=0; i < m; ++i)
      {
         DataRow row = frame.row(i);

         row = scaler.transform(row);

         double[] x0 = row.toArray();
         int n = x0.length;


         vy.add(row.target());
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
      prob.x = new SupportVectorMachineNode[prob.l][];
      for(int i=0;i<prob.l;i++)
         prob.x[i] = vx.elementAt(i);
      prob.y = new double[prob.l];
      for(int i=0;i<prob.l;i++)
         prob.y[i] = vy.elementAt(i);

      if(param.gamma == 0 && max_index > 0)
         param.gamma = 1.0/max_index;


      model = svm_train(prob, param);
   }


   public void setName(String name) {
      this.name = name;
   }


   public String getName() {
      return name;
   }


   public enum SVMType{
      nu,
      epsilon
   }
}
