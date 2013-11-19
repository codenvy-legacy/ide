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
import com.codenvy.ide.ext.git.server.nativegit.Config;
import com.codenvy.ide.ext.git.shared.GitUser;

import java.io.File;

/**
 * Commit changes
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class CommitCommand extends GitCommand<Void> {

    private String  message;
    private GitUser author;
    private GitUser committer;
    private boolean amend;
    private boolean all;

    public CommitCommand(File place) {
        super(place);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (message == null) {
            throw new GitException("Message wasn't set.");
        }
        clear();
        commandLine.add("commit");
        if (amend) {
            commandLine.add("--amend");
        }
        if (all) {
            commandLine.add("-a");
        }
        commandLine.add("-m", message);
        if (author != null) {
            commandLine.add(String.format("--author=%s \\<%s>", author.getName(), author.getEmail()));
        }
        if (committer != null) {
            //save config
            Config config = new Config(getRepository());
            GitUser defaultCommitter = config.loadUser().getUser();
            //set new committer
            config.setUser(committer).saveUser();
            try {
                start();
            } finally {
                //set default user back
                config.setUser(defaultCommitter).saveUser();
            }
        } else {
            start();
        }
        return null;
    }

    /**
     * @param message
     *         commit message
     * @return CommitCommand object
     */
    public CommitCommand setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * @param all
     *         if <code>true</code> all files will be added to index
     * @return CommitCommand with established all parameter
     */
    public CommitCommand setAll(boolean all) {
        this.all = all;
        return this;
    }

    /**
     * @param amend
     *         change previous commit
     * @return CommitCommand established amend parameter
     */
    public CommitCommand setAmend(boolean amend) {
        this.amend = amend;
        return this;
    }

    /**
     * @param author
     *         author of commit
     * @return CommitCommand with established author
     */
    public CommitCommand setAuthor(GitUser author) {
        this.author = author;
        return this;
    }

    /**
     * If committer is <code>null</code> then default configuration person
     * will be used.
     *
     * @param committer
     *         who makes commit
     * @return CommitCommand with established committer
     */
    public CommitCommand setCommitter(GitUser committer) {
        this.committer = committer;
        return this;
    }
}
