#
# CODENVY CONFIDENTIAL
# __________________
#
# [2012] - [2013] Codenvy, S.A.
# All Rights Reserved.
#
# NOTICE:  All information contained herein is, and remains
# the property of Codenvy S.A. and its suppliers,
# if any.  The intellectual and technical concepts contained
# herein are proprietary to Codenvy S.A.
# and its suppliers and may be covered by U.S. and Foreign Patents,
# patents in process, and are protected by trade secret or copyright law.
# Dissemination of this information or reproduction of this material
# is strictly forbidden unless prior written permission is obtained
# from Codenvy S.A..
#


EXO_CONFIG_OPTS="-Xshare:auto -Xms256m -Xmx1024m"
#REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
JAVA_OPTS="$JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_CONFIG_OPTS $EXO_OPTS $REMOTE_DEBUG $IDE_OPTS"
export JAVA_OPTS

