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

import com.codenvy.api.core.util.CommandLine;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.nativegit.CommandProcess;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for all git commands
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public abstract class GitCommand<T> {

    private final File repository;
    private int timeout = -1;
    private   String       SSHScriptPath;
    private   String       askPassScriptPath;
    protected List<String> output;
    protected CommandLine  commandLine;

    /**
     * @param repository
     *         directory where command will be executed
     */
    public GitCommand(File repository) {
        this.repository = repository;
        commandLine = new CommandLine();
        output = new LinkedList<>();
    }

    /**
     * @return git command result
     * @throws GitException
     *         when command execution failed or command execution exit value is not 0
     */
    public abstract T execute() throws GitException;

    public File getRepository() {
        return repository;
    }

    /**
     * If command needs ssh, then it needs path to ssh script,
     * that use stored key.
     *
     * @param SSHScriptPath
     *         path to ssh script
     */
    public void setSSHScriptPath(String SSHScriptPath) {
        this.SSHScriptPath = SSHScriptPath;
    }

    /** @return command output as {@link List} */
    public List<String> getOutput() {
        return output;
    }

    /** @return current command line */
    public CommandLine getCommandLine() {
        return new CommandLine(commandLine);
    }

    public String getOutputMessage() {
        StringBuilder builder = new StringBuilder();
        int size = output.size();
        for (int i = 0; i < size - 1; i++) {
            builder.append(output.get(i)).append("\n");
        }
        if (size != 0) {
            builder.append(output.get(size - 1));
        }
        return builder.toString();
    }

    /** @return path to ssh script */
    public String getSSHScriptPath() {
        return SSHScriptPath;
    }

    /** @return GitCommand with timeout */
    public void setAskPassScriptPath(String askPassScriptPath) {
        this.askPassScriptPath = askPassScriptPath;
    }

    public String getAskPassScriptPath() {
        return askPassScriptPath;
    }

    /**
     * @param timeout
     *         command execution timeout in seconds
     * @return GitCommand with timeout
     */
    public GitCommand setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /** @return command execution timeout in seconds */
    public int getTimeout() {
        return timeout;
    }

    /** Command line initialization. */
    protected void clear() {
        commandLine.clear().add("git");
        output.clear();
    }

    /**
     * Executes git command.
     *
     * @throws GitException
     *         when command execution failed or command execution exit value is not 0
     */
    protected void start() throws GitException {
        CommandProcess.executeGitCommand(this, output);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String command : commandLine.asArray()) {
            builder.append(command).append(" ");
        }
        return builder.toString();
    }
}
