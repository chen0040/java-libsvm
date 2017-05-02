//
// svm_model
//
package com.github.chen0040.libsvm;
public class svm_model implements Cloneable, java.io.Serializable {
	public svm_parameter param;    // parameter
	public int nr_class;        // number of classes, = 2 in regression/one class svm
	public int l;            // total #SV
	public SupportVectorMachineNode[][] SV;    // SVs (SV[l])
	public double[][] sv_coef;    // coefficients for SVs in decision functions (sv_coef[k-1][l])
	public double[] rho;        // constants in decision functions (rho[k*(k-1)/2])
	public double[] probA;         // pariwise probability information
	public double[] probB;
	public int[] sv_indices;       // sv_indices[0,...,nSV-1] are values in [1,...,num_traning_data] to indicate SVs in the train set

	// for classification only

	public int[] label;        // label of each class (label[k])
	public int[] nSV;        // number of SVs for each class (nSV[k])
	// nSV[0] + nSV[1] + ... + nSV[k-1] = l

	public svm_model makeCopy() {
		svm_model clone = new svm_model();
		clone.copy(this);

		return clone;
	}

	public void copy(svm_model rhs){
		param = rhs.param;
		nr_class = rhs.nr_class;
		l = rhs.l;            // total #SV
		SV=new SupportVectorMachineNode[rhs.SV.length][];    // SVs (SV[l])

		for(int i=0; i < rhs.SV.length; ++i){
			SV[i] = new SupportVectorMachineNode[rhs.SV[i].length];
			for(int j=0; j < rhs.SV[i].length; ++j){
				SV[i][j] = rhs.SV[i][j].makeCopy();
			}
		}

		sv_coef = new double[rhs.sv_coef.length][];    // coefficients for SVs in decision functions (sv_coef[k-1][l])
		for(int i=0; i < rhs.sv_coef.length; ++i){
			sv_coef[i] = rhs.sv_coef[i].clone();
		}

		rho = rhs.rho == null ? null : rhs.rho.clone();        // constants in decision functions (rho[k*(k-1)/2])
		probA = rhs.probA == null ? null : rhs.probA.clone();         // pariwise probability information
		probB = rhs.probB == null ? null : rhs.probB.clone();
		sv_indices = rhs.sv_indices == null ? null : rhs.sv_indices.clone();       // sv_indices[0,...,nSV-1] are values in [1,...,num_traning_data] to indicate SVs in the train set

		// for classification only

		label = rhs.label == null ? null : rhs.label.clone();        // label of each class (label[k])
		nSV = rhs.nSV == null ? null : rhs.nSV.clone();        // number of SVs for each class (nSV[k])
	}
}
