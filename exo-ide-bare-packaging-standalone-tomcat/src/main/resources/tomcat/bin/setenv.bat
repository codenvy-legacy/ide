@echo off

set IDE_OPTS="-Dorg.exoplatform.logreader.logpath=%CATALINA_HOME%/logs"
set LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
set LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger"
set SECURITY_OPTS="-Djava.security.auth.login.config=%CATALINA_HOME%\conf\jaas.conf"
set EXO_OPTS="-Djavasrc=%JAVA_HOME%\src.zip" "-Djre.lib=%JAVA_HOME%\jre\lib" "-Dorg.exoplatform.mimetypes=conf\mimetypes.properties" "-Dorg.exoplatform.ide.server.fs-root-path=${CATALINA_HOME}/bin/git -Dorg.exoplatform.ide.git.server=git"
set JAVA_OPTS=-Xshare:auto -Xms128m -Xmx512m %LOG_OPTS% %SECURITY_OPTS% %EXO_OPTS% %IDE_OPTS%
set CLASSPATH=%CATALINA_HOME%/conf/;%CATALINA_HOME%/lib/jul-to-slf4j.jar;%CATALINA_HOME%/lib/slf4j-api.jar;%CATALINA_HOME%/lib/logback-classic.jar;%CATALINA_HOME%/lib/logback-core.jar
echo "======="
echo %JAVA_OPTS%
echo "======="
