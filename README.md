# java-libsvm
Package provides the direct java conversion of the origin libsvm

[![Build Status](https://travis-ci.org/chen0040/java-libsvm.svg?branch=master)](https://travis-ci.org/chen0040/java-libsvm) [![Coverage Status](https://coveralls.io/repos/github/chen0040/java-libsvm/badge.svg?branch=master)](https://coveralls.io/github/chen0040/java-libsvm?branch=master)

# Usage

# One-class SVM 

Below is the code to create and train a one-class SVM:

```java
OneClassSVM algorithm = new OneClassSVM();
algorithm.fit(training_data)
```

Below is the code to predict if data point is an outlier:

```java
algorithm.isAnomaly(data_point)
```

Below is an complete code example of the one-class SVM for the example below here:

![scki-learn example for one-class](http://scikit-learn.org/stable/_images/sphx_glr_plot_oneclass_001.png)

```java
import com.github.chen0040.svmext.data.DataFrame;
import com.github.chen0040.svmext.data.DataQuery;
import com.github.chen0040.svmext.data.Sampler;
import com.github.chen0040.svmext.oneclass.OneClassSVM;

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

algorithm.fit(trainingData);

for(int i = 0; i < crossValidationData.rowCount(); ++i){
 boolean predicted = algorithm.isAnomaly(crossValidationData.row(i));
 logger.info("predicted: {}\texpected: {}", predicted, crossValidationData.row(i).target() > threshold);
}

for(int i = 0; i < outliers.rowCount(); ++i){
 boolean predicted = algorithm.isAnomaly(outliers.row(i));
 logger.info("outlier predicted: {}\texpected: {}", predicted, outliers.row(i).target() > threshold);
}
```
