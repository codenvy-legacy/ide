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
