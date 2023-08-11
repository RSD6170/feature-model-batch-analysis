#!/bin/sh
mvn install:install-file -Dfile=lib/de.ovgu.featureide.lib.fm.attributes-v3.9.2.jar -DgroupId=de.ovgu.featureide -DartifactId=lib.fm.attributes -Dversion=3.9.2 -Dpackaging=jar
mvn install:install-file -Dfile=lib/de.ovgu.featureide.lib.fm-v3.9.2.jar -DgroupId=de.ovgu.featureide -DartifactId=lib.fm -Dversion=3.9.2 -Dpackaging=jar
mvn install:install-file -Dfile=lib/antlr-3.4.jar -DgroupId=org.antlr -DartifactId=antlr -Dversion=3.4 -Dpackaging=jar
mvn install:install-file -Dfile=lib/uvl-parser.jar -DgroupId=de.vill -DartifactId=uvl-parser -Dversion=1.0-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=lib/org.sat4j.core.jar -DgroupId=org.sat4j -DartifactId=org.sat4j.core -Dversion=2.3.5 -Dpackaging=jar
