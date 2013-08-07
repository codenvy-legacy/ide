/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.extruntime.server.codeserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.codenvy.ide.ext.extruntime.server.ExtensionLauncher.ADD_SOURCES_PROFILE;

/**
 * Implementation of {@link CodeServerStarter} that uses GWT Maven plug-in to start new code servers.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GwtMvnCodeServerStarter.java Jul 25, 2013 6:09:27 PM azatsarynnyy $
 */
public class GwtMvnCodeServerStarter implements CodeServerStarter {

    /** {@inheritDoc} */
    @Override
    public CodeServer start(Path workDir) throws CodeServerException {
        // need 'clean compile' to get 'IDEInjector.java' and 'ExtensionManager.java' in a target folder
        final String[] command = new String[]{
                getMavenExecCommand(),
                "clean",
                "compile",
                "gwt:run-codeserver", // org.codehaus.mojo:gwt-maven-plugin should be described in a pom.xml
                "-P" + ADD_SOURCES_PROFILE};

        Path logFilePath = workDir.resolve("code-server.log");
        ProcessBuilder processBuilder = new ProcessBuilder(command).directory(workDir.toFile());
        processBuilder.redirectOutput(logFilePath.toFile());

        try {
            return new DefaultCodeServer(processBuilder.start(), logFilePath);
        } catch (IOException e) {
            throw new CodeServerException("Unable to start code server.");
        }
    }

    private String getMavenExecCommand() {
        final File mvnHome = getMavenHome();
        if (mvnHome != null) {
            final String mvn = "bin" + File.separatorChar + "mvn";
            return new File(mvnHome, mvn).getAbsolutePath(); // use Maven home directory if it's set
        } else {
            return "mvn"; // otherwise 'mvn' should be in PATH variable
        }
    }

    private File getMavenHome() {
        final String m2HomeEnv = System.getenv("M2_HOME");
        if (m2HomeEnv == null) {
            return null;
        }
        final File m2Home = new File(m2HomeEnv);
        return m2Home.exists() ? m2Home : null;
    }
}
