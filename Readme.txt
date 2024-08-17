First you need to download the data USA folder from zenodo.org (USA folder here is a just subset of the original dataset)

To run the Basic Index code:
From CMD command editor
1-	To create output file, write: 
ECHO "Exp Output" >> Expermintal\BasicOutput.txt
2-	Enter the following command:
java -jar Expermintal\BasicIndexing.jar ".\USA" 100 1000 100 50 1 5000000 ".\Code\query.txt" >> Expermintal\BasicOutput.txt

where these numbers 100 1000 100 50 1 5000000 represent the parameters
3rd one for K value
4th one for Range value
5th one for number of keywords
6th one for Dataset value
Try more than one value for each parameter.

To run the Hybrid Index code:
From CMD command editor
1-	To create output file, write: 
ECHO "Exp Output" >> Expermintal\HybridOutput.txt
2-	Enter the following command:
java -jar Expermintal\HybridIndexing.jar ".\USA" 100 1000 100 50 1 5000000 ".\Code\query.txt" >> Expermintal\HybridOutput.txt

where these numbers 100 1000 100 50 1 5000000 represent the parameters
3rd one for K value
4th one for Range value
5th one for number of keywords
6th one for Dataset value
Try more than one value for each parameter.
