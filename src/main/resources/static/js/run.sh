#!/bin/bash

docker run --rm -it --net=host \
       -v `pwd`:/home/work \
       -w /home/work \
       -e DEBUG=$DEBUG \
       bintools-build:nodejs "$@"
