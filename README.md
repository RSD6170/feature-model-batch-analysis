# Feature-Model Batch Analysis
This repository can be used to analyze a batch of feature models using FeatureIDE. The feature models can be of any format that is supported by FeatureIDE, namely the FeatureIDE format (xml), SXFM, UVL, and DIMACS.
As input a path to either a directory or a feature-model file is expected. If given a directory path, the tool computes the metrics for every feature model in the directory *recursively*.

## Building

The project is built using gradle. While you can use the provided wrapper `./gradlew`, you still need to [install gradle](https://docs.gradle.org/current/userguide/installation.html) on your system.

For model counting, we use d4 for which we only provide a Linux binary. [Here](https://github.com/SoftVarE-Group/d4v2), we provide pre-built binaries for Windows, Mac, and Linux.

## Usage
Only tested with Ubuntu 22.04LTS, binaries have to be recompiled!

To run use `./gradlew run --args=<path> [OPTIONS]`

Available options are:
* Mandatory argument([0]): Input path
* Optional Argument([1]): Analysis category: All (chosen if not set), Default, Simple, CNF, Satzilla
* Optional Argument([2]): Output path results
* Optional Argument([3]): Output path time
* Optional Argument([4]): Output path runstatus

An example executing all default analyses on all models in `models`:

`./gradlew run --args=models default`
