#!/bin/bash

for f in $(find assets -name '*.source'); do
  sed -r 's/^([[:space:]]*)(when |broadcast |print |wait |repeat |end of loop)/\1And \2/g' $f > $f.feature;
done
