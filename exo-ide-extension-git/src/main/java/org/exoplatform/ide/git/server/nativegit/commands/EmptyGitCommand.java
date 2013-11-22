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
import java.util.LinkedList;
import java.util.List;

/**
 * Used with specific git commands
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class EmptyGitCommand extends GitCommand<Void> {

    private List<String> parameters = new LinkedList<>();

    public EmptyGitCommand(File place) {
        super(place);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        clear();
        commandLine.add(parameters);
        start();
        return null;
    }

    /**
     * @param nextParameter
     *         next parameter
     * @return EmptyGitCommand with established next parameter
     */
    public EmptyGitCommand setNextParameter(String nextParameter) {
        parameters.add(nextParameter);
        return this;
    }
}
