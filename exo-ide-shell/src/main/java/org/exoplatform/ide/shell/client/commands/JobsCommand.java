/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
