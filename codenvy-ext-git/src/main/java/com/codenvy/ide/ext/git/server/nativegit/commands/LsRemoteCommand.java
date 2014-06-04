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
import com.codenvy.ide.ext.git.shared.RemoteReference;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vladyslav Zhukovskii
 */
public class LsRemoteCommand extends GitCommand<Void> {
    private String url;

    public LsRemoteCommand(File repository) {
        super(repository);
    }

    /** {@inheritDoc} */
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
     */
    public List<RemoteReference> getRemoteReferences() {
        List<RemoteReference> references = new LinkedList<>();
        for (String outLine : output) {
            String[] parts = outLine.trim().split("\\s");
            String commitId = parts[0];
            String referenceName = parts[1];
            references.add(com.codenvy.dto.server.DtoFactory.getInstance().createDto(RemoteReference.class)
                                                            .withCommitId(commitId)
                                                            .withReferenceName(referenceName));
        }
        return references;
    }
}
