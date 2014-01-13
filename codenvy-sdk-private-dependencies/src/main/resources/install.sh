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
tomcatVersion="7.0.50"
tomcatDir="apache-tomcat-"${tomcatVersion}
curl http://apache-mirror.telesys.org.ua/tomcat/tomcat-7/v${tomcatVersion}/bin/apache-tomcat-${tomcatVersion}.zip > $tomcatDir.zip
unzip $tomcatDir
rm apache-tomcat-${tomcatVersion}.zip
mv $tomcatDir tomcat
rm -rf $tomcatDir
