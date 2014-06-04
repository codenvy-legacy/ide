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
package com.codenvy.ide.ext.git.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks Git configuration to ensure properly work Git-extension in Codenvy.
 *
 * @author Artem Zatsarynnyy
 */
public class GitConfigurationChecker {
    private static final Logger      LOG                = LoggerFactory.getLogger(GitConfigurationChecker.class);
    /** Special comment for global .gitignore file. Define begin of Codenvy-specific patterns. */
    private static final String      CODENVY_COMMENT    = "# Codenvy files";
    /** Codenvy-specific file patterns to ignore by Git. */
    private static final Set<String> GITIGNORE_PATTERNS = new LinkedHashSet<>();
    /** Path to the global gitconfig file. */
    private final Path GLOBAL_GITCONFIG_FILE_PATH;
    /** Path to the file that contains Codenvy-specific file patterns to ignore its by Git. */
    private final Path DEFAULT_GITIGNORE_FILE_PATH;

    public GitConfigurationChecker() {
        GLOBAL_GITCONFIG_FILE_PATH = Paths.get(System.getProperty("user.home") + "/.gitconfig");
        DEFAULT_GITIGNORE_FILE_PATH = Paths.get(System.getProperty("user.home") + "/.gitignore_codenvy");

        GITIGNORE_PATTERNS.add(".codenvy/");
        GITIGNORE_PATTERNS.add(".vfs/");
    }

    // Constructor for unit-tests.
    GitConfigurationChecker(Path globalGitconfigFilePath, Path gitIgnoreFilePath) {
        GLOBAL_GITCONFIG_FILE_PATH = globalGitconfigFilePath;
        DEFAULT_GITIGNORE_FILE_PATH = gitIgnoreFilePath;

        GITIGNORE_PATTERNS.add(".codenvy/");
        GITIGNORE_PATTERNS.add(".vfs/");
    }

    @PostConstruct
    public void start() {
        try {
            ensureExistingGlobalGitconfigFile();
            checkExcludesfileProperty();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void ensureExistingGlobalGitconfigFile() throws IOException {
        if (Files.notExists(GLOBAL_GITCONFIG_FILE_PATH)) {
            Files.createFile(GLOBAL_GITCONFIG_FILE_PATH);
        }
    }

    private void checkExcludesfileProperty() throws IOException {
        List<String> gitconfigContent = Files.readAllLines(GLOBAL_GITCONFIG_FILE_PATH, Charset.forName("UTF-8"));
        for (String s : gitconfigContent) {
            if (s.trim().startsWith("excludesfile")) {
                final String line = s.trim();
                Path existingGitignoreFilePath = Paths.get(line.substring(line.indexOf("=") + 2));
                writeCodenvyExcludesToFile(existingGitignoreFilePath);
                return;
            }
        }
        writeCodenvyExcludesToFile(DEFAULT_GITIGNORE_FILE_PATH);
        Files.write(GLOBAL_GITCONFIG_FILE_PATH, getExcludesfilePropertyDefaultContent().getBytes(), StandardOpenOption.APPEND);
    }

    private void writeCodenvyExcludesToFile(Path path) throws IOException {
        List<String> toAdd = new ArrayList<>(GITIGNORE_PATTERNS);

        // add only new rules to the existing .gitignore file
        if (Files.exists(path)) {
            List<String> existingRules = Files.readAllLines(path, Charset.forName("UTF-8"));
            for (String rule : existingRules) {
                toAdd.remove(rule.trim());
            }
        }

        if (!toAdd.isEmpty()) {
            toAdd.add(0, '\n' + CODENVY_COMMENT);
            Files.write(path, toAdd, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    private String getExcludesfilePropertyDefaultContent() {
        return "\n"
               + "[core]\n"
               + "\texcludesfile = " + DEFAULT_GITIGNORE_FILE_PATH
               + "\n";
    }
}
