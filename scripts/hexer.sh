#!/bin/bash -x

hexdump $1 -e '/1 "%02X"' -v > $2

