#!/bin/bash

ARCHIVE=`awk '/^__ARCHIVE_BELOW__/ {print NR + 1; exit 0; }' $0`
DIR=`basename $0 .sh`

if [[ -e $DIR ]] ; then
    echo "File or directory \"$DIR\" already exists"
    exit 1
fi

mkdir $DIR
retval=$?
if [ $retval -ne 0 ] ; then
    echo "Failed to create directory \"$DIR\" (exit code: $retval)"
    exit 1
fi

tail -n+$ARCHIVE $0 | base64 -di | tar xJ -C $DIR
retval=$?
if [ $retval -ne 0 ] ; then
    echo "Failed to unpack (exit code: $retval)"
    exit 1
fi

cd $DIR
retval=$?
if [ $retval -ne 0 ] ; then
    echo "Failed to change the current directory (exit code: $retval)"
    exit 1
fi

