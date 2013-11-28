/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.exoplatform.ide.git.server.nativegit.commands;

import org.exoplatform.ide.git.server.GitException;

import java.io.File;

/**
 * Used for adding new files into index(stage area).
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class AddCommand extends GitCommand<Void> {
    private boolean update;
    private String[] filePattern;

    public AddCommand(File repositoryPlace) {
        super(repositoryPlace);
    }

    /**
     * @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute()
     */
    @Override
    public Void execute() throws GitException {
        if (filePattern == null) {
            throw new GitException("No file pattern was set.");
        }
        clear();
        commandLine.add("add");
        for (String line : filePattern) {
            commandLine.add(line);
        }
        if (update) {
            commandLine.add("--update");
        }
        start();
        return null;
    }

    /**
     * Set up file pattern for add command.
     *
     * @param pattern file pattern for add command.
     * @return AddCommand with established pattern
     */
    public AddCommand setFilePattern(String[] pattern) {
        this.filePattern = pattern;
        return this;
    }

    /**
     * @param update makes add command only for updated files.
     * @return AddCommand with established update parameter
     */
    public AddCommand setUpdate(boolean update) {
        this.update = update;
        return this;
    }
}
