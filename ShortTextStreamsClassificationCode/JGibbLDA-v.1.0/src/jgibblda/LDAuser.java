package jgibblda;

import jgibblda.evaluation.evaluation;

public class LDAuser {

	private double alpha = 0.5;
	private double beta = 0.01;
	private int niters = 1000;
	private int savestep = 100;
	private int twords = 100;
	private String dir = "";
	private String dfile = "";
	private int K = 100;
	private boolean est = true;
	private boolean estc = false;
	private boolean inf = false;
	private String modelName = "";

	
	public LDAuser() {
	}
	
	
	public void start() {
		// TODO Auto-generated method stub
		
		LDACmdOption ldaOption = new LDACmdOption(); 
		ldaOption.alpha = (double)50/K;
		ldaOption.beta = beta; 
		ldaOption.niters = niters; 
		ldaOption.savestep = savestep;
		ldaOption.twords = twords;
		ldaOption.dir = dir; 
		ldaOption.dfile = dfile;
		ldaOption.K = K;
		ldaOption.est = est;
		ldaOption.estc = estc;
		ldaOption.inf = inf;
		ldaOption.modelName = modelName;
		if (ldaOption.est || ldaOption.estc){

			long a=System.currentTimeMillis();
			Estimator estimator = new Estimator();
			estimator.init(ldaOption);
			estimator.estimate();
			/*
			evaluation ev = new evaluation(estimator.trnModel);
			System.out.println(" = "+ev.evaluation());
			*/
			System.out.println("\r<br>执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
		}
		else if (ldaOption.inf){
			Inferencer inferencer = new Inferencer();
			inferencer.init(ldaOption);
				
			Model newModel = inferencer.inference();
			
			for (int i = 0; i < newModel.phi.length; ++i){
				//phi: K * V
				System.out.println("-----------------------\ntopic" + i  + " : ");
				for (int j = 0; j < 10; ++j){
					System.out.println(inferencer.globalDict.id2word.get(j) + "\t" + newModel.phi[i][j]);
				}
			}
		}
	}
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public double getBeta() {
		return beta;
	}
	public void setBeta(double beta) {
		this.beta = beta;
	}
	public int getNiters() {
		return niters;
	}
	public void setNiters(int niters) {
		this.niters = niters;
	}
	public int getSavestep() {
		return savestep;
	}
	public void setSavestep(int savestep) {
		this.savestep = savestep;
	}
	public int getTwords() {
		return twords;
	}
	public void setTwords(int twords) {
		this.twords = twords;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getDfile() {
		return dfile;
	}
	public void setDfile(String dfile) {
		this.dfile = dfile;
	}
	public int getK() {
		return K;
	}
	public void setK(int k) {
		K = k;
	}
	public boolean isEst() {
		return est;
	}
	public void setEst(boolean est) {
		this.est = est;
	}
	public boolean isInf() {
		return inf;
	}
	public void setInf(boolean inf) {
		this.inf = inf;
	}


	public boolean isEstc() {
		return estc;
	}


	public void setEstc(boolean estc) {
		this.estc = estc;
	}


	public String getModelName() {
		return modelName;
	}


	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	
}
