/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.extruntime.server.codeserver;

import com.codenvy.ide.ext.extruntime.server.ExtensionLauncherException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Implementation of {@link CodeServerStarter} that uses GWT Maven plug-in.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GwtMvnCodeServerStarter.java Jul 25, 2013 6:09:27 PM azatsarynnyy $
 */
public class GwtMvnCodeServerStarter implements CodeServerStarter {

    /** {@inheritDoc} */
    @Override
    public CodeServer start(Path workDir) throws ExtensionLauncherException {
        // TODO 'clean compile' it's a temporary workaround to get IDEInjector.java and ExtensionManager.java in a target folder
        final String[] command = new String[]{
                getMavenExecCommand(),
                "clean",
                "compile",
                "gwt:run-codeserver", // org.codehaus.mojo:gwt-maven-plugin should be described in pom.xml
                "-PdevMode"};

        Path logFilePath = workDir.resolve("CodeServer.log");
        ProcessBuilder processBuilder = new ProcessBuilder(command).directory(workDir.toFile());
        processBuilder.redirectOutput(logFilePath.toFile());

        try {
            return new DefaultCodeServer(processBuilder.start(), logFilePath);
        } catch (IOException e) {
            throw new ExtensionLauncherException("Unable to launch application.");
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
