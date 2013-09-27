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
import org.exoplatform.ide.shell.client.marshal.StringUnmarshaller;
import org.exoplatform.ide.shell.client.model.ClientCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * Get the list of running jobs (asynchronous tasks).
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 13, 2012 10:16:28 AM anya $
 */
public class JobsCommand extends ClientCommand {
    private static final Set<String> commands = new HashSet<String>();

    static {
        commands.add("jobs");
    }

    public JobsCommand() {
        super(commands, new Options(), CloudShell.messages.jobsHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        try {
            JobService.getService().getJobs(
                    new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {

                        @Override
                        protected void onSuccess(StringBuilder result) {
                            CloudShell.console().print(result.toString());
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            CloudShell.console().print(CloudShell.messages.jobsError());
                        }
                    });
        } catch (RequestException e) {
            CloudShell.console().print(CloudShell.messages.jobsError());
        }
    }
}
