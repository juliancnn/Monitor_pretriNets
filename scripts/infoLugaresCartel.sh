#!/usr/bin/env bash
mark=$(sort -k1 -k2 ../log_tp2018.txt | grep -E -i "shotevent .* true" | awk -F "|" '{print $1 $3 $8}')
echo "$mark"

