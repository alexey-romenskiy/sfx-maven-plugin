#!/bin/bash

if [ -z "$BASH_SOURCE" ] ; then
    TO_DIR=()
else
    DIR=`basename "$BASH_SOURCE" .sh`

    if [[ -e "$DIR" ]] ; then
        echo "File or directory \"$DIR\" already exists"
        exit 1
    fi

    mkdir "$DIR"
    retval=$?
    if [ $retval -ne 0 ] ; then
        echo "Failed to create a directory \"$DIR\" (exit code: $retval)"
        exit 1
    fi

    TO_DIR=('-C' "$DIR")
fi

base64 -di << "EOD!" | tar xJ "${TO_DIR[@]}"
