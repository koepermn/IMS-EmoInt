#!/bin/bash
### We start with the inputfile for anger

#1] first we parse the file, download tweetNLP

#cd ark-tweet-nlp-0.3.2 # go to folder
# We now create a file based on plain text
# Input one Tweet per line : At the point today where if someone [...]
# Output TweetNLP one token per line
# At	P
# the	D
# point	N
# today	N
# where	R
# if	P
# someone	N
# [...]
sh runTagger.sh --quiet --no-confidence --output-format conll anger_plain.txt > anger_plain.txt.parsed

#---- OPTIONAL ---
#2]  We now run the CNN-LSTM, this is done in the keras_regression folder
# Put a input file into the cnn_lstm/test/ folder that contains two clumns 'Tweet' and 'Rating' (see example)
# Now adjust the paths in the 'run_CNN-LSTM.py' (vectors, train-test) 
# note that the provided vectors here cover only the required subset 
# Run the model
python run_CNN-LSTM.py
# This file creates an output file for each of four emotions (anger, joy, fear, sadness) as well as a combined (afjs)
# the output predictions are written into cnn_lstm/test/pred

#3] Combine the predictions in a single file
cd cnn_lstm/test/pred;
paste anger.txt joy.txt fear.txt sadness.txt > afjs.out.txt # <-- tab seperated
#---------------
#4] Create arff file based on the created information
# Here we use the createarff.jar script (you can also modify it, see the orginal code in the createarff_java folder)
# The script requires 3 arguments (4 when using also the CNN-LSTM output as feature) 
# 1:<Input File parsed,  1 Word per Line>
# 2:<Input File Orig, <Tweet TAB Rating>, with Header>
# 3:<Ratings File txt or txt.gz>
# 4:Optional[CNN-lSTM output, 4 numbers p line: anger fear  joy sadness]
# e.g you can call the script
java -jar createarff.jar anger_plain.txt.parsed anger_CNN-LSTM_input.txt .extRatings.csv.gz afjs.out.txt 
# the script will output'anger_plain.txt.parsed.arff' in the current folder

#5] Use Affective Tweets to add Baselinefeatures
# This can be done via GUI or via command line (see : https://github.com/felipebravom/AffectiveTweets)
# We use following to filters:
# -F \"weka.filters.unsupervised.attribute.TweetToSentiStrengthFeatureVector -I 1 -U -O\" 
# -F \"weka.filters.unsupervised.attribute.TweetToLexiconFeatureVector -I 1 -A -D -F -H -J -L -N -P -Q -R -T -U -O\" 
# Using the GUI , load the arff file and click on weka.filters.unsupervised.attribute.
# first execute TweetToSentiStrengthFeatureVector, change textindex to 3
# second execute TweetToLexiconFeatureVector, change textindex to 3
# --> safe the resulting arff file


#6]Finally you can execute the Random Forest, we provide two scripts for this
# Only evaluation 
sh run_RandomForest_eval.sh official_train_arff/ anger_plain.txt.parsed_withAT.arff 
# Save predictions to file
sh run_RandomForest_save-predictions.sh official_train_arff/ anger_plain.txt.parsed_withAT.arff 
