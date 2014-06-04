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
package com.codenvy.ide.ext.git.server.nativegit;


import com.codenvy.api.core.util.CancellableProcessWrapper;
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.core.util.ShellFactory;
import com.codenvy.api.core.util.Watchdog;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Executes GitCommand.
 *
 * @author Eugene Voevodin
 */
public class CommandProcess {

    private static final Logger   LOG          = LoggerFactory.getLogger(CommandProcess.class);
    private static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    /**
     * @param command
     *         GitCommand that will be executed
     * @param output
     *         list that will be used as output container
     * @throws GitException
     *         when command execution error occurs
     */
    public static void executeGitCommand(GitCommand command, List<String> output) throws GitException {
        List<String> environment = new LinkedList<>();
        //set up needed environment
        environment.add("HOME=" + System.getProperty("user.home"));
        //if command should be executed with ssh key
        if (command.getSSHScriptPath() != null) {
            environment.add("GIT_SSH=" + command.getSSHScriptPath());
        }
        //if command should be executed with credentials
        if (command.getAskPassScriptPath() != null) {
            environment.add("GIT_ASKPASS=" + command.getAskPassScriptPath());
        }
        String[] env = new String[environment.size()];
        environment.toArray(env);
        String[] line = ShellFactory.getShell().createShellCommand(command.getCommandLine());
        LOG.debug("Executing " + command);
        Process process;
        try {
            process = Runtime.getRuntime().exec(line, env, command.getRepository());
        } catch (IOException e) {
            LOG.error("Process creating failed", e);
            throw new GitException("It is not possible to execute command");
        }
        //process will be stopped after timeout
        Watchdog watcher = null;
        if (command.getTimeout() > 0) {
            watcher = new Watchdog(command.getTimeout(), DEFAULT_UNIT);
            watcher.start(new CancellableProcessWrapper(process));
        }
        //read all process output and store it
        ProcessLineConsumer consumer = new ProcessLineConsumer(output);
        try {
            ProcessUtil.process(process, consumer, consumer);
            process.waitFor();
            /*
            * Check process exit value and search for correct error message
            * without hint and warning messages ant throw it to user.
            * */
            if (process.exitValue() != 0) {
                String message = searchErrorMessage(consumer.getOutput());
                LOG.debug("Command execution failed!\n" + message);
                throw new GitException(message);
            } else {
                LOG.debug("Command was executed successful!");
            }
        } catch (InterruptedException e) {
            Thread.interrupted();
        } catch (IOException e) {
            LOG.error("Cant listen process", e);
            throw new GitException("It is not possible to execute command");
        } finally {
            if (watcher != null) {
                watcher.stop();
            }
        }
    }

    /**
     * Searches useful information in command output
     *
     * @param output
     *         command execution output
     * @return filtered output as message
     */
    private static String searchErrorMessage(List<String> output) {
        //check if troubles with ssh keys
        int i = 0;
        for (int length = output.size(); i < length && output.get(i).indexOf("fatal:") == -1; i++) ;
        StringBuilder builder = new StringBuilder();
        if (i == output.size()) {
            for (String line : output) {
                if (!(line.startsWith("hint:") || line.startsWith("Warning:"))) {
                    builder.append(line).append('\n');
                }
            }
        }
        for (; i < output.size(); i++) {
            if (!(output.get(i).startsWith("hint:") || output.get(i).startsWith("Warning:"))) {
                builder.append(output.get(i)).append('\n');
            }
        }
        if (builder.toString().toLowerCase().contains("fatal: the remote end hung up unexpectedly")) {
            builder.append("SSH key doesn't exist or it is not valid");
        }
        return builder.toString();
    }

    public static class ProcessLineConsumer implements LineConsumer {

        private List<String> output;

        /**
         * @param output
         *         list where will be written output
         */
        public ProcessLineConsumer(List<String> output) {
            this.output = output;
        }

        /** @see com.codenvy.api.core.util.LineConsumer#writeLine(String) */
        @Override
        public void writeLine(String line) throws IOException {
            output.add(line);
        }

        /** @see com.codenvy.api.core.util.LineConsumer#writeLine(String) */
        @Override
        public void close() throws IOException {
            //nothing to close
        }

        /** @return output */
        public List<String> getOutput() {
            return output;
        }
    }
}
