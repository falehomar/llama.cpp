#!/bin/bash

cd `dirname $0`
cd ..

# get -m model parameter otherwise defer to default
if [ "$1" == "-m" ]; then
  MODEL="-m $2 "
fi

llama-cli -m "/Users/e168693/.ollama/models/blobs/sha256-4ad960d180b16f56024f5b704697e5dd5b0837167c2e515ef0569abfc599743c" --color \
    -f ./prompts/reason-act.txt \
    -i --interactive-first \
    --top_k 10000 --temp 0.2 --repeat_penalty 1 -t 7 -c 2048 \
    -r "Question:" -r "Observation:" --in-prefix " " \
    -n -1
