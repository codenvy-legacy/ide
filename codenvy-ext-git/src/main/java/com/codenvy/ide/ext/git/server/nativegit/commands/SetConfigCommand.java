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
 * Set git configuration.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class SetConfigCommand extends GitCommand<Void> {

    private String parameter;
    private String value;

    public SetConfigCommand(File place) {
        super(place);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        clear();
        commandLine.add("config");
        value = value == null || value.length() == 0 ? "\"\"" : value;
        commandLine.add(parameter, value);
        start();
        return null;
    }

    /**
     * @param parameter
     *         git config parameter suck as user.name
     * @param value
     *         value that will used with parameter
     * @return SetConfigCommand with with established value and parameter
     */
    public SetConfigCommand setValue(String parameter, String value) {
        this.parameter = parameter;
        this.value = value;
        return this;
    }

}
