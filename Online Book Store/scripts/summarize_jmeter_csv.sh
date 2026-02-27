#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <jmeter-results.csv>"
  exit 1
fi

CSV_FILE="$1"

if [[ ! -f "$CSV_FILE" ]]; then
  echo "Error: File not found: $CSV_FILE"
  exit 1
fi

awk -F, '
NR==1 {
  for (i=1; i<=NF; i++) {
    gsub(/^[[:space:]]+|[[:space:]]+$/, "", $i)
    if ($i=="elapsed") elapsedCol=i
    if ($i=="success") successCol=i
    if ($i=="label") labelCol=i
    if ($i=="responseCode") codeCol=i
  }
  next
}
{
  total++
  elapsed=$elapsedCol+0
  sum+=elapsed
  values[total]=elapsed

  if ($successCol=="true") pass++
  else fail++

  labels[$labelCol]++
  codes[$codeCol]++
}
END {
  if (total==0) {
    print "No result rows found in CSV."
    exit 0
  }

  # sort values for percentile calculations
  for (i=1; i<=total; i++) {
    for (j=i+1; j<=total; j++) {
      if (values[i] > values[j]) {
        temp=values[i]; values[i]=values[j]; values[j]=temp
      }
    }
  }

  p50=values[int((total*0.50)+0.5)]
  p95=values[int((total*0.95)+0.5)]
  p99=values[int((total*0.99)+0.5)]

  printf "\nJMeter Terminal Summary\n"
  printf "========================\n"
  printf "Total Requests : %d\n", total
  printf "Passed         : %d\n", pass
  printf "Failed         : %d\n", fail
  printf "Avg Latency    : %.2f ms\n", sum/total
  printf "P50 Latency    : %d ms\n", p50
  printf "P95 Latency    : %d ms\n", p95
  printf "P99 Latency    : %d ms\n", p99

  printf "\nRequests by label:\n"
  for (k in labels) printf "- %s: %d\n", k, labels[k]

  printf "\nResponse codes:\n"
  for (k in codes) printf "- %s: %d\n", k, codes[k]

  printf "\n"
}
' "$CSV_FILE"