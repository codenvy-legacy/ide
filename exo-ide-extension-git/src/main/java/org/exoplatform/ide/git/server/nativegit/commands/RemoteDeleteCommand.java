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
 * Delete remote
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteDeleteCommand extends GitCommand<Void> {

    private String name;

    public RemoteDeleteCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
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
