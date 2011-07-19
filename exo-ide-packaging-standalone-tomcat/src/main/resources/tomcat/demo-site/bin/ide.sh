#!/bin/sh

# Computes the absolute path of eXo
cd `dirname "$0"`

# Sets some variables


# Launches the server
exec "$PRGDIR"./catalina.sh "$@"
