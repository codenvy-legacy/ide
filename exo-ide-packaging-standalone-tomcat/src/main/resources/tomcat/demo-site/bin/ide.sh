#!/bin/sh

# Computes the absolute path of eXo
cd `dirname "$0"`

# Sets some variables
LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger"
SECURITY_OPTS="-Djava.security.auth.login.config=../conf/jaas.conf"
EXO_CONFIG_OPTS="-Xshare:auto -Xms512m -Xmx1024m"
JAVA_SRC="$JAVA_HOME/src.zip"
EXO_OPTS="-Djavasrc=$JAVA_HOME/src.zip -Djre.lib=$JAVA_HOME/jre/lib -Dorg.exoplatform.mimetypes=conf/mimetypes.properties"
#REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

JAVA_OPTS="$JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_CONFIG_OPTS $EXO_OPTS $REMOTE_DEBUG"
export JAVA_OPTS

# Launches the server
exec "$PRGDIR"./catalina.sh "$@"
