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
 * Create branch
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class BranchCreateCommand extends GitCommand<Void> {

    private String branchName;
    private String startPoint;

    public BranchCreateCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (branchName == null) {
            throw new GitException("Branch name was not set.");
        }
        clear();
        commandLine.add("branch").add(branchName);
        if (startPoint != null) {
            commandLine.add(startPoint);
        }
        start();
        return null;
    }

    /**
     * @param branchName
     *         branch to create
     * @return BranchCreateCommand with established branch name
     */
    public BranchCreateCommand setBranchName(String branchName) {
        this.branchName = branchName;
        return this;
    }

    /**
     * @param commitId
     *         branch creating start point
     * @return BranchCreateCommand with established commit id
     */
    public BranchCreateCommand setStartPoint(String commitId) {
        this.startPoint = commitId;
        return this;
    }
}
