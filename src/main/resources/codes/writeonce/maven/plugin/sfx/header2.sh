EOD!

for retval in "${PIPESTATUS[@]}" ; do
    if [ $retval -ne 0 ] ; then
        echo "Failed to unpack (exit code: $retval)"
        exit 1
    fi
done
