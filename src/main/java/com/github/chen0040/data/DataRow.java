package com.github.chen0040.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by xschen on 10/7/2016.
 */
public class DataRow implements Serializable {
    private DataColumnCollection columns = new DataColumnCollection();

    private Map<Integer, Double> data = new HashMap<>();

    private String predictedLabel = "";
    private String label = "";

    private double predictedOutputValue = 0;
    private double outputValue = 0;
    private int rowIndex = -1;

    public DataRow(){

    }

    public DataRow(DataColumnCollection columns){
        this.columns = columns;
    }

    public DataRow clone(){
        DataRow clone = new DataRow(columns.clone());
        clone.data.putAll(data);

        clone.predictedLabel = predictedLabel;
        clone.label = label;

        clone.predictedOutputValue = predictedOutputValue;
        clone.outputValue = outputValue;

        clone.rowIndex = rowIndex;

        return clone;
    }

    public Map<Integer, Double> getData() {
        return data;
    }

    public void setData(Map<Integer, Double> data) {
        this.data = data;
    }

    public void setColumns(DataColumnCollection columns) {
        this.columns = columns;
    }

    public String getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(String predictedLabel) {
        this.predictedLabel = predictedLabel;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getPredictedOutputValue() {
        return predictedOutputValue;
    }

    public void setPredictedOutputValue(double predictedOutputValue) {
        this.predictedOutputValue = predictedOutputValue;
    }

    public double getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(double outputValue) {
        this.outputValue = outputValue;
    }

    public DataColumnCollection getColumns() {
        return columns;
    }

    public double cell(String name){
        int columnIndex = columns.indexOf(name);
        return cell(columnIndex);
    }

    public void cell(String name, double value){
        int columnIndex = columns.indexOf(name);
        if(columnIndex != -1) {
            cell(columnIndex, value);
        }
    }

    public void cell(int columnIndex, double value){
        data.put(columnIndex, value);
    }

    public double cell(int columnIndex){
        return data.getOrDefault(columnIndex, 0.0);
    }

    public int columnCount(){
        return columns.columnCount();
    }

    public double[] cells() {
        int count = columns.columnCount();
        double[] values = new double[count];
        for(int i=0; i < count; ++i){
            values[i] = cell(i);
        }

        return values;

    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
