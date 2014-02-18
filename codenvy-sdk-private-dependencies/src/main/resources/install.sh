#!/bin/sh

# ----------------------------------------------------------------------
# Codenvy SDK install script that parses local settings.xml file, extracts
# path to a local Maven repository and copies private Codenvy libs.
# Run script as is. No additional parameters required.
# ----------------------------------------------------------------------

# Find and parse Maven's settings.xml file. No errors should occur as long as M2_HOME environment variable is set, and settings.xml contains a valid Maven local repo path

if [ "$M2_HOME" = "" ]; then

echo "$(tput setaf 1)ERROR: M2_HOME variable not found! Please, check if $(tput setaf 6)M2_HOME $(tput setaf 1)is correctly set!$(tput setaf 7)"

exit 1

fi

echo "$(tput setaf 2)INFO: Looking for your local Maven repository...$(tput setaf 7)"

FILE2PARSE="$M2_HOME/conf/settings.xml"


if [ ! -f "$FILE2PARSE" ]

then
    echo "$(tput setaf 1)ERROR: Failed to locate $(tput setaf 4)settings.xml $(tput setaf 1)in your Maven's installation directory. Make sure the file exists or has a correct name!$(tput setaf 7)"

exit 1

fi

PARSED_PATH=`tr -d "\r\n\t" < $FILE2PARSE | awk 'ps=index($0,"<localRepository>"),pe=index($0,"</localRepository>") {print substr($0,ps+17,pe-ps-17)}'`


if [ "$PARSED_PATH" = "" ]; then

echo "$(tput setaf 1)ERROR: Corrupt settings.xml! Please, check tags $(tput setaf 6)<localRepository>$(tput setaf 7)path/to/local/repo$(tput setaf 6)</localRepository>$(tput setaf 7)"

exit 1

fi

# Copy libraries to a local Maven repository

fileRepo="repo"
mvnLocalRepo=$PARSED_PATH

if [ ! -d "$PARSED_PATH" ]; then

echo "$(tput setaf 1)ERROR: Local path $(tput setaf 6)'$PARSED_PATH'$(tput setaf 1) specified in your settings.xml does not exist!"$(tput setaf 7)

exit 1

fi
echo "$(tput setaf 2)INFO: Local Maven repository found. Copying libs..."$(tput setaf 7)

cp -Rv $fileRepo/* $mvnLocalRepo

echo $(tput setaf 2)"INFO: Libraries successfully copied"$(tput setaf 7)

# Download and unpack Apache Tomcat for Runner

echo $(tput setaf 2)"INFO: Downloading Apache Tomcat for runners"$(tput setaf 7)

tomcatVersion="7.0.50"
tomcatDir="apache-tomcat-"${tomcatVersion}
curl http://apache-mirror.telesys.org.ua/tomcat/tomcat-7/v${tomcatVersion}/bin/apache-tomcat-${tomcatVersion}.zip > $tomcatDir.zip
unzip $tomcatDir
rm apache-tomcat-${tomcatVersion}.zip
mv $tomcatDir tomcat
rm -rf $tomcatDir
echo $(tput setaf 2)"INFO: Tomcat for runners successfully downloaded"$(tput setaf 7)



