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
 * Download objects and refs from other repository
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class FetchCommand extends GitCommand<Void> {

    private List<String> refSpec;
    private String   remote;
    private boolean  prune;

    public FetchCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        remote = remote == null ? "origin" : remote;
        clear();
        commandLine.add("fetch", remote);
        commandLine.add(refSpec);
        if (prune) {
            commandLine.add("--prune");
        }
        start();
        return null;
    }

    /**
     * @param refSpec
     *         ref spec to fetch
     * @return FetchCommand with established ref spec
     */
    public FetchCommand setRefSpec(List<String> refSpec) {
        this.refSpec = refSpec;
        return this;
    }

    /**
     * @param remote
     *         remote name
     * @return FetchCommand with established remote fetch
     */
    public FetchCommand setRemote(String remote) {
        this.remote = remote;
        return this;
    }

    /**
     * @param prune
     *         if <code>true</code> not existing remote branches will be removed
     * @return FetchCommand with established prune parameter
     */
    public FetchCommand setPrune(boolean prune) {
        this.prune = prune;
        return this;
    }
}
