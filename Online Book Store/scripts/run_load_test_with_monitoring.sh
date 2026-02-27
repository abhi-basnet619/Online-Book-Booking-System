#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RESULTS_ROOT="$ROOT_DIR/target/performance"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
RUN_DIR="$RESULTS_ROOT/$TIMESTAMP"
MONITOR_CSV="$RUN_DIR/resource-monitoring.csv"

DURATION_SECONDS="${DURATION_SECONDS:-300}"
NETWORK_INTERFACE="${NETWORK_INTERFACE:-lo}"

mkdir -p "$RUN_DIR"

echo "[1/4] Starting resource monitoring for ${DURATION_SECONDS}s..."
"$ROOT_DIR/scripts/monitor_resources.sh" "$DURATION_SECONDS" "$MONITOR_CSV" "$NETWORK_INTERFACE" &
MONITOR_PID=$!

cleanup() {
  if kill -0 "$MONITOR_PID" >/dev/null 2>&1; then
    kill "$MONITOR_PID" >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT

echo "[2/4] Running JMeter load test (1000 API calls over 5 minutes)..."
(
  cd "$ROOT_DIR"
  mvn -DskipTests jmeter:jmeter
)

echo "[3/4] Waiting for monitoring process to finish..."
wait "$MONITOR_PID" || true
trap - EXIT

JTL_CSV="$(find "$ROOT_DIR/target/jmeter/results" -maxdepth 1 -type f -name '*.csv' | head -n 1)"
if [[ -z "$JTL_CSV" ]]; then
  echo "Error: Could not find JMeter CSV result under target/jmeter/results"
  exit 1
fi

cp "$JTL_CSV" "$RUN_DIR/jmeter-results.csv"

echo "[4/4] Printing test summary in terminal..."
"$ROOT_DIR/scripts/summarize_jmeter_csv.sh" "$RUN_DIR/jmeter-results.csv" | tee "$RUN_DIR/terminal-summary.txt"

echo "Artifacts saved in: $RUN_DIR"
echo "- JMeter results: $RUN_DIR/jmeter-results.csv"
echo "- Resource metrics: $RUN_DIR/resource-monitoring.csv"
echo "- Terminal summary: $RUN_DIR/terminal-summary.txt"