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
import org.exoplatform.ide.git.shared.Tag;

import java.io.File;

/**
 * Create tag
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class TagCreateCommand extends GitCommand<Tag> {

    private String  name;
    private String  commit;
    private String  message;
    private boolean force;

    public TagCreateCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Tag execute() throws GitException {
        if (name == null) {
            throw new GitException("Name wasn't set.");
        }
        clear();
        commandLine.add("tag", name);
        if (commit != null) {
            commandLine.add(commit);
        }
        if (message != null) {
            commandLine.add("-m", message);
        }
        if (force) {
            commandLine.add("--force");
        }
        start();
        return new Tag(name);
    }

    /**
     * @param name
     *         tag name
     * @return TagCreateCommand with established name
     */
    public TagCreateCommand setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param commit
     *         commit start point for tag creating
     * @return TagCreateCommand with established commit
     */
    public TagCreateCommand setCommit(String commit) {
        this.commit = commit;
        return this;
    }

    /**
     * @param message
     *         tag message
     * @return TagCreateCommand with established message
     */
    public TagCreateCommand setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * @param force
     *         force tag creating
     * @return TagCreateCommand with established force parameter
     */
    public TagCreateCommand setForce(boolean force) {
        this.force = force;
        return this;
    }
}
