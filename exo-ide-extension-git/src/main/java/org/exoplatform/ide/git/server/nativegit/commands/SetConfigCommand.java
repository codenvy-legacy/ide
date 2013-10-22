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

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
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
