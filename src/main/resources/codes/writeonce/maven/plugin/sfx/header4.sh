
retval=$?
if [ $retval -ne 0 ] ; then
    echo "Failed to install (exit code: $retval)"
    exit 1
fi
