#!/bin/sh
grep "Name:" SW-Log-added.txt | sed -e "s/Name:\ //g" > SW-names.txt