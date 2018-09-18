Source Code In 
       Online BTM based Short Text Stream Classification using Short Text Expansion and Concept Drifting 
	   Detection
	   
The experiment is divided three part:
    1 short text expansion
	2 short text representation
	3 model building and prediction

Quick Start:
=============
1 short text expansion
  1.1 JGibbLDA-v.1.0 is used to train external corpus and infer topic for the data which are short texts, 
  and this is java code 
  Input: the external corpus, data 
  Output: doc-topic distribution, topN words
  1.2 ExtendOrder.java is used to expand short texts
  Input: doc-topic distribution, topN words and data
  Output: expanded data
  
2 short text representation
  2.1 OnlineBTM-master is used to represent expanded data, and this is c code
  Input: expanded data
  Output: expanded short texts are represented as topics vector
  
3 model building and prediction
  3.1 STSC-EandCDD-04.02 is used to build model and predict new data, and this is matlab code
  Input: expanded data
  Output: predictive results
  
All other parameters please refer to the paper "Online BTM based Short Text Stream Classification using Short 
Text Expansion and Concept Drifting Detection"

