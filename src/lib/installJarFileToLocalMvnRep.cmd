@echo off
mvn install:install-file -Dfile=adsjdbc11.0.jar -DgroupId=com.extendedsystems.jdbc.advantage -DartifactId=adsjdbc -Dversion=11.0 -Dpackaging=jar
mvn install:install-file -Dfile=adsjdbc11.10.jar -DgroupId=com.extendedsystems.jdbc.advantage -DartifactId=adsjdbc -Dversion=11.10 -Dpackaging=jar
mvn install:install-file -Dfile=adsjdbc12.0.jar -DgroupId=com.extendedsystems.jdbc.advantage -DartifactId=adsjdbc -Dversion=12.0 -Dpackaging=jar
pause