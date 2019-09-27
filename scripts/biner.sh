#!/bin/bash

INPUT=$1

while IFS= read -r -n2 char
do
    [ ! -z "$var"] && printf "\x$char"
done < "$INPUT" > $2;


