# IMS-EmoInt
This repository contains the IMS System submission for the WASSA-2017 Shared Task on Emotion Intensity (EmoInt)<br />
**Task:** Given a **tweet** and an **emotion** x {anger,fear,joy,sadness}, determine the intensity or degree of emotion X felt by the speaker -- a **real-valued score between 0 and 1**.

[The offical shared task paper](http://www.romanklinger.de/publications/emo_ims_kkk.pdf)

<p align="center">
<a href="url"><img src="https://github.com/koepermn/IMS-EmoInt/blob/master/logo.png" align="center" height="250" width="250" ></a>
</p>


## -- Requirements:

1] We make use of **weka** for extracting baseline features and performing the random forest regression
http://www.cs.waikato.ac.nz/ml/weka/

2] We use **Keras** with **Thenao** backend for our Regression feature base on embeddings

https://keras.io/
http://deeplearning.net/software/theano/

3] We need **Lemma** and **Part-of-Speech** tags, these were obtained using the **TweetNLP**
Download: http://www.cs.cmu.edu/~ark/TweetNLP/ (we used ark-tweet-nlp-0.3.2) 

4] Some features were created by the Baseline System (**Affective Tweets**), which is an additional weka package
https://github.com/felipebravom/AffectiveTweets

5] **The Automatically Extended Norms** (created by us) are available for download here (65MB):
http://www.ims.uni-stuttgart.de/forschung/ressourcen/experiment-daten/IMS_emoint_norms.tar.gz
this file contains exntendes resources of affective norms and emotion lexicons.  <br /> *Note: if you use the extended norms  please cite our work **and** the orgininal resource (see paper for reference details)*

## Example Usage:

Assuming you want to use IMS to predict intensity prediction for a given input file.
We provide a full pipeline for the example in the folder:
`run_through_example/anger_example/anger_plain.txt`
Note that you need to ajdust multiple paths with respect to the required tools (TwitterNLP, weka, ...) according to your local machine.
Then you need to do the following steps <br />
######  1) Parse the input file <br />
  - using a plain text file you can run `scripts/run_LemmaPOS.sh`
  - This will transform a one sentence/tweet per line format into a one word per line format
    
###### 2) Run the CNN-LSTM Regression model <br />
  - The scripts trains one model per emotion for the given test file
  - By default we rely on the official training data for training
  - Note that we provide here only a subset of our vectors 
  - _keras_regression/twitter_sgns_subset.txt.gz_ covers the shared task vocabulary
  - vectors are in word2vec format (can be gz, txt or binary)
  - The output of the regression is a single file per training emotion, for further processing we create one file containing    all four predictions by using `paste anger.txt. fear.txt. joy.txt sadness.txt  > afjs.txt`
     
###### 3) Create an Inputfile for weka <br/>
  - this can be done using the `scripts/createarff.jar` (fulll code `scripts/createarff_java/`)
  - This step combines the previous steps
  - Run `java -jar createarff.jar <parsedFile> <inputfile w.Ratings> ratings/Ratings.csv.gz <CNN-LSTM output>`
###### 4) Add Baseline features from Affective tweets <br/>
 - Using the GUI or the command line we add the features from AffectiveTweets
 - We apply default settings of `TweetToSentiStrengthFeatureVector` & `TweetToLexiconFeatureVector`
   
###### 5) Run wekas Random Forest <br />
 - `scripts/run_RandomForest_eval-only.sh` or `scripts/run_RandomForest_save-predictions.sh`
 - To apply the script link to the folder from the training (arff) files (`official_train_arff/`)
  <br />
   A full and more detailed description for using IMS emotion prediction can can be seen in the `run_full.sh` script. 

## Citation info
If you use the code or the created feature norms, please [cite our paper (Bibtex)](http://www.romanklinger.de/publications/2017_bib.html#Koeper2017) [PDF](http://www.romanklinger.de/publications/emo_ims_kkk.pdf)
 
## Contact info
Contact: maximilian.koeper AT ims.uni-stuttgart.de  <br />

[Project Homepage](http://www.ims.uni-stuttgart.de/data/ims_emoint)<br />
[University Homepage Maximilian](http://www.ims.uni-stuttgart.de/institut/mitarbeiter/koepermn/index.en.html)  <br />
[University Homepage Roman](http://www.ims.uni-stuttgart.de/institut/mitarbeiter/klingern/)  <br />
[University Homepage Evgeny](http://www.ims.uni-stuttgart.de/institut/mitarbeiter/kimey)  <br />


