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
 * Add remote to repository
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteAddCommand extends GitCommand<Void> {
    private String   name;
    private String   url;
    private String[] branches;

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

    public RemoteAddCommand setBranches(String[] branches) {
        this.branches = branches;
        return this;
    }
}
