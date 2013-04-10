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
package org.exoplatform.ide.git.server;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Checks for the presence of a global .gitignore file and adds some rules to it.
 * Adds this file to Git cross-repository configuration.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GitIgnoreConfigChecker.java Apr 10, 2013 12:03:18 PM azatsarynnyy $
 */
public class GitIgnoreConfigChecker implements Startable {

    private static final Log LOG = ExoLogger.getExoLogger(GitIgnoreConfigChecker.class);

    private static final String GIT_GLOBAL_CONFIG_FILE_NAME = "/.gitconfig";

    private static final String GIT_IGNORE_GLOBAL_FILE_NAME = "/.gitignore_global";

    /** @see org.picocontainer.Startable#start() */
    @Override
    public void start() {
        checkGitIgnoreConfiguration();
        checkGitGlobalConfiguration();
    }

    /** @see org.picocontainer.Startable#stop() */
    @Override
    public void stop() {
    }

    /** Checks for the presence of a global .gitignore file and adds rules to it. */
    private void checkGitIgnoreConfiguration() {
        try {
            File file = new File(System.getProperty("user.home") + GIT_IGNORE_GLOBAL_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }

            final String fileContent = readFileContent(file.getAbsolutePath());
            final String data = getCodenvyIgnoreSectionContent();
            if (!fileContent.contains(".vfs/")) {
                writeFileContent(file.getAbsolutePath(), data, true);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /** Change Git cross-repository configuration. */
    private void checkGitGlobalConfiguration() {
        try {
            // Do not run git command as a process because git is not installed on the server.
            // Runtime.getRuntime().exec("git config --global core.excludesfile " + GIT_IGNORE_GLOBAL_FILE_NAME);
            // Update .gitconfig file manually.
            final String gitGlobalConfigFilePath = System.getProperty("user.home") + GIT_GLOBAL_CONFIG_FILE_NAME;

            final String fileContent = readFileContent(gitGlobalConfigFilePath);
            if (!fileContent.contains("excludesfile")) {
                final String data = getCoreExcludesFileSectionContent();
                writeFileContent(gitGlobalConfigFilePath, data, true);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static String getCoreExcludesFileSectionContent() {
        return "\n"
               + "[core]\n"
               + "        excludesfile = " + System.getProperty("user.home") + GIT_IGNORE_GLOBAL_FILE_NAME + "\n";
    }

    private static String getCodenvyIgnoreSectionContent() {
        return "\n"
               + "# Codenvy VFS files #\n"
               + "#####################\n"
               + ".vfs/\n";
    }

    private static String readFileContent(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    private void writeFileContent(String filePath, String data, boolean append) throws IOException {
        FileWriter fileWritter = new FileWriter(filePath, append);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(data);
        bufferWritter.close();
    }
}
