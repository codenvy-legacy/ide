#!/bin/sh

# ----------------------------------------------------------------------
# Codenvy Platform SDK runtime libraries copy script.
#
# Optional command line parameters:
# ---------------------------------
#   path to local Maven repository where libraries should be copied
#
# ----------------------------------------------------------------------

fileRepo="repo"
mvnLocalRepo=$1

if [ "$mvnLocalRepo" = "" ]; then
  echo "ERROR: Maven local repository is not specified"
  exit 1
fi

cp -Rv $fileRepo/* $mvnLocalRepo
