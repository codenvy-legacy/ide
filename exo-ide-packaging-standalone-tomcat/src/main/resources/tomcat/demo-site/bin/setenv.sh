
IDE_OPTS="-Dorg.exoplatform.logreader.logpath=${CATALINA_HOME}/logs"
LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
SECURITY_OPTS="-Djava.security.auth.login.config=../conf/jaas.conf"
EXO_CONFIG_OPTS="-Xshare:auto -Xms512m -Xmx1024m"
JAVA_SRC="$JAVA_HOME/src.zip"
EXO_OPTS="-Djavasrc=$JAVA_HOME/src.zip -Djre.lib=$JAVA_HOME/jre/lib -Dorg.exoplatform.mimetypes=conf/mimetypes.properties -Dorg.exoplatform.ide.server.fs-root-path=git -Dorg.exoplatform.ide.server.user-config-path=/ide-home/users/"
#REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

JAVA_OPTS="$JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_CONFIG_OPTS $EXO_OPTS $REMOTE_DEBUG $IDE_OPTS"
export JAVA_OPTS

export CLASSPATH="${CATALINA_HOME}/conf/:${CATALINA_HOME}/lib/jul-to-slf4j.jar:${CATALINA_HOME}/lib/slf4j-api.jar:${CATALINA_HOME}/lib/logback-classic.jar:${CATALINA_HOME}/lib/logback-core.jar"
