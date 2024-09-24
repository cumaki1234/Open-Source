#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd "${DIR}"
#--add-opens javafx.controls/javafx.scene.control.cell=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED --add-exports=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED
./java-runtime/bin/java -jar KitchenGardenAid-*.jar
 
