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
 * Update remote
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteUpdateCommand extends GitCommand<Void> {

    private boolean  addBranches;
    private String   remoteName;
    private String   newUrl;
    private String[] branchesToAdd;
    private String[] addUrl;
    private String[] removeUrl;
    private String[] addPushUrl;
    private String[] removePushUrl;

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand */
    public RemoteUpdateCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (remoteName == null) {
            throw new GitException("Remote name wasn't set.");
        }
        clear();
        commandLine.add("remote");
        if (branchesToAdd != null) {
            commandLine.add("set-branches");
            if (addBranches) {
                commandLine.add("--add");
            }
            commandLine.add(remoteName);
            commandLine.add(branchesToAdd);
        } else {
            commandLine.add("set-url");
            if (addUrl != null) {
                commandLine.add("--add", remoteName);
                commandLine.add(addUrl);
            } else if (addPushUrl != null) {
                commandLine.add("--push", remoteName);
                commandLine.add(addPushUrl);
            } else if (removeUrl != null) {
                commandLine.add("--delete", remoteName);
                commandLine.add(removeUrl);
            } else if (removePushUrl != null) {
                commandLine.add("--delete", "--push", remoteName);
                commandLine.add(removePushUrl);
            } else if (newUrl != null) {
                commandLine.add(remoteName, newUrl);
            } else {
                throw new GitException("Url wasn't set.");
            }
        }
        start();
        return null;
    }

    /**
     * @param addBranches
     *         do not replace branches
     * @return RemoteUpdateCommand with established add branches parameter
     */
    public RemoteUpdateCommand setAddBranches(boolean addBranches) {
        this.addBranches = addBranches;
        return this;
    }

    /**
     * @param branchesToAdd
     *         branches to add or replace
     * @return RemoteUpdateCommand with established branches to add
     */
    public RemoteUpdateCommand setBranchesToAdd(String[] branchesToAdd) {
        this.branchesToAdd = branchesToAdd;
        return this;
    }

    /**
     * @param addUrl
     *         url(s) that will be added to remote
     * @return RemoteUpdateCommand with established add url(s)
     */
    public RemoteUpdateCommand setAddUrl(String[] addUrl) {
        this.addUrl = addUrl;
        return this;
    }

    /**
     * @param removeUrl
     *         url(s) that will be removed from remote
     * @return RemoteUpdateCommand with established removeUrl parameter
     */
    public RemoteUpdateCommand setRemoveUrl(String[] removeUrl) {
        this.removeUrl = removeUrl;
        return this;
    }

    /**
     * @param addPushUrl
     *         url(s) that will be added as push to remote
     * @return RemoteUpdateCommand with established push url(s) that will be added
     */
    public RemoteUpdateCommand setAddPushUrl(String[] addPushUrl) {
        this.addPushUrl = addPushUrl;
        return this;
    }

    /**
     * @param remoteName
     *         remote name
     * @return RemoteUpdateCommand with established remote name
     */
    public RemoteUpdateCommand setRemoteName(String remoteName) {
        this.remoteName = remoteName;
        return this;
    }

    /**
     * @param newUrl
     *         url that replaces current remote url
     * @return RemoteUpdateCommand with established new url
     */
    public RemoteUpdateCommand setNewUrl(String newUrl) {
        this.newUrl = newUrl;
        return this;
    }

    /**
     * @param removePushUrl
     *         url(s) that will be removed from push
     * @return RemoteUpdateCommand with established push url(s) that will be removed
     */
    public RemoteUpdateCommand setRemovePushUrl(String[] removePushUrl) {
        this.removePushUrl = removePushUrl;
        return this;
    }
}
