# IMS-EmoInt
This repository contains the IMS System submission for the WASSA-2017 Shared Task on Emotion Intensity (EmoInt)

![image](logo.png = 250x)

**Requirements**:

1] You need to have weka installed
http://www.cs.waikato.ac.nz/ml/weka/

2] We use Keras with Thenao backend for our Regression feature

https://keras.io/
http://deeplearning.net/software/theano/

3] We need Lemma and Part-of-Speech tags, these were obtained using the Tweet NLP 
Download: http://www.cs.cmu.edu/~ark/TweetNLP/ (we used ark-tweet-nlp-0.3.2) 

4] Parts of the features were created by the Baseline System (Affective Tweets)
https://github.com/felipebravom/AffectiveTweets

5] The extended ratings (created by us) are available for download here (65MB):
http://www.ims.uni-stuttgart.de/forschung/ressourcen/experiment-daten/IMS_emoint_norms.tar.gz



-- Example Usage ---

Aussming you want to use IMS to predict intensity prediction for a given input file.
We provide a full pipeline for the example in the folder:
_run_through_example/anger_example/anger_plain.txt_
Note that you need to ajdust all the paths with respect to the required tools (TwitterNLP, weka, ...).
Then you need to to the following steps
1) *Parse the input file*
  - using a plain text file you can run _scripts/run_LemmaPOS.sh_
2) Run the CNN-LSTM Regression model
  - The scripts trains one model per emotion for the given test file
  - Note that we provide here only a subset of our vectors
  - _keras_regression/twitter_sgns_subset.txt.gz_ covers the shared task vocabulary
  - For further processing we create one file containing all the predictions
3) Create an Inputfile for weka
  - this can be done using the _scripts/createarff.jar_ (fulll code _scripts/createarff_java/_)
  - Run _java -jar createarff.jar <parsedFile> <inputfile w.Ratings> ratings/Ratings.csv.gz <CNN-LSTM output>_
4) Run wekas Random Forest
 - _scripts/run_RandomForest_eval-only.sh_ or _scripts/run_RandomForest_save-predictions.sh_
 - To apply the script link to the folder from the training (arff) files (_official_train_arff/_)
   
A full and more detailed example how to use our system can be seen in the _run_full.sh_ script. 

Contact: maximilian.koeper AT gmail.com
