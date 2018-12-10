#!/usr/bin/env bash
grep -E "ShotEvent .* true" ../log_ej6_conTiempo.txt | sort -k1