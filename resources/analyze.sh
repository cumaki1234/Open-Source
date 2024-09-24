#!/bin/sh
cat species_out.xml.obs.txt | tr ' ' '\n' | sort | grep -v ":$" | uniq -c | sort
