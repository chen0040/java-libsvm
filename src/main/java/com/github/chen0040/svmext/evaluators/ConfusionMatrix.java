package com.github.chen0040.svmext.evaluators;


import com.github.chen0040.svmext.utils.TupleTwo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


/**
 * Created by xschen on 04/16/17.
 */
public class ConfusionMatrix {
   private Map<TupleTwo<String, String>, Integer> matrix = new HashMap<>();
   private Set<String> labels = new HashSet<>();

   private transient ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

   public void incCount(String actual, String predicted) {
      readWriteLock.writeLock().lock();
      try{
         labels.add(actual);
         labels.add(predicted);
         TupleTwo<String, String> key = new TupleTwo<>(actual, predicted);
         matrix.put(key, matrix.getOrDefault(key, 0) + 1);
      }finally {
         readWriteLock.writeLock().unlock();
      }
   }

   public List<String> getLabels(){
      List<String> result = new ArrayList<>();

      readWriteLock.readLock().lock();
      try {
         result.addAll(labels.stream().collect(Collectors.toList()));
      } finally {
         readWriteLock.readLock().unlock();
      }

      return result;
   }

   public void setLabels(List<String> labels) {
      readWriteLock.writeLock().lock();
      try {
         this.labels.clear();
         this.labels.addAll(labels);
      }finally {
         readWriteLock.writeLock().unlock();
      }
   }

   // sum of a row representing class c, which is sum of cases that truely belong to class c
   public int getRowSum(String actual) {
      List<String> list = this.getLabels();
      int sum = 0;
      for(int i=0; i < list.size(); ++i) {
         String predicted = list.get(i);
         sum += getCount(actual, predicted);
      }
      return sum;
   }


   // sum of a column representing class c, which is sum of cases the classifiers claims to belong to class c
   public int getColumnSum(String predicted) {
      List<String> list = this.getLabels();
      int sum = 0;
      for(int i=0; i < list.size(); ++i) {
         String actual = list.get(i);
         sum += getCount(actual, predicted);
      }
      return sum;
   }



   public int getCount(String actual, String predicted) {
      int value = 0;
      readWriteLock.readLock().lock();
      try{
         value = matrix.getOrDefault(new TupleTwo<>(actual, predicted), 0);
      }finally {
         readWriteLock.readLock().unlock();
      }
      return value;
   }


   public void reset() {
      readWriteLock.writeLock().lock();
      try {
         matrix.clear();
      } finally {
         readWriteLock.writeLock().unlock();
      }
   }


   public Map<TupleTwo<String, String>, Integer> getMatrix() {
      return matrix;
   }


   public void setMatrix(Map<TupleTwo<String, String>, Integer> matrix) {
      this.matrix = matrix;
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();

      readWriteLock = new ReentrantReadWriteLock();
   }
}
