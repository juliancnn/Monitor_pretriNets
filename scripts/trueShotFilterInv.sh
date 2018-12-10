#!/usr/bin/env bash

# Armar JSON a partir del array
json_array() {
  echo -n '['
  while [ $# -gt 0 ]; do
    x=${1//\\/\\\\}
    echo -n ${x//\"/\\\"}
    [ $# -gt 1 ] && echo -n ', '
    shift
  done
  echo ',]'
}

# Log ordenado por tiempo y filtrado solo tiros validos + marcador
log=$(grep -E "ShotEvent .* true" ../log_ej6_conTiempo.txt | sort -k1 | awk -F  "|"  '{print $6 $8}');

#filtramos la lista transiciones disparadas
list=($(echo "$log" | awk '{print $2}'));
json=$(json_array "${list[@]}");

echo "$json"
# Scamos los tinvariantes
echo "$json" | sed -r 's/(1,)(.*^3)(3,)(.*^5)(5,)/\2\4/g'
