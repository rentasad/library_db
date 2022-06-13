#! /bin/bash

mvn versions:set -DnewVersion=D2.4.0

#If you made a mistake, do
#  mvn versions:revert
# afterwards, or
mvn versions:commit
# if you're happy with the results.
