/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ant.tools;

import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.ProcessUtil;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** @author andrew00x */
public class AntUtils {
    /** Not instantiable. */
    private AntUtils() {
    }

    public static String getAntExecCommand() {
        final java.io.File antHome = getAntHome();
        if (antHome != null) {
            final String ant = "bin" + java.io.File.separatorChar + "ant";
            return new java.io.File(antHome, ant).getAbsolutePath(); // If ant home directory set use it
        } else {
            return "ant"; // otherwise 'ant' should be in PATH variable
        }
    }

    public static java.io.File getAntHome() {
        final String antHomeEnv = System.getenv("ANT_HOME");
        if (antHomeEnv == null) {
            return null;
        }
        java.io.File antHome = new java.io.File(antHomeEnv);
        return antHome.exists() ? antHome : null;
    }

    private static final Path   BUILD_FILE_PATH    = Paths.get(System.getProperty("java.io.tmpdir"), "codenvy_ant_properties.xml");
    private static final String BUILD_FILE_CONTENT = "<project name=\"ant_properties\" default=\"get_properties\">\n" +
                                                     "    <target name=\"get_properties\">\n" +
                                                     "        <echo>Ant version: ${ant.version}</echo>\n" +
                                                     "        <echo>Ant home: ${ant.home}</echo>\n" +
                                                     "        <echo>Java version: ${java.version}, vendor: ${java.vendor}</echo>\n" +
                                                     "        <echo>Java home: ${java.home}</echo>\n" +
                                                     "        <echo>OS name: \"${os.name}\", version: \"${os.version}\", arch: \"${os.arch}\"</echo>\n" +
                                                     "    </target>\n" +
                                                     "</project>\n";

    public static Map<String, String> getAntEnvironmentInformation() throws IOException {
        final Map<String, String> versionInfo = new HashMap<>();
        final LineConsumer cmdOutput = new LineConsumer() {
            boolean end = false;

            @Override
            public void writeLine(String line) throws IOException {
                if (line.isEmpty()) {
                    end = true;
                }
                if (end) {
                    return;
                }
                String key = null;
                int keyEnd = 0;
                int valueStart = 0;
                final int l = line.length();
                while (keyEnd < l) {
                    if (line.charAt(keyEnd) == ':') {
                        valueStart = keyEnd + 1;
                        break;
                    }
                    keyEnd++;
                }
                if (keyEnd > 0) {
                    key = line.substring(0, keyEnd);
                }
                if (key != null) {
                    while (valueStart < l && Character.isWhitespace(line.charAt(valueStart))) {
                        valueStart++;
                    }
                    if ("Ant version".equals(key)) {
                        int valueEnd = line.indexOf("compiled on", valueStart);
                        final String value = line.substring(valueStart, valueEnd).trim();
                        versionInfo.put(key, value);
                    } else {
                        final String value = line.substring(valueStart);
                        versionInfo.put(key, value);
                    }
                }
            }

            @Override
            public void close() throws IOException {
            }
        };
        readAntEnvironmentInformation(cmdOutput);
        return versionInfo;
    }

    private static void readAntEnvironmentInformation(LineConsumer cmdOutput) throws IOException {
        if (!Files.isReadable(BUILD_FILE_PATH)) {
            try (Writer writer = Files.newBufferedWriter(BUILD_FILE_PATH, Charset.forName("UTF-8"))) {
                writer.write(BUILD_FILE_CONTENT);
            }
        }
        final CommandLine commandLine = new CommandLine(getAntExecCommand()).add("-f", BUILD_FILE_PATH.toString(), "-quiet", "-emacs");
        final ProcessBuilder processBuilder = new ProcessBuilder().command(commandLine.toShellCommand()).redirectErrorStream(true);
        final Process process = processBuilder.start();
        ProcessUtil.process(process, cmdOutput, LineConsumer.DEV_NULL);
    }
}
