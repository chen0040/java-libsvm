package com.github.chen0040.libsvm;
public class svm_parameter implements Cloneable,java.io.Serializable
{
	/* svm_type */
	public static final int C_SVC = 0;
	public static final int NU_SVC = 1;
	public static final int ONE_CLASS = 2;
	public static final int EPSILON_SVR = 3;
	public static final int NU_SVR = 4;

	/* kernel_type */
	public static final int LINEAR = 0;
	public static final int POLY = 1;
	public static final int RBF = 2;
	public static final int SIGMOID = 3;
	public static final int PRECOMPUTED = 4;

	public int svm_type;
	public int kernel_type;
	public int degree;	// for poly
	public double gamma;	// for poly/rbf/sigmoid
	public double coef0;	// for poly/sigmoid

	// these are for train only
	public double cache_size; // in MB
	public double eps;	// stopping actionselection
	public double C;	// for C_SVC, EPSILON_SVR and NU_SVR
	public int nr_weight;		// for C_SVC
	public int[] weight_label;	// for C_SVC
	public double[] weight;		// for C_SVC
	public double nu;	// for NU_SVC, ONE_CLASS, and NU_SVR
	public double p;	// for EPSILON_SVR
	public int shrinking;	// use the shrinking heuristics
	public int probability; // do probability estimates

	public void copy(svm_parameter rhs){
		svm_type = rhs.svm_type;
		kernel_type = rhs.kernel_type;
		degree = rhs.degree;	// for poly
		gamma = rhs.gamma;	// for poly/rbf/sigmoid
		coef0 = rhs.coef0;	// for poly/sigmoid

		// these are for train only
		cache_size = rhs.cache_size; // in MB
		eps = rhs.eps;	// stopping actionselection
		C = rhs.C;	// for C_SVC, EPSILON_SVR and NU_SVR
		nr_weight = rhs.nr_weight;		// for C_SVC
		weight_label = rhs.weight_label.clone();	// for C_SVC
		weight = rhs.weight.clone();		// for C_SVC
		nu = rhs.nu;	// for NU_SVC, ONE_CLASS, and NU_SVR
		p = rhs.p;	// for EPSILON_SVR
		shrinking = rhs.shrinking;	// use the shrinking heuristics
		probability = rhs.probability; // do probability estimates
	}

	public svm_parameter makeCopy()
	{
		svm_parameter clone = new svm_parameter();
		clone.copy(this);
		return clone;
	}

}
