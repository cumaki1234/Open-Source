#!/bin/bash
for LANGUAGE in `ls resources/translation/*.xml | grep -v lang`; do
    echo $LANGUAGE
    LANG=$(basename $LANGUAGE)

    # delete obsolete lines
    PATTERN=""
    for TOKEN in \
            `grep "public String [a-z]*[(][)][ ]*[{] return translate(\"[a-z]*\"[)]; [}]" \
                src1/org/sourceforge/kga/translation/Translation.java |
             grep -o '"[a-z]*"' | tr -d '"'`
    do
        if [ ! -z "$PATTERN" ]; then
            PATTERN="$PATTERN|"
        fi
        PATTERN="$PATTERN\"$TOKEN\""
    done
    for LINE in `egrep -nv "$PATTERN" $LANGUAGE | grep entry | awk -F":" '{print $1}' | sort -nr`; do
        echo -n  "Delete from $LANGUAGE line $LINE: "
        sed -n "${LINE}p" < $LANGUAGE
        sed "${LINE}d" $LANGUAGE > $LANGUAGE.tmp
        mv $LANGUAGE.tmp $LANGUAGE
    done
done
echo "------------------------------"
echo

for LANGUAGE in `ls resources/translation/*.xml | grep -v lang`; do
    LANG=$(basename $LANGUAGE)
    echo $LANG
    grep "key=\"${LANG:0:2}\"" resources/translation/languages.xml   | grep -o ">.*-" | tr -d ">-"

    # adding new lines
    PREV_TOKEN=""
    for TOKEN in \
            `grep "public String [a-z]*[(][)][ ]*[{] return translate(\"[a-z]*\"[)]; [}]" \
                src1/org/sourceforge/kga/translation/Translation.java |
             grep -o '"[a-z]*"' | tr -d '"'`
    do
        if [ `grep $TOKEN $LANGUAGE | wc -l` -ne 0 ]
        then
            PREV_TOKEN="$TOKEN"
            continue
        fi
        if [ -z "$PREV_TOKEN" ]; then
            LINE=`grep -n "<entry key=\"" $LANGUAGE | head -1 | awk -F':' '{ print $1 }'`
        else
            LINE=`grep -n "key=\"$PREV_TOKEN\"" $LANGUAGE | awk -F':' '{ print $1 }'`
            LINE=$(( LINE+1 ))
        fi

        if [ "$LANG" = "en.xml" ]; then
            sed "${LINE}i\
<entry key=\"$TOKEN\">$TOKEN???</entry>
" $LANGUAGE > $LANGUAGE.tmp
            mv $LANGUAGE.tmp $LANGUAGE
        else
            EN=`grep "key=\"$TOKEN\"" resources/translation/en.xml | grep -o ">.*<" | tr -d '<>'`
            echo "$TOKEN : $EN"
        fi


        PREV_TOKEN="$TOKEN"
    done
    echo
done

