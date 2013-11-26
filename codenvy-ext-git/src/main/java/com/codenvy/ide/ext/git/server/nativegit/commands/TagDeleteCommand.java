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
