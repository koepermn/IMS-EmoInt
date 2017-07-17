#!/bin/bash
echo "Input file: $1" # Input has to be in the same folder as Arktweet NLP
echo "Output file: ${1}.parsed" #  File will be created in current folder
ARKTWEET_FOLDER="...ark-tweet-nlp-0.3.2;"
cd $ARKTWEET_FOLDER
sh runTagger.sh --quiet --no-confidence --output-format conll ${1} > ${1}.parsed
#options:
#  --model <Filename>        Specify model filename. (Else use built-in.)
# --just-tokenize           Only run the tokenizer; no POS tags.
#--quiet                   Quiet: no output
#--input-format <Format>   Default: auto
#                        Options: json, text, conll
#--output-format <Format>  Default: automatically decide from input format.
#                         Options: pretsv, conll
#--input-field NUM         Default: 1
#                       Which tab-separated field contains the input
#                      (1-indexed, like unix 'cut')
#                     Only for {json, text} input formats.
#--word-clusters <File>    Alternate word clusters file (see FeatureExtractor)
#--no-confidence           Don't output confidence probabilities
#--decoder <Decoder>       Change the decoding algorithm (default: greedy)
