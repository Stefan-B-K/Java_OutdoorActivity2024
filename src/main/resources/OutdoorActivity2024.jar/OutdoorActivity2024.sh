#!/bin/bash
bash=$$

echo "bash PID $bash"

until java -jar OutdoorActivity2024.jar; do
    echo "OutdoorActivity2024 crashed with exit code $?.  Respawning... " >&2
    sleep 1
done