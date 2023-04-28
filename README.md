# Feature-Model Batch Analysis
This repository can be used to analyze a batch of feature models using FeatureIDE. The feature models can be of any format that is supported by FeatureIDE, namely the FeatureIDE format (xml), SXFM, UVL, and DIMACS.
As input a path to either a directory or a feature-model file is expected. If given a directory path, the tool computes the metrics for every feature model in the directory *recursively*.


## How to install

1. Install Maven if necessary `sudo apt-get install maven`
2. Install supplied jars `sh install_jars_in_maven.sh`

## How to run

Clean + Install required dependencies + compile + execute 
`mvn clean compile exec:java`

Execute with args
`mvn clean compile exec:java -Dexec.args="<directory with fms to analyze> <output csv path>"`


## Adapting the Set of Employed Analyses
In `src/main/java/de/uulm/fm/FeatureModelStructureAnalysis.java`, the different analyses to be computed are registered. By removing/adding respective analyses there, the set of computed analyses can be change.

## Adding new Analyses
Each analysis .class requires to implement the IFMAnalysis interface. After implementing the required methods, your new analysis can be registered in `src/main/java/de/uulm/fm/FeatureModelStructureAnalysis.java`.