package com.shorttext.extend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

public class ExtendOrder {

	/**dir/filename, model is builded by LDA and data is external
	 *  data(such as wikipedia)*/
	public String thetaFile;
	public String twordsFile;
	
	/**dir/filename, short text set requiring to be enriched*/
	public String textFile;	
	
	/**number of topics in model*/
	public int K;

	/**topic ids that are extended into short text, size: M × 
	 * number of topics extended into short text*/
	public Vector<Integer>[] extendTopicIds;
	
	/**extended texts are top N words according to topic ids, size: M*/
	public String[] extendedTexts;
	
	/**short texts*/
	private Vector<String> texts;
	
	/**document - topic distributions, size M x K*/
	private float[][] pz_d;
	
	private int[][] topic_prob_bydecs;
	
	/**top N words in model*/
	private Vector<String>[] topWords;
	
	/**intervals : [lower bound, upper bound, times]*/
	private Vector<Float[]> intervals;
	
	/**doc.size*/
	private int M;
	
	private int TWnum = -1;
	
	private int selectedKnum = 2;
	
	/**
	 * constructor
	 * @param modelFile
	 * @param textFile
	 * @param k
	 */
	public ExtendOrder(String thetaFile, String twordsFile, String textFile, int k) {
		this.thetaFile = thetaFile;
		this.twordsFile = twordsFile;
		this.textFile = textFile;
		K = k;
		
		//initial parameter
		texts = new Vector<String>();
		topWords = new Vector[K];
		intervals = new Vector<>();
	}
	
	public void extendTexts(String intervalfile)
	{
		int times;
		readFiles(intervalfile);
		//init extendTopicIds
		extendTopicIds = new Vector[M];
		extendedTexts = new String[M];
		//expend short text
		System.out.println("start to extend short text");
		for(int m=0; m<M; m++){
			//by decs
			topic_prob_bydecs[m] = binary_InsertSort(topic_prob_bydecs[m], pz_d[m]);
			for(int i=0; i<selectedKnum; i++){
//				System.out.println(pz_d[m][topic_prob_bydecs[m][i]]);
				times = extendableTimes(m, topic_prob_bydecs[m][i]);
				addTopicIds(m, topic_prob_bydecs[m][i], times);
				addTexts(m, topic_prob_bydecs[m][i], times);
			}
			
		}

	}
	
