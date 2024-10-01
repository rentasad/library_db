@echo off
cd ..
git diff --staged >diff.txt
notepad diff.txt