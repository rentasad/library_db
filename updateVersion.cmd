@echo off

call mvn versions:set -DnewVersion=D2.6.7

::If you made a mistake, do
:: mvn versions:revert
::afterwards, or
call mvn versions:commit
::if you're happy with the results.
pause