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

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.shared.GitUser;
import com.codenvy.ide.ext.git.shared.Revision;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Show commit logs
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class LogCommand extends GitCommand<List<Revision>> {

    private int    count;
    private String branch;

    public LogCommand(File place) {
        super(place);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public List<Revision> execute() throws GitException {
        clear();
        commandLine.add("log")
                   .add("--format=%an#%ae#%cn#%ce#%cd#%H#%s")
                   .add("--date=raw");
        if (branch != null) {
            commandLine.add(branch);
        }
        if (count > 0) {
            commandLine.add("-" + count);
        }
        start();
        List<Revision> list = new LinkedList<>();
        for (String oneRev : getOutput()) {
            String[] elements = oneRev.split("#");
            GitUser committer = DtoFactory.getInstance().createDto(GitUser.class).withName(elements[2]).withEmail(elements[3]);
            long commitTime = Long.parseLong(elements[4].substring(0, elements[4].indexOf(" "))) * 1000L;
            String commitId = elements[5];
            StringBuilder commitMessage = new StringBuilder();
            for (int i = 6; i < elements.length; i++) {
                commitMessage.append(elements[i]);
            }
            Revision revision = DtoFactory.getInstance().createDto(Revision.class).withId(commitId).withMessage(commitMessage.toString()).withCommitTime(commitTime).withCommitter(committer);
            list.add(revision);
        }
        return list;
    }

    /**
     * @param count
     *         log objects limit
     * @return LogCommand with established limit of log objects
     * @throws GitException
     */
    public LogCommand setCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * @param branch
     *         branch
     * @return LogCommand with established branch
     */
    public LogCommand setBranch(String branch) {
        this.branch = branch;
        return this;
    }
}
