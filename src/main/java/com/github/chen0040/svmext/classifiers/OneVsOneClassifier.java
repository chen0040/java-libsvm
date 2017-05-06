package com.github.chen0040.svmext.classifiers;

import com.github.chen0040.svmext.Learner;
import com.github.chen0040.svmext.data.BasicDataFrame;
import com.github.chen0040.svmext.data.DataFrame;
import com.github.chen0040.svmext.data.DataRow;
import com.github.chen0040.svmext.regression.SVR;
import com.github.chen0040.svmext.utils.TupleTwo;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by xschen on 8/20/2015 0020.
 */
public class OneVsOneClassifier implements Learner {
    protected List<TupleTwo<SVR, SVR>> classifiers;
    private double alpha = 0.1;
    private boolean shuffleData = false;
    private List<String> classLabels = new ArrayList<>();

    public OneVsOneClassifier(List<String> classLabels){
        this.classLabels.addAll(classLabels);
        classifiers = new ArrayList<>();
    }

    public OneVsOneClassifier(){
        super();
        classifiers = new ArrayList<>();
    }

    public boolean isShuffleData() {
        return shuffleData;
    }

    public void setShuffleData(boolean shuffleData) {
        this.shuffleData = shuffleData;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    protected void createClassifiers(DataFrame dataFrame){
        classifiers = new ArrayList<>();

        if(classLabels.size()==0){
            classLabels.addAll(dataFrame.stream().map(DataRow::categoricalTarget).distinct().collect(Collectors.toList()));
        }
        for(int i=0; i < classLabels.size()-1; ++i){
            for(int j=i+1; j < classLabels.size(); ++j) {
                SVR svr1 = createClassifier(classLabels.get(i));
                SVR svr2 = createClassifier(classLabels.get(j));
                classifiers.add(new TupleTwo<>(svr1, svr2));
            }
        }
    }



    protected SVR createClassifier(String classLabel) {
        SVR svr = new SVR();
        svr.setName(classLabel);
        return svr;
    }

    protected double getClassifierScore(DataRow tuple, SVR classifier) {
        return classifier.transform(tuple);
    }

    protected List<DataFrame> split(DataFrame dataFrame, int n){
        List<DataFrame> miniFrames = new ArrayList<>();

        for(int i=0; i < n; ++i){
            miniFrames.add(new BasicDataFrame());
        }

        int index = 0;
        for(DataRow tuple : dataFrame) {
            int batchIndex = index % n;
            miniFrames.get(batchIndex).addRow(tuple);
            index++;
        }

        return miniFrames;
    }

    protected List<DataFrame> remerge(List<DataFrame> batches, int k){
        List<DataFrame> newBatches = new ArrayList<>();


        for(int i=0; i < batches.size(); ++i){

            DataFrame newBatch = new BasicDataFrame();

            for(int j=0; j < k; ++j){
                int d = (i + j) % batches.size();
                DataFrame batch = batches.get(d);
                for(DataRow tuple : batch){
                    newBatch.addRow(tuple.makeCopy());
                }
            }

            newBatches.add(newBatch);
        }
        return newBatches;
    }


    @Override public double transform(DataRow row) {
        String label = classify(row);
        return classLabels.indexOf(label);
    }


    @Override
    public void fit(DataFrame dataFrame) {

        createClassifiers(dataFrame);

        if(shuffleData) {
            dataFrame.shuffle();
        }

        List<DataFrame> batches = split(dataFrame, classifiers.size());

        int k= Math.max(1, (int)alpha * batches.size());
        batches = remerge(batches, k);


        for(int i=0; i < classifiers.size(); ++i){
            TupleTwo<SVR, SVR> pair = classifiers.get(i);
            SVR classifier1 = pair._1();
            SVR classifier2 = pair._2();

            classifier1.fit(createBinaryBatch(batches.get(i), classifier1.getName()));
            classifier2.fit(createBinaryBatch(batches.get(i), classifier2.getName()));
        }

    }

    private DataFrame createBinaryBatch(DataFrame dataFrame, String classLabel){
        DataFrame binaryBatch = new BasicDataFrame();
        for(DataRow row  : dataFrame){
            String label = row.categoricalTarget();
            DataRow rowWithBinaryTargetOutput = row.makeCopy();
            rowWithBinaryTargetOutput.setTargetCell("success", label.equals(classLabel) ? 1.0 : 0.0);
            binaryBatch.addRow(rowWithBinaryTargetOutput);
        }
        return binaryBatch;
    }


    public String classify(DataRow row) {


        Map<String, Integer> scores = score(row);

        String predicatedClassLabel = null;
        int maxScore = 0;
        for(Map.Entry<String, Integer> entry : scores.entrySet()){
            String label = entry.getKey();
            int score = entry.getValue();
            if(score > maxScore){
                maxScore= score;
                predicatedClassLabel = label;
            }
        }

        if(predicatedClassLabel == null) {
            predicatedClassLabel = "NA";
        }

        return predicatedClassLabel;
    }


    public void reset() {
        classifiers.clear();
        classLabels.clear();
    }


    public List<String> getClassLabels() {
        return classLabels;
    }


    public Map<String, Integer> score(DataRow tuple) {

        Map<String, Integer> scores = new HashMap<>();

        for(int i=0; i < classifiers.size(); ++i){
            TupleTwo<SVR, SVR> pair = classifiers.get(i);
            SVR classifier1 = pair._1();
            SVR classifier2 = pair._2();

            double score1 = getClassifierScore(tuple, classifier1);
            double score2 = getClassifierScore(tuple, classifier2);

            if(score1 == score2) continue;

            String winningLabel;
            if(score1 > score2) {
                winningLabel = classifier1.getName();
            }
            else {
                winningLabel = classifier2.getName();
            }
            if(scores.containsKey(winningLabel)){
                scores.put(winningLabel, scores.get(winningLabel) + 1);
            }else {
                scores.put(winningLabel, 1);
            }
        }

        return scores;
    }
}
