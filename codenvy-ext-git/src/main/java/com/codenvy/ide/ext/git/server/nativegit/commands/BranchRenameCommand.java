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

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
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
