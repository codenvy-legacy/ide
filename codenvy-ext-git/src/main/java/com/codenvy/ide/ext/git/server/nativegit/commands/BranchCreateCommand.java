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
package com.codenvy.ide.ext.git.server.nativegit.commands;

import com.codenvy.ide.ext.git.server.GitException;

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

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
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
