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
 * Reset repository to specifically state
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class ResetCommand extends GitCommand<Void> {

    private String commit;
    private String mode;

    public ResetCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (mode == null) {
            throw new GitException("Reset mode wasn't set.");
        }
        clear();
        commandLine.add("reset", mode);
        if (commit != null) {
            commandLine.add(commit);
        }
        start();
        return null;
    }

    /**
     * @param commit
     *         reset point
     * @return ResetCommand with established commit
     */
    public ResetCommand setCommit(String commit) {
        this.commit = commit;
        return this;
    }

    /**
     * @param mode
     *         reset mode
     * @return ResetCommand with established mode
     */
    public ResetCommand setMode(String mode) {
        this.mode = mode;
        return this;
    }
}
