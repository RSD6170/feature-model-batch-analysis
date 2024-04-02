# Feature-Model Batch Analysis
This repository can be used to analyze a batch of feature models using FeatureIDE. The feature models can be of any format that is supported by FeatureIDE, namely the FeatureIDE format (xml), SXFM, UVL, and DIMACS.
As input a path to either a directory or a feature-model file is expected. If given a directory path, the tool computes the metrics for every feature model in the directory *recursively*.

## Usage
Only tested with Ubuntu 22.04LTS, binaries have to be recompiled!

Call FeatureStepAnalysis with 
    Mandatory argument([0]): Input path
    Optional Argument([1]): Analysis category: All (chosen if not set), Default, Simple, CNF, Satzilla
    Optional Argument([2]): Output path results
    Optional Argument([3]): Output path time
    Optional Argument([4]): Output path runstatus

to execute all analysis on all files in the given path recursively.