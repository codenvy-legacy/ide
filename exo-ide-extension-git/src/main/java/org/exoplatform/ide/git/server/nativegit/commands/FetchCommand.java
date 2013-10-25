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
 * Download objects and refs from other repository
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class FetchCommand extends GitCommand<Void> {

    private String[] refSpec;
    private String   remote;
    private boolean  prune;

    public FetchCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
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
    public FetchCommand setRefSpec(String[] refSpec) {
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
