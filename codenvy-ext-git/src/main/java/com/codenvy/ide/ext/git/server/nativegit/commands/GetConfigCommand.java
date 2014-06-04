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

/**
 * Get git configuration.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class GetConfigCommand extends GitCommand<Void> {

    private String  attribute;
    private boolean getList;

    public GetConfigCommand(File place) {
        super(place);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        clear();
        commandLine.add("config");
        if (getList) {
            commandLine.add("--list");
        } else {
            if (attribute == null) {
                throw new GitException("Nothing to get, attribute wasn't set.");
            }
            commandLine.add("--get", attribute);
        }
        start();
        return null;
    }

    /**
     * @param attribute
     *         what to get from config
     * @return GetConfigCommand with established attribute
     */
    public GetConfigCommand setAttribute(String attribute) {
        this.attribute = attribute;
        return this;
    }

    /**
     * @param getList
     *         if <code>true</code> all config will be selected
     * @return GetConfigCommand with established getList parameter
     */
    public GetConfigCommand setGetList(boolean getList) {
        this.getList = getList;
        return this;
    }
}
