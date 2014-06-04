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
import java.util.List;

/**
 * Add remote to repository
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteAddCommand extends GitCommand<Void> {
    private String   name;
    private String   url;
    private List<String> branches;

    public RemoteAddCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (name == null || url == null) {
            throw new GitException("Name or url is not set.");
        }
        clear();
        commandLine.add("remote", "add", name, url);
        if (branches != null) {
            for (String branch : branches) {
                commandLine.add("--track", branch);
            }
        }
        start();
        return null;
    }

    /**
     * @param name
     *         remote name that will be added
     * @return RemoteAddCommand with established remote name
     */
    public RemoteAddCommand setName(String name) {
        this.name = name;
        return this;
    }

    public RemoteAddCommand setUrl(String url) {
        this.url = url;
        return this;
    }

    public RemoteAddCommand setBranches(List<String> branches) {
        this.branches = branches;
        return this;
    }
}
