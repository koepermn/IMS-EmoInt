import numpy as np
import theano
import math
import csv
import keras
import sklearn
import gensim
import random
import scipy
import keras
from scipy.stats import pearsonr
from scipy.stats import spearmanr
from keras.preprocessing import sequence
from keras.preprocessing.text import Tokenizer
from keras.models import Sequential #, Graph
from keras.layers.core import Dense , Dropout , Activation , Flatten
from keras.layers.convolutional import Convolution1D, MaxPooling1D
from keras.layers import Embedding , Merge, LSTM
from sklearn.base import BaseEstimator
from sklearn.kernel_ridge import KernelRidge
from gensim.models.word2vec import Word2Vec
from gensim.models.doc2vec import Doc2Vec , TaggedDocument
from sklearn import preprocessing
from sklearn.metrics import mean_squared_error , mean_absolute_error


# size of the word embeddings
embeddings_dim = 300
# maximum number of words to consider in the representations
max_features = 20000 #Voc is: 24535 but 10273 only (>2)
# maximum length of a sentence
max_sent_len = 50 # Max 58, Median = 17, avg 16
crossval = 20
## Random Seed
seed = 27
#number of dimensions in regression problem
reg_dimensions = 1

print ("")
print ("Reading pre-trained word embeddings...")
embeddings = dict( )
# Load external word embeddings
embeddings = Word2Vec.load_word2vec_format( "cnn_lstm/twitter_sgns_subset.txt.gz" , binary=False ) 
print ("Reading text data for regression and building representations...")
## We first estimate the entire vocabulary, here we use a file that combines Test&Train data:
full_voc_data  = [ ( row["Tweet"] , float( row["Rating"] )  ) for row in csv.DictReader(open("cnn_lstm/vocab.csv"), delimiter='\t', quoting=csv.QUOTE_NONE) ]
full_data_size = int(len(full_voc_data))
all_texts = [ txt for ( txt, label ) in full_voc_data[0:full_data_size] ]
tokenizer = Tokenizer(num_words=max_features, filters='%&()*+,-./:;<=>[\\]^_`{|}~\t\n',lower=True, split=" ")
tokenizer.fit_on_texts(all_texts) # <-- Tokenizer based on all TEXTS
## NOW LOAD TEST DATA ##
TSdata = [ ( row["Tweet"] , float( row["Rating"] )  ) for row in csv.DictReader(open("cnn_lstm/test/anger_CNN-LSTM_input.txt"), delimiter='\t', quoting=csv.QUOTE_NONE) ]
test_size = int(len(TSdata) )
test_texts = [ txt for ( txt, label ) in TSdata[0:test_size] ]
test_labels = [ label for ( txt , label ) in TSdata[0:test_size] ]
test_sequences = sequence.pad_sequences( tokenizer.texts_to_sequences( test_texts ) , maxlen=max_sent_len )
##############
## we Iterate through all training types, as well as the combination (afjs):
## Note that afjs is (yet) not used by our system.
EMOS="joy fear sadness anger afjs"
for i in EMOS.split():
	currentemo=str(i)
	TRdata = [ ( row["Tweet"] , float( row["Rating"] )  ) for row in csv.DictReader(open("cnn_lstm/train/"+currentemo+"_tr_dv.csv"), delimiter='\t', quoting=csv.QUOTE_NONE) ]
	random.shuffle( TRdata )    
	train_size = int(len(TRdata) )
	train_texts = [ txt for ( txt, label ) in TRdata[0:train_size] ]
	train_labels = [ label for ( txt , label ) in TRdata[0:train_size] ]
	train_sequences = sequence.pad_sequences( tokenizer.texts_to_sequences( train_texts ) , maxlen=max_sent_len )
	embedding_weights = np.zeros( ( max_features , embeddings_dim ) )
	for word,index in tokenizer.word_index.items():
	    if index < max_features:
	        try: embedding_weights[index,:] = embeddings[word]
	        except: embedding_weights[index,:] = np.random.rand( 1 , embeddings_dim )
	np.random.seed(seed)
	filter_length = 3
	nb_filter = embeddings_dim
	pool_length = 2
	model = Sequential()
	model.add(Embedding(max_features, embeddings_dim, input_length=max_sent_len, weights=[embedding_weights]))
	model.add(Dropout(0.25))
	model.add(Convolution1D(nb_filter=nb_filter, filter_length=filter_length, border_mode='valid', activation='relu', subsample_length=1))
	model.add(MaxPooling1D(pool_length=pool_length))
	model.add(LSTM(embeddings_dim))
	model.add(Dense(reg_dimensions))
	model.add(Activation('sigmoid'))
	model.compile(loss='mean_absolute_error', optimizer='adam')
	model.fit( train_sequences , train_labels , nb_epoch=30, batch_size=16)
	model.save("cnn_lstm/models/"+currentemo+".h5")  # creates a HDF5 file 'my_model.h5'
	results = model.predict( test_sequences )
	np.savetxt("cnn_lstm/test/pred/"+currentemo+".txt", results, newline='\n')