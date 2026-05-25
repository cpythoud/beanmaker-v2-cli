#!/usr/bin/env bash
set -euo pipefail

iterations=1000

for ((i = 1; i <= iterations; i++)); do
  echo "Run $i of $iterations"
  bm bean show
done
