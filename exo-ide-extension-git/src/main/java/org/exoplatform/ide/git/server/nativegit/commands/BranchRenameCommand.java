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
 * Used for branches renaming
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class BranchRenameCommand extends GitCommand<Void> {

    private String oldName;
    private String newName;

    public BranchRenameCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (oldName == null || newName == null) {
            throw new GitException("Old name or new name was not set.");
        }
        clear();
        commandLine.add("branch");
        commandLine.add("-m", oldName, newName);
        start();
        return null;
    }

    /**
     * @param oldName
     *         old branch name
     * @param newName
     *         new branch name
     * @return BranchRenameCommand with established old and new branch names
     */
    public BranchRenameCommand setNames(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
        return this;
    }
}
