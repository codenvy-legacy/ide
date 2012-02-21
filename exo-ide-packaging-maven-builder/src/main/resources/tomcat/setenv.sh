
EXO_CONFIG_OPTS="-Xshare:auto -Xms128m -Xmx256m"
#REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
JAVA_OPTS="$JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_CONFIG_OPTS $EXO_OPTS $REMOTE_DEBUG $IDE_OPTS"
export JAVA_OPTS

