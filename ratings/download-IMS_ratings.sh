#!/bin/bash

echo "Downloading IMS_emoint_norms ratings (65MB)"
if [ -e IMS_emoint_norms.tar.gz ] 
then
    echo "IMS_emoint_norms.tar.gz exists already"
else wget http://www.ims.uni-stuttgart.de/forschung/ressourcen/experiment-daten/IMS_emoint_norms.tar.gz
fi