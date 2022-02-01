@echo off

mvn versions:set -DnewVersion=D2.1.3

::If you made a mistake, do
:: mvn versions:revert
::afterwards, or
::  mvn versions:commit
::if you're happy with the results.