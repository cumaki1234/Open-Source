#!/bin/sh
grep -v "<nutritional-needs" species_out.xml | grep -v "<companion " | grep -v "<root " | grep -v "<lifetime " | grep -v "<weed " > obs.xml
