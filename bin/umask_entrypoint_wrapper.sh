#!/bin/sh

set -xeu

# Ensure that files created by subprocess (like our unix domain socket)
# are readable by everyone and by folks outside this container.
# This will ensure that Dapr container logic will be able to communicate
# with the pluggable component within this container.
umask 000

# Now, do the wrapper thing: execute the target app.
exec "$@"