/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
import org.exoplatform.ide.git.shared.RemoteReference;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to execute git ls-remote command.
 *
 * @author Alexander Garagatyi
 */
public class LsRemoteCommand extends GitCommand<Void> {
    private String url;

    public LsRemoteCommand(File repository) {
        super(repository);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (url == null) {
            throw new GitException("Remote repository URL wasn't set.");
        }
        clear();
        commandLine.add("ls-remote", url);
        start();
        return null;
    }

    /**
     * @param url
     *         url of remote repository to get references list
     * @return LsRemoteCommand with established url
     */
    public LsRemoteCommand setRemoteUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Get list of remote references.
     *
     * @see org.exoplatform.ide.git.shared.RemoteReference
     */
    public List<RemoteReference> getRemoteReferences() {
        List<RemoteReference> references = new LinkedList<>();
        for (String outLine : output) {
            String[] parts = outLine.trim().split("\\s");
            String commitId = parts[0];
            String referenceName = parts[1];
            references.add(new RemoteReference(commitId, referenceName));
        }
        return references;
    }
}
