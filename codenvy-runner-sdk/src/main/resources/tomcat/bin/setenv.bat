@REM
@REM Copyright (c) 2012-2014 Codenvy, S.A.
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM http://www.eclipse.org/legal/epl-v10.html
@REM
@REM Contributors:
@REM   Codenvy, S.A. - initial API and implementation
@REM

@echo off

set IDE_OPTS="-Dorg.exoplatform.logreader.logpath=%CATALINA_HOME%/logs/logreader"
set LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
set CODENVY_CONFIG_OPTS="-Xshare:auto -Xms512m -Xmx1024m -XX:MaxPermSize=256m"
set CODENVY_OPTS="-Djavasrc=%JAVA_HOME%\src.zip" "-Djre.lib=%JAVA_HOME%\jre\lib" "-Dorg.exoplatform.mimetypes=conf\mimetypes.properties" "-Dorg.exoplatform.ide.git.server=git" "-Dtenant.masterhost=localhost" "-Dorganization.application.server.url=http://localhost:${PORT}/userdb" "-Dmailsender.application.server.url=http://localhost:${PORT}/mail" "-Dbuilder.timeout=600" "-Dcodenvy.local.conf.dir=${CATALINA_HOME}/conf"
set JAVA_OPTS=%LOG_OPTS% %CODENVY_CONFIG_OPTS% %CODENVY_OPTS% %IDE_OPTS%
set CLASSPATH=%CATALINA_HOME%/conf/;%CATALINA_HOME%/lib/jul-to-slf4j.jar;%CATALINA_HOME%/lib/slf4j-api.jar;%CATALINA_HOME%/lib/logback-classic.jar;%CATALINA_HOME%/lib/logback-core.jar
echo "======="
echo %JAVA_OPTS%
echo "======="
