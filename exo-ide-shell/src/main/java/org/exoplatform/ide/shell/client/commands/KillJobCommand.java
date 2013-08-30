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
package org.exoplatform.ide.shell.client.commands;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.JobService;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.model.ClientCommand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Kills job by id.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 12, 2012 5:20:49 PM anya $
 */
public class KillJobCommand extends ClientCommand {
    private static final Set<String> commands = new HashSet<String>();

    static {
        commands.add("kill");
    }

    public KillJobCommand() {
        super(commands, new Options(), CloudShell.messages.killHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        @SuppressWarnings("unchecked")
        List<String> args = commandLine.getArgList();
        args.remove(0);

        if (commandLine.hasOption('h')) {
            printHelp(CloudShell.messages.killUsage());
            return;
        }

        if (args.isEmpty()) {
            printHelp(CloudShell.messages.killUsage());
            return;
        } else if (args.size() == 1) {
            String jobId = args.get(0);
            try {
                JobService.getService().killJob(jobId, new AsyncRequestCallback<StringBuilder>() {

                    @Override
                    protected void onSuccess(StringBuilder result) {
                        CloudShell.console().println(CloudShell.messages.killJobSuccess());
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        CloudShell.console().println(CloudShell.messages.killError());
                    }
                });
            } catch (RequestException e) {
                CloudShell.console().println(CloudShell.messages.killError());
            }
        }
    }

}
