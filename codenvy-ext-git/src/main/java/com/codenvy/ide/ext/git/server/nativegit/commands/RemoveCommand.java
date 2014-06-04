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

import java.io.File;
import java.util.List;

/**
 * Remove files
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoveCommand extends GitCommand<Void> {

    private List<String> listOfFiles;
    private boolean  cached;

    public RemoveCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (listOfFiles == null) {
            throw new GitException("Nothing to remove.");
        }
        clear();
        commandLine.add("rm");
        commandLine.add(listOfFiles);
        if (cached) {
            commandLine.add("--cached");
        }
        start();
        return null;
    }

    /**
     * @param listOfFiles
     *         files to remove
     * @return RemoveCommand with established listOfFiles
     */
    public RemoveCommand setListOfFiles(List<String> listOfFiles) {
        this.listOfFiles = listOfFiles;
        return this;
    }

    public RemoveCommand setCached(boolean cached) {
        this.cached = cached;
        return this;
    }
}
