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
package com.codenvy.ide.ext.git.server.nativegit.commands;

import com.codenvy.ide.ext.git.server.GitException;

import java.io.File;

/**
 * Delete branch
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class BranchDeleteCommand extends GitCommand<Void> {

    private boolean deleteFullyMerged;
    private String branchName;

    public BranchDeleteCommand(File repository) {
        super(repository);
    }

    /**
     * @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute()
     */
    @Override
    public Void execute() throws GitException {
        if (branchName == null) {
            throw new GitException("Branch name was not set.");
        }
        clear();
        commandLine.add("branch");
        commandLine.add(deleteFullyMerged ? "-D" : "-d");
        commandLine.add(branchName);
        start();
        return null;
    }

    /**
     * @param deleteFullyMerged if <code>true</code> fully merged branch will be deleted
     * @return BranchDeleteCommand with established delete fully merged branch parameter
     */
    public BranchDeleteCommand setDeleteFullyMerged(boolean deleteFullyMerged) {
        this.deleteFullyMerged = deleteFullyMerged;
        return this;
    }

    /**
     * @param branchName branch to delete
     * @return BranchDeleteCommand with established branch to delete parameter
     */
    public BranchDeleteCommand setBranchName(String branchName) {
        this.branchName = branchName;
        return this;
    }
}
