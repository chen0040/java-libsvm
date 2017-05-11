package com.github.chen0040.svmext;


import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;


/**
 * Created by xschen on 6/5/2017.
 */
public interface Learner {
   double transform(DataRow row);
   void fit(DataFrame dataFrame);
}
