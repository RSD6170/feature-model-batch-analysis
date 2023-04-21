## How to install

1. Install Maven if necessary `sudo apt-get install maven`
2. Install supplied jars `sh install_jars_in_maven.sh`

## How to run

Clean + Install required dependencies + compile + execute 
`mvn clean compile exec:java`

Execute with args
`mvn clean compile exec:java -Dexec.args="<directory with fms to analyze> <output csv path>"`