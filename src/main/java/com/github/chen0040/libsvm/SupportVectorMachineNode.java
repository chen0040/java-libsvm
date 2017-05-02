package com.github.chen0040.libsvm;
public class SupportVectorMachineNode implements java.io.Serializable
{
	public int index;
	public double value;

	public void copy(SupportVectorMachineNode rhs){
		index = rhs.index;
		value = rhs.value;
	}

	public SupportVectorMachineNode makeCopy(){
		SupportVectorMachineNode clone = new SupportVectorMachineNode();
		clone.copy(this);
		return clone;
	}
}
