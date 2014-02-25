#!/bin/sh

# ----------------------------------------------------------------------
# Codenvy SDK install script that parses local settings.xml file, extracts
# path to a local Maven repository and copies private Codenvy libs.
# Run script as is. No additional parameters required.
# ----------------------------------------------------------------------

# Download and unpack Apache Tomcat for Runner

echo "$(tput setaf 2)INFO: Downloading Apache Tomcat for runners"$(tput setaf 7)
tomcatVersion="7.0.52"
tomcatDir="apache-tomcat-"${tomcatVersion}
curl http://apache-mirror.telesys.org.ua/tomcat/tomcat-7/v${tomcatVersion}/bin/apache-tomcat-${tomcatVersion}.zip > $tomcatDir.zip
unzip $tomcatDir
rm apache-tomcat-${tomcatVersion}.zip
mv $tomcatDir tomcat
rm -rf $tomcatDir
echo "$(tput setaf 2)INFO: Tomcat $tomcatVersion for runners successfully downloaded"$(tput setaf 7)

# If there's no settings.xml in $HOME/.m2 the script looks for this file at $M2_HOME/conf

FILE2PARSE_DEFAULT="$HOME/.m2/settings.xml"
if [ ! -f "$FILE2PARSE_DEFAULT" ]
then

# If M2_HOME variable is not set

if [ "$M2_HOME" = "" ]; then
echo "$(tput setaf 1)ERROR: M2_HOME variable not found! Please, check if $(tput setaf 6)M2_HOME $(tput setaf 1)is correctly set!$(tput setaf 7)"
exit 1
fi

# Looking for settings.xml in $M2_HOME/conf


FILE2PARSE="$M2_HOME/conf/settings.xml"
if [ ! -f "$FILE2PARSE" ]
then
echo "$(tput setaf 1)ERROR: Failed to locate $(tput setaf 4)settings.xml $(tput setaf 1)both at $(tput setaf 4)$HOME/.m2$(tput setaf 1) and $(tput setaf 4)$M2_HOME/conf$(tput setaf 1). Make sure the file exists or has a correct name!$(tput setaf 7)"
exit 1
fi

# When found parse its content

PARSED_PATH=`tr -d "\r\n\t" < $FILE2PARSE | awk 'ps=index($0,"<localRepository>"),pe=index($0,"</localRepository>") {print substr($0,ps+17,pe-ps-17)}'`

# Error messages if parsed content isn't as expected

