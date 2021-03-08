
if [ ! -z "$DIR" ] ; then
    cd "$DIR"
    retval=$?
    if [ $retval -ne 0 ] ; then
        echo "Failed to change the current directory (exit code: $retval)"
        exit 1
    fi
fi

