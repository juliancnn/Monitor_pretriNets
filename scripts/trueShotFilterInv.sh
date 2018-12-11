#!/usr/bin/env bash


# Log ordenado por tiempo y filtrado solo tiros validos + marcador
# Filtrar y cortar en tal linea
#log=$(grep -E "ShotEvent .* true" ../log_tp2018.txt | sort -k1 | awk -F  "|"  '{print $6 $8}' | head -n200)
#log=$(grep -E "ShotEvent .* true" ../log_tp2018.txt | sort -k1 | awk -F  "|"  '{print $6}' | head -n200 | awk '{print $2}' |  sort -k2 | uniq -c);
log=$(grep -E "ShotEvent .* true" ../log_tp2018.txt | sort -k1 | awk -F  "|"  '{print $6}' | awk '{print $2}' |  sort -k2 | uniq -c);
echo "$log"