if [ "$PARSED_PATH" = "/path/to/local/repo" ]; then
echo $(tput setaf 2)"INFO: Copying libraries to a default Maven repository '$(tput setaf 7)$HOME/.m2/repository'"
sleep 3
if [ ! -d "$HOME/.m2/repository" ]; then
mkdir $HOME/.m2/repository
fi
fileRepo="repo"
mvnLocalRepo=$HOME/.m2/repository
cp -Rv $fileRepo/* $mvnLocalRepo
echo $(tput setaf 2)"INFO: Libraries successfully copied"$(tput setaf 7)
exit
fi

if [ "$PARSED_PATH" = "" ]; then
echo "$(tput setaf 1)ERROR: Path to a local Maven repo either isn't specified in settings.xml or there're errors in syntax and/or tags. Copying libs to a default repository $(tput setaf 2)$HOME/.m2/repository"$(tput setaf 7)
sleep 3
if [ ! -d "$HOME/.m2/repository" ]; then
mkdir $HOME/.m2/repository
fi
echo "$(tput setaf 2)INFO: Copying libs..."$(tput setaf 7)
fileRepo="repo"
mvnLocalRepo=$HOME/.m2/repository
cp -Rv $fileRepo/* $mvnLocalRepo
echo $(tput setaf 2)"INFO: Libraries successfully copied"$(tput setaf 7)
exit 1
fi

# Copy libraries to a local Maven repository after all checks are passed

fileRepo="repo"
mvnLocalRepo=$PARSED_PATH

# Check is path exists

if [ ! -d "$PARSED_PATH" ]; then

read -p "$(tput setaf 1)WARNING: Path $(tput setaf 6)$PARSED_PATH$(tput setaf 1) specified in your settings.xml does not exist. Create this directory and copy libs? (y/n)? $(tput setaf 7)" choice
case "$choice" in 
  y|Y ) 

mkdir $PARSED_PATH
echo "$(tput setaf 2)INFO: Creating directory and copying libs...$(tput setaf 7)"
sleep 2
cp -Rv $fileRepo/* $mvnLocalRepo
echo "$(tput setaf 2)INFO: Libs successfully copied$(tput setaf 7)"
exit 1;;

  n|N ) 
echo "$(tput setaf 2)INFO: Script execution cancelled. Please, check a path to your local Maven repository in settings.xml$(tput setaf 7)"
exit;;
  * ) echo "Invalid choice";;
esac

exit 1
fi
echo "$(tput setaf 2)INFO: Local Maven repository found at $(tput setaf 7)$PARSED_PATH.$(tput setaf 2) Copying libs..."$(tput setaf 7)
sleep 3
cp -Rv $fileRepo/* $mvnLocalRepo
echo $(tput setaf 2)"INFO: Libraries successfully copied"$(tput setaf 7)
exit 1
fi

PARSED_PATH=`tr -d "\r\n\t" < $FILE2PARSE_DEFAULT | awk 'ps=index($0,"<localRepository>"),pe=index($0,"</localRepository>") {print substr($0,ps+17,pe-ps-17)}'`

# Chech if a path to a local Maven repository isn't specified or does not exist


if [ "$PARSED_PATH" = "" ]; then

echo "$(tput setaf 1)ERROR: Path to a local Maven repo isn't specified in settings.xml. Copying libs to a default repository $(tput setaf 2)$HOME/.m2/repository"$(tput setaf 7)
sleep 2
if [ ! -d "$HOME/.m2/repository" ]; then
mkdir $HOME/.m2/repository
fi
echo "$(tput setaf 2)INFO: Copying libs..."$(tput setaf 7)
fileRepo="repo"
mvnLocalRepo=$HOME/.m2/repository
cp -Rv $fileRepo/* $mvnLocalRepo
echo $(tput setaf 2)"INFO: Libraries successfully copied"$(tput setaf 7)
exit 1
fi

# Check if a default path in a default Maven's settings.xml exist (commented), copy libs to a default local Maven repo

if [ "$PARSED_PATH" = "/path/to/local/repo" ]; then
echo $(tput setaf 2)"INFO: Copying libraries to a default Maven repository $HOME/.m2/repository"$(tput setaf 7)
sleep 2
if [ ! -d "$HOME/.m2/repository" ]; then
mkdir $HOME/.m2/repository
fi
fileRepo="repo"
mvnLocalRepo=$HOME/.m2/repository
cp -Rv $fileRepo/* $mvnLocalRepo
echo $(tput setaf 2)"INFO: Libraries successfully copied"$(tput setaf 7)
exit
fi

# If a path does not exist, ask a user to make a choice

if [ ! -d "$PARSED_PATH" ]; then

read -p "$(tput setaf 1)WARNING: Path $(tput setaf 6)$PARSED_PATH$(tput setaf 1) specified in your settings.xml does not exist. Create this directory and copy libs? (y/n)? $(tput setaf 7)" choice
case "$choice" in 
  y|Y ) 

echo "$(tput setaf 2)INFO: Creating directory...$(tput setaf 7)"
sleep 1
mkdir $PARSED_PATH
echo "$(tput setaf 2)INFO: Copying libs..."$(tput setaf 7)
sleep 1
cp -Rv $fileRepo/* $mvnLocalRepo
echo $(tput setaf 2)"INFO: Libraries successfully copied"$(tput setaf 7)
exit 1;;
n|N ) 
echo "$(tput setaf 2)INFO: Script execution cancelled. Please, check a path to your local Maven repository in settings.xml$(tput setaf 7)"
exit;;
  * ) echo "Invalid choice";;
esac
fi

echo "$(tput setaf 2)INFO: Local Maven repository found at $(tput setaf 7)$HOME/.m2/repository. $(tput setaf 2)Copying libs..."$(tput setaf 7)
sleep 2
fileRepo="repo"
mvnLocalRepo=$PARSED_PATH
cp -Rv $fileRepo/* $mvnLocalRepo
echo $(tput setaf 2)"INFO: Libraries successfully copied"$(tput setaf 7)
