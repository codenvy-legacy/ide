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
package org.exoplatform.ide.shell.client.maven;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.Environment;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.marshal.StringUnmarshaller;
import org.exoplatform.ide.shell.client.model.ClientCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 4, 2012 11:44:11 AM anya $
 */
public class BuildCommand extends ClientCommand {
    private static final Set<String> commads = new HashSet<String>();

    static {
        commads.add("mvn clean package");
    }

    /** Id of the current build. */
    private String buildId;

    private static final int delay = 3000;

    /** Timer for getting build status. */
    private Timer getBuildStatusTimer = new Timer() {
        @Override
        public void run() {
            try {
                AutoBean<BuildStatus> buildStatus = CloudShell.AUTO_BEAN_FACTORY.create(BuildStatus.class);
                AutoBeanUnmarshaller<BuildStatus> unmarshaller = new AutoBeanUnmarshaller<BuildStatus>(buildStatus);
                MavenService.getService().status(buildId, new AsyncRequestCallback<BuildStatus>(unmarshaller) {
                    @Override
                    protected void onSuccess(BuildStatus response) {
                        BuildStatus.Status status = response.getStatus();

                        if (BuildStatus.Status.IN_PROGRESS == status) {
                            schedule(delay);
                        } else if (BuildStatus.Status.FAILED == status) {
                            printLog();
                        } else if (BuildStatus.Status.SUCCESSFUL == status) {
                            CloudShell.console().println(CloudShell.messages.mvnBuildSuccess(response.getDownloadUrl()));
                        }
                    }

                    protected void onFailure(Throwable exception) {
                        CloudShell.console().println(exception.getMessage());
                    }

                    ;

                });
            } catch (RequestException e) {
                CloudShell.console().println(e.getMessage());
            }
        }
    };

    /**
     *
     */
    public BuildCommand() {
        super(commads, new Options(), CloudShell.messages.mvnBuildHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        String projectId = Environment.get().getValue("projectid");
        if (projectId == null || projectId.isEmpty()) {
            CloudShell.console().print(CloudShell.messages.requiredPropertyNotSet("projectid") + "\n");
            return;
        }

        String vfsId = Environment.get().getValue("vfsid");
        if (vfsId == null || vfsId.isEmpty()) {
            CloudShell.console().print(CloudShell.messages.requiredPropertyNotSet("vfsid") + "\n");
            return;
        }

        try {
            MavenService.getService().build(projectId, vfsId,
                                            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {

                                                @Override
                                                protected void onSuccess(StringBuilder result) {
                                                    buildId = result.substring(result.lastIndexOf("/") + 1);
                                                    CloudShell.console().print("");
                                                    getBuildStatusTimer.schedule(delay);
                                                }

                                                @Override
                                                protected void onFailure(Throwable exception) {
                                                    CloudShell.console().println(exception.getMessage());
                                                }
                                            });
        } catch (RequestException e) {
            CloudShell.console().println(e.getMessage());
        }
    }

    /** Gets build log and prints it on console. */
    private void printLog() {
        try {
            MavenService.getService().log(buildId,
                                          new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                                              @Override
                                              protected void onSuccess(StringBuilder result) {
                                                  CloudShell.console().print(result.toString());
                                              }

                                              @Override
                                              protected void onFailure(Throwable exception) {
                                                  CloudShell.console().println(exception.getMessage());
                                              }
                                          });
        } catch (RequestException e) {
            CloudShell.console().println(e.getMessage());
        }
    }
}