	public void extendTextsToSnippets(String intervalfile)
	{
		int times;
		readFiles(intervalfile);
		//init extendTopicIds
		extendTopicIds = new Vector[M];
		extendedTexts = new String[M];
		//expend short text
		System.out.println("start to extend short text");
		for(int m=0; m<M; m++){
			//by decs
			topic_prob_bydecs[m] = binary_InsertSort(topic_prob_bydecs[m], pz_d[m]);
			for(int i=0; i<selectedKnum; i++){
				//delete topic44 which is divided into almost short texts
				if(topic_prob_bydecs[m][i] == 44)
					continue;
				times = extendableTimes(m, topic_prob_bydecs[m][i]);
				addTopicIds(m, topic_prob_bydecs[m][i], times);
//				addTexts(m, topic_prob_bydecs[m][i], times);
			}
			
		}
				

	}
	/**
	 * write down extended texts
	 * @param filename of extended texts
	 */
	public void writeExtendedTexts(String filename){
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(filename)));
			
			for(int m=0; m<M; m++){
				String[] words = texts.get(m).split("\t");
				bw.write(words[0]);
				if(words.length>1)
				{
					for(int i=1; i<words.length-1; i++)
					{
						bw.write("\t");
						bw.write(words[i]);
					}
					if(extendedTexts[m] != null)
						bw.write(extendedTexts[m]);
					bw.write("\t");
					bw.write(words[words.length-1]);
				}else
				{
					if(extendedTexts[m] != null)
						bw.write(extendedTexts[m]);
				}
				
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//for snippets
	public void writeExtendedTextsToSnippets(String filename){
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(filename)));
			
			for(int m=0; m<M; m++){
				String[] words = texts.get(m).split("\t");
				bw.write(words[0]);
				if(words.length>1)
				{
					for(int i=1; i<words.length-1; i++)
					{
						bw.write("\t");
						bw.write(words[i]);
					}
					/*if(extendedTexts[m] != null)
						bw.write(extendedTexts[m]);*/
					extendedTexts[m] = "";
					for(int i=0; i<extendTopicIds[m].size(); i++)
					{
						int k = extendTopicIds[m].get(i);
						int num;
						if(TWnum == -1)
							num = topWords[k].size();
						else
							num = TWnum;
						for(int j=0; j<num; j++){
							extendedTexts[m] += topWords[k].get(j);
							extendedTexts[m] += " ";				
						}
					}
										
					if(extendedTexts[m] != null)
						bw.write(extendedTexts[m]);
					
					bw.write(" ");
					bw.write(words[words.length-1]);
				}else
				{
					if(extendedTexts[m] != null)
						bw.write(extendedTexts[m]);
				}
				
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * write down extended ids
	 * @param filename of extended texts
	 */
	public void writeExtendedIds(String filename){
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(filename)));
			
			for(int m=0; m<M; m++){
				String[] words = texts.get(m).split("\t");
				/*for(int i=0; i<words.length-1; i++)
				{
					bw.write(words[i]);
					bw.write("\t");
				}*/
				for(int i=0; i<extendTopicIds[m].size(); i++){
					bw.write(" ");
					bw.write("Topic:"+extendTopicIds[m].get(i));
				}
				bw.write("\t");
				bw.write(words[words.length-1]);
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//for snippets
	public void writeExtendedIdsToSnippets(String filename){
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(filename)));
			
			for(int m=0; m<M; m++){
				String[] words = texts.get(m).split("\t");
				/*for(int i=0; i<words.length-1; i++)
				{
					bw.write(words[i]);
					bw.write("\t");
				}*/
				for(int i=0; i<extendTopicIds[m].size(); i++){
					//snippets
					if(!words[words.length-1].equals("business") && extendTopicIds[m].get(i) == 40)
						continue;
					bw.write(" ");
					bw.write("Topic:"+extendTopicIds[m].get(i));
				}
				bw.write("\t");
				bw.write(words[words.length-1]);
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeSortIdsToSnippets(String filename){
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(filename)));
			
			for(int m=0; m<M; m++)
			{
				int[] topicIds = topic_prob_bydecs[m];
				for(int i=0; i<topicIds.length; i++)
				{
					int id= topicIds[i];
					bw.write("topic"+id+":");
//					new DecimalFormat("0.00000").format(pz_d[m][id])
					bw.write(String.valueOf(new DecimalFormat("0.0000").format(pz_d[m][id])));
					bw.write(" ");
				}
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * the kth topic add to the mth document vector with n times
	 * @param the mth document 
	 * @param the kth topic
	 * @param n times the kth topic appears
	 */
	private void addTopicIds(int m, int k, int n) {
		if(extendTopicIds[m] == null)
			extendTopicIds[m] = new Vector<Integer>();
		for(int i=0; i<n; i++){
			extendTopicIds[m].add(k);
		}
	}

	/**
	 * the kth topic content add to the mth document vector with n times
	 * @param the mth document 
	 * @param the kth topic
	 * @param n times the kth topic appears
	 */
	private void addTexts(int m, int k, int n) {
		
		if(extendedTexts[m]==null)
			extendedTexts[m] = "";
		
		int num = 0;
		if(TWnum == -1)
			num = topWords[k].size();
		else
			num = TWnum;
		
		for(int i=0; i<n; i++){
			for(int j=0; j<num; j++){
				extendedTexts[m] += topWords[k].get(j);
				extendedTexts[m] += " ";				
			}
		}
	}
	/**
	 * a topic appears once or several times on extended texts 
	 * depending on the probability of that topic which interval
	 * @param the mth document 
	 * @param the kth topic
	 * @return times the kth topic appears
	 */
	private int extendableTimes(int m, int k) {
		float upperBound, lowerBound;
		int times;
		for(int i=0; i<intervals.size(); i++){
			lowerBound = intervals.get(i)[0];
			upperBound = intervals.get(i)[1];
			times = intervals.get(i)[2].intValue();
			if((pz_d[m][k]<upperBound)&&(pz_d[m][k]>=lowerBound))
				return times;
		}
		return 0;
	}
	
	public static int[] binary_InsertSort(int[] key, float[] value){
		int high, low, tmp;
		for(int i=1; i<key.length; i++){
			high = i-1;
			low = 0;
			tmp = key[i];
			while(low<=high){
				int mid = (high+low)/2;
				if(value[key[i]]<value[key[mid]]){
					low = mid+1;
				}else{
					high = mid-1;
				}
			}
			int j;
			for(j=i; j>low; j--){
				key[j] = key[j-1];
			}
			key[j] = tmp;
		}
		return key;
	}
	/**
	 * read kinds of files
	 * @param intervalfile, interval file
	 */
	private void readFiles(String intervalfile) {

		
		
		System.out.println("read interval");
		readInterval(intervalfile);
		
		System.out.println("read short text");
		readText(textFile);	
		
		System.out.println("read p(z|d)");
		readTheta(thetaFile);
		
		System.out.println("read top N words under " + K + " topics");
		readTwords(twordsFile);
		
	}

	/**
	 * read texts
	 * @param filename, dir+filename
	 * @return
	 */
	private boolean readText(String filename){
		String line;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File(filename)));
			while((line=br.readLine())!=null){
				texts.add(line);
			}
			M = texts.size();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * read P(z|d)
	 * @param filename
	 * @return
	 */
	private boolean readTheta(String filename){
		
		pz_d = new float[M][K];
		topic_prob_bydecs = new int[M][K];
		String line;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File(filename)));
			for(int m=0; (line=br.readLine())!=null; m++){
				if(m>=M){
					System.err.println("Error:\tnumber of inputing short text doesn't match with model");
					br.close();
					return false;
				}
				String[] probabilities = line.split(" ");
				/**/
				if(K!=probabilities.length){
					System.err.println("Error:\tinput K doesn't match with model");
					br.close();
					return false;
				}
				for(int k=0; k<probabilities.length; k++){
					pz_d[m][k] = Float.valueOf(probabilities[k]);
					topic_prob_bydecs[m][k] = k;
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * read top N words
	 * @param filename, dir+filename
	 * @return
	 */
	private boolean readTwords(String filename){
		String line;
		int k=0;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File(filename)));
			while((line=br.readLine())!=null){
				if(k>K){
					System.err.println("Error:\tinput K doesn't match with model");
					br.close();
					return false;
				}
				if(line.startsWith("Topic")&&line.endsWith("th:")){
					topWords[k++] = new Vector<String>();
					continue;
				}
				line = line.replaceAll("\t", "");
				String word = line.split(" ")[0];
				topWords[k-1].add(word);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * read interval file, which consists of '[lowerbound upperbound) times'
	 * @param filename, dir+filename
	 * @return
	 */
	private boolean readInterval(String filename){
		String line;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File(filename)));
			while((line=br.readLine())!=null){
				String[] values = line.split(" ");
				if(values.length!=3){
					System.err.println("Error:\tInterval type is '[float float) int'");
					br.close();
					return false;
				}
				Float[] valuef = new Float[3];
				valuef[0] = Float.valueOf(values[0].substring(1));
				valuef[1] = Float.valueOf(values[1].substring(0,values[1].length()-1));
				valuef[2] = Float.valueOf(values[2]);
				intervals.add(valuef);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public int getTWnum() {
		return TWnum;
	}

	public void setTWnum(int tWnum) {
		TWnum = tWnum;
	}

	public int getSelectedKnum() {
		return selectedKnum;
	}

	public void setSelectedKnum(int selectedKnum) {
		this.selectedKnum = selectedKnum;
	}
	
	
}
