#!/bin/bash
# TODO CHANGE PATH ACCORDINGT TO YOUR MACHINE!
export CLASSPATH=.....weka-3-9-1/weka.jar
echo "Train Folder: $1"
echo "Test Input: $2"
## This executes weka + saves the output to a CSV file including Tweet ID and tweet (String)
ITR_RF="800"  #<- we increased number of trees (iterations)
for Emo in "anger" "fear" "joy" "sadness"
	do	 
	 TEST="${2}"
	 TRAIN="${1}${Emo}12.arff"
	 echo "Now at ${Emo}  ${TEST}"
	 java weka.Run weka.classifiers.meta.FilteredClassifier \
	 -F "weka.filters.unsupervised.attribute.RemoveType -T string" \
	 -t ${TRAIN} -T ${TEST} -c 1 -no-cv \
	 -W weka.classifiers.trees.RandomForest -- -P 100 -I ${ITR_RF} -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1;
	 #-classifications "weka.classifiers.evaluation.output.prediction.CSV -use-tab -p 2,3 -file ${2}-${Emo}-pred.txt" \

	done
