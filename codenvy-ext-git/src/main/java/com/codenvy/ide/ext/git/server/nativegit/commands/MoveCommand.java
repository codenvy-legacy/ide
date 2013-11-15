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
 * Move files.
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class MoveCommand extends GitCommand<Void> {

    private String target;
    private String source;

    public MoveCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (source == null || target == null) {
            throw new GitException("Target or source wasn't set.");
        }
        clear();
        commandLine.add("mv", source, target);
        start();
        return null;
    }

    /**
     * @param target
     *         file where source will be moved
     * @return MoveCommand with established target parameter
     */
    public MoveCommand setTarget(String target) {
        this.target = target;
        return this;
    }

    /**
     * @param source
     *         file that will be moved to target
     * @return MoveCommand with established source parameter
     */
    public MoveCommand setSource(String source) {
        this.source = source;
        return this;
    }
}
