#! /bin/bash
cd ..
mvn versions:set -DnewVersion=D2.5.4

#If you made a mistake, do
#  mvn versions:revert
# afterwards, or
mvn versions:commit
# if you're happy with the results.
