#!/bin/sh

# ----------------------------------------------------------------------
# Codenvy SDK install script.
#
# Mandatory command line parameters:
# -----------------------------------
#   path to local Maven repository where libraries should be copied
#
# ----------------------------------------------------------------------

# Copy libraries into local Maven repository
fileRepo="repo"
mvnLocalRepo=$1

if [ "$mvnLocalRepo" = "" ]; then
  echo "ERROR: Maven local repository is not specified"
  exit 1
fi

cp -Rv $fileRepo/* $mvnLocalRepo

# Download and unpack Apache Tomcat for Runner
tomcatName="apache-tomcat-7.0.47"
wget http://apache-mirror.telesys.org.ua/tomcat/tomcat-7/v7.0.47/bin/${tomcatName}.zip
unzip $tomcatName
rm ${tomcatName}.zip
mv $tomcatName tomcat
rm -rf $tomcatName
