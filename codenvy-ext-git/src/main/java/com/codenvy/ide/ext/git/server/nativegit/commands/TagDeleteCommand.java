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
 * Delete tag
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class TagDeleteCommand extends GitCommand<Void> {

    private String name;

    public TagDeleteCommand(File repository) {
        super(repository);
    }

    /**
     * @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute()
     */
    @Override
    public Void execute() throws GitException {
        if (name == null) {
            throw new GitException("Tag name wasn't set. Nothing to delete.");
        }
        clear();
        commandLine.add("tag", "--delete", name);
        start();
        return null;
    }

    /**
     * @param name name of tag to delete
     * @return TagDeleteCommand with established name
     */
    public TagDeleteCommand setName(String name) {
        this.name = name;
        return this;
    }
}
