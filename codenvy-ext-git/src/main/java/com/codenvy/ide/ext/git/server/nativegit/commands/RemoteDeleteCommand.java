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
 * Delete remote
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteDeleteCommand extends GitCommand<Void> {

    private String name;

    public RemoteDeleteCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (name == null) {
            throw new GitException("Name was not set.");
        }
        clear();
        commandLine.add("remote", "rm", name);
        start();
        return null;
    }

    /**
     * @param name
     *         remote name that will be removed
     * @return RemoteDelete with established remote name
     */
    public RemoteDeleteCommand setName(String name) {
        this.name = name;
        return this;
    }
}
