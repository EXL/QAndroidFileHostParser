#!/bin/sh
grep "Name:" SW-Log.txt | sed -e "s/Name:\ //g" > SW-names.txt