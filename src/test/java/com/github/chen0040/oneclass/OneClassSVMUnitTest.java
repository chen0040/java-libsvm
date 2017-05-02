package com.github.chen0040.oneclass;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Created by xschen on 2/5/2017.
 */
public class OneClassSVMUnitTest {

   private static final Logger logger = LoggerFactory.getLogger(OneClassSVMUnitTest.class);

   @Test
   public void test_oneClass(){
      OneClassSVM oneClass = new OneClassSVM();
      logger.info("Hello World");
   }
}
