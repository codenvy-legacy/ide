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
 * Update remote refs with associated objects
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class PushCommand extends GitCommand<Void> {

    private List<String> refSpec;
    private String   remote;
    private boolean  force;

    public PushCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        remote = remote == null ? "origin" : remote;
        clear();
        commandLine.add("push");
        commandLine.add(remote);
        if (refSpec != null) {
            commandLine.add(refSpec);
        }
        if (force) {
            commandLine.add("--force");
        }
        start();
        return null;
    }

    /**
     * @param refSpecs
     *         ref specs to push
     * @return PushCommand with established ref specs
     */
    public PushCommand setRefSpec(List<String> refSpecs) {
        this.refSpec = refSpecs;
        return this;
    }

    /**
     * If remote name is null "origin" will be used
     *
     * @param remoteName
     *         remote name
     * @return PushCommand with established remote name
     */
    public PushCommand setRemote(String remoteName) {
        this.remote = remoteName;
        return this;
    }

    /**
     * @param force
     *         if <code>true</code> push will be forced
     * @return PushCommand with established force parmeter
     */
    public PushCommand setForce(boolean force) {
        this.force = force;
        return this;
    }
}
