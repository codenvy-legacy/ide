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
package com.codenvy.ide.ext.extruntime.server.runner;

import com.codenvy.api.core.util.ProcessUtil;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import static com.codenvy.ide.commons.FileUtils.downloadFile;
import static com.codenvy.ide.commons.ZipUtils.unzip;
import static com.codenvy.ide.ext.extruntime.server.Utils.getTomcatBinaryDistribution;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Class represents a Tomcat server.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TomcatServer.java Jul 26, 2013 3:15:52 PM azatsarynnyy $
 */
public class TomcatServer {
    private static final Log LOG = ExoLogger.getLogger(TomcatServer.class);
    /** Process that represents a launched Tomcat server. */
    private Process                   process;
    /** Configuration for launching Tomcat server. */
    private TomcatServerConfiguration configuration;

    /**
     * Starts Tomcat server.
     *
     * @param configuration
     *         Tomcat server configuration
     * @throws IOException
     *         if any error occurred while launching Tomcat server
     */
    public void start(TomcatServerConfiguration configuration) throws IOException {
        this.configuration = configuration;

        InputStream tomcatDistribution = getTomcatBinaryDistribution().openStream();
        unzip(tomcatDistribution, configuration.getWorkDir().toFile());
        generateServerXml(configuration.getWorkDir(), configuration.getPort());

        final File ideWar = downloadFile(new File(configuration.getWorkDir() + "/webapps"), "app-", ".war",
                                         configuration.getIdeWarUrl());
        ideWar.renameTo(configuration.getWorkDir().resolve("webapps/ide.war").toFile());

        final Path catalinaShPath = configuration.getWorkDir().resolve("bin/catalina.sh");
        Files.setPosixFilePermissions(catalinaShPath, PosixFilePermissions.fromString("rwxr--r--"));
        process = new ProcessBuilder(catalinaShPath.toString(), "run").start();
    }

    /**
     * Get Tomcat server's logs.
     *
     * @return Tomcat server's logs
     * @throws IOException
     *         if any error occurred while retrieving logs
     */
    public String getLogs() throws IOException {
        // read all catalina*.log files
        File logsDir = configuration.getWorkDir().resolve("logs").toFile();
        File[] catalinaLogFiles = logsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("catalina") && name.endsWith(".log");
            }
        });

        StringBuilder logs = new StringBuilder();
        for (File catalinaLogFile : catalinaLogFiles) {
            final String catalinaLogs = new String(Files.readAllBytes(catalinaLogFile.toPath()));
            if (!catalinaLogs.isEmpty()) {
                logs.append("========> ");
                logs.append(catalinaLogFile.getName());
                logs.append(" <========");
                logs.append("\n\n");
                logs.append(catalinaLogs);
                logs.append("\n\n");
            }
        }

        return logs.toString();
    }

    /** Stop Tomcat server. */
    public void stop() {
        LOG.debug("Killing process tree");
        // Use ProcessUtil because java.lang.Process.destroy() method doesn't
        // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
        ProcessUtil.kill(process);
    }

    /** Returns Tomcat server configuration. */
    public TomcatServerConfiguration getConfiguration() {
        return configuration;
    }

    private void generateServerXml(Path tomcatDir, int httpPort) throws IOException {
        final Path serverXmlPath = tomcatDir.resolve("conf/server.xml");
        Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("tomcat/conf/server.xml"),
                   serverXmlPath, REPLACE_EXISTING);
        final String serverXmlContent = new String(Files.readAllBytes(serverXmlPath));
        Files.write(serverXmlPath, serverXmlContent.replace("${PORT}", Integer.toString(httpPort)).getBytes());
    }
}
