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
 * Show repository status
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class StatusCommand extends GitCommand<List<String>> {

    boolean isShort;

    public StatusCommand(File repository) {
        super(repository);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public List<String> execute() throws GitException {
        clear();
        commandLine.add("status");
        if (isShort) {
            commandLine.add("--short");
        }
        start();
        return getOutput();
    }

    /**
     * @param aShort
     *         short status format
     * @return StatusCommand withe established short parameter
     */
    public StatusCommand setShort(boolean aShort) {
        isShort = aShort;
        return this;
    }
}
