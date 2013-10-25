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

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
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
