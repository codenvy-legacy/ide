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
 * Update remote refs with associated objects
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class PushCommand extends GitCommand<Void> {

    private String[] refSpec;
    private String   remote;
    private boolean  force;

    public PushCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
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
    public PushCommand setRefSpec(String[] refSpecs) {
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
