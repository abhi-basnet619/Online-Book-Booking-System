#!/usr/bin/env bash
set -euo pipefail

DURATION_SECONDS="${1:-300}"
OUTPUT_FILE="${2:-resource-monitoring.csv}"
INTERFACE="${3:-lo}"

printf 'timestamp,cpu_usage_percent,load1,mem_used_mb,mem_available_mb,net_rx_kbps,net_tx_kbps\n' > "$OUTPUT_FILE"

read -r rx_prev tx_prev < <(awk -v iface="$INTERFACE" '$1 ~ iface":" {gsub(":","",$1); print $2, $10}' /proc/net/dev)
read -r total_prev idle_prev < <(awk '/^cpu / {total=$2+$3+$4+$5+$6+$7+$8; print total, $5}' /proc/stat)

for ((i=0; i< DURATION_SECONDS; i++)); do
  sleep 1

  read -r total idle < <(awk '/^cpu / {total=$2+$3+$4+$5+$6+$7+$8; print total, $5}' /proc/stat)
  total_diff=$((total-total_prev))
  idle_diff=$((idle-idle_prev))
  cpu_usage=$(awk -v t="$total_diff" -v id="$idle_diff" 'BEGIN {if (t==0) printf "0.00"; else printf "%.2f", (100*(t-id)/t)}')
  total_prev=$total
  idle_prev=$idle

  mem_total_kb=$(awk '/MemTotal/ {print $2}' /proc/meminfo)
  mem_available_kb=$(awk '/MemAvailable/ {print $2}' /proc/meminfo)
  mem_used_mb=$(awk -v t="$mem_total_kb" -v a="$mem_available_kb" 'BEGIN {printf "%.2f", (t-a)/1024}')
  mem_available_mb=$(awk -v a="$mem_available_kb" 'BEGIN {printf "%.2f", a/1024}')

  load1=$(awk '{print $1}' /proc/loadavg)

  read -r rx tx < <(awk -v iface="$INTERFACE" '$1 ~ iface":" {gsub(":","",$1); print $2, $10}' /proc/net/dev)
  rx_rate=$(awk -v curr="$rx" -v prev="$rx_prev" 'BEGIN {printf "%.2f", ((curr-prev)*8)/1024}')
  tx_rate=$(awk -v curr="$tx" -v prev="$tx_prev" 'BEGIN {printf "%.2f", ((curr-prev)*8)/1024}')
  rx_prev=$rx
  tx_prev=$tx

  timestamp=$(date '+%Y-%m-%d %H:%M:%S')
  printf '%s,%s,%s,%s,%s,%s,%s\n' "$timestamp" "$cpu_usage" "$load1" "$mem_used_mb" "$mem_available_mb" "$rx_rate" "$tx_rate" >> "$OUTPUT_FILE"
done