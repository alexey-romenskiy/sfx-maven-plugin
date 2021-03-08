#!/bin/bash

DIR=`mktemp -d`

retval=$?
if [ $retval -ne 0 ] ; then
    echo "Failed to create a temporary directory (exit code: $retval)"
    exit 1
fi

base64 -di << "EOD!" | tar xJ -C "$DIR"
