#!/usr/bin/env bash
# Ojo con el sort que te ordena por tiempo
grep -E "ShotEvent .* true" ../log_tp2018.txt | grep -E -i "sale"