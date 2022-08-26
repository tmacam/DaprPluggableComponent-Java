#!/bin/sh

# Waits for a ${DAPR_COMPONENT_SOCKET_PATH} file to show up.
#
# This was made simple on purpose.
#
# For something more robust, take a look at
# https://github.com/vishnubob/wait-for-it and
# https://github.com/eficode/wait-for

set -eu

TIMEOUT=60

echo "Will wait for ${DAPR_COMPONENT_SOCKET_PATH} for up to ${TIMEOUT} seconds" >&2

wait() {
    for attempt in  $(seq  $TIMEOUT); do
        if test -S ${DAPR_COMPONENT_SOCKET_PATH}; then
            echo "Found ${DAPR_COMPONENT_SOCKET_PATH} !" >&2
            return 0
        else
            echo "File ${DAPR_COMPONENT_SOCKET_PATH} still not there... " \
                "Attempt ${attempt} of $TIMEOUT" >&2
        fi 

        sleep 1
    done
    
    echo "Operation timed out" >&2
    exit 1
}


wait

exec "$@"