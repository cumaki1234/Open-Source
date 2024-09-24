#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd "${DIR}"
chmod +x kitchenGardenAid.sh
chmod +x java-runtime/bin/java
spctl --add ./java-runtime/bin/java

