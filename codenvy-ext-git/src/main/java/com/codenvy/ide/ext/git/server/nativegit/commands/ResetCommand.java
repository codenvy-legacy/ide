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
