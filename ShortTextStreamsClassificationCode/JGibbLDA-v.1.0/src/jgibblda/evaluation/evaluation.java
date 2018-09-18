package jgibblda.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import jgibblda.Model;
import jgibblda.Pair;


public class evaluation {

	protected Model model;	
	protected String modelName;
	protected int K;
	
	/**twords under topics*/
	protected Vector<Integer>[] twordsV;
	
	/**different words under documents*/
	protected Vector<Integer>[] docWords;
	
	//tmp variable
	private int twordsN = 0;

	
	//---------------------------------------------------------------
	// constructor
	//---------------------------------------------------------------
	public evaluation(Model model) {
		this.model = model;
		K = model.K;
		twordsV = new Vector[K];
		docWords = new Vector[model.data.docs.length];
		
	}
	
	public evaluation(String modelName, int K) {
		this.modelName = modelName;
		this.K = K;
		twordsV = new Vector[model.K];
	}
	
	//-------------------------------------------------------------
    //Private Instance Methods
	//-------------------------------------------------------------
	/** evaluate BTM
	 *  calculate the average coherence score for topics
	 */
    public double evaluation(){
    	getTwordsV();
    	getDocWords();
    	
		double aveCoScore = 0;
		for(int k=0; k<K; k++){
			aveCoScore += CoScore(k);
		}
		aveCoScore = aveCoScore/(double)K;
		
		return aveCoScore;
	}
	
    
    /**calculate the coherence score for topic*/
	private double CoScore(int k) {
		
		double coscore = 0;
		
		for(int m=1; m<twordsN; m++){
			
			for(int l=0; l<m; l++){
//				System.out.println("m= "+m+" l= "+l);
				double tmp = Math.log( ((double)D(twordsV[k].get(m), twordsV[k].get(l))+1)/D(twordsV[k].get(l)) );
				coscore += tmp;
//				System.out.println("   tmp= "+tmp);
			}
		}
//		System.out.println(k+" :coscore= "+coscore);
		return coscore;
	}


	/**calculate the number of documents words Vm and Vl co-occurred*/
	private int D(int vm, int vl) {
		
		int rel = 0;
		for(int d=0; d<docWords.length; d++){
			
			if(findWords(d, vm, vl))
				rel++;
		}
		
		return rel;
	}
	


	/**calculate the document frequency of word V*/
	private int D(int v) {
		
		int rel = 0;
		for(int d=0; d<docWords.length; d++){
			
			if(findword(d, v))
				rel++;
		}
		
		return rel;
	}
	
	/**find word*/
	private boolean findWords(int d, int m, int l) {
		
		if(findword(d, m) && findword(d, l))
			return true;
		
		return false;
	}

	private boolean findword(int d, int v) {
		
		for(int i=0; i<docWords[d].size(); i++){
			if(docWords[d].get(i) == v)
				return true;
		}
		return false;
	}

	/**get twords from model*/
	private void getTwordsV(){
		
		twordsN = model.twords;
		
		for (int k = 0; k < K; k++){
			twordsV[k] = new Vector<Integer>();
			List<Pair> wordsProbsList = new ArrayList<Pair>(); 
			for (int w = 0; w < model.V; w++){
				Pair p = new Pair(w, model.phi[k][w], false);
				
				wordsProbsList.add(p);
			}//end foreach word
			
			//print topic				
			Collections.sort(wordsProbsList);
			
			for (int i = 0; i < twordsN; i++){
				
				if (model.data.localDict.contains((Integer)wordsProbsList.get(i).first)){
					twordsV[k].add((Integer)wordsProbsList.get(i).first);
				}
			}
		} //end foreach topic	
		
	}
	
	/**get words id from model in documnent*/
	private void getDocWords(){
		
		for(int m=0; m<model.data.docs.length; m++){
			
			docWords[m] = new Vector<Integer>();
			for(int v=0; v<model.data.docs[m].words.length; v++){
				
				docWords[m].add(model.data.docs[m].words[v]);
			}
		}
	}
	
	
	
}
