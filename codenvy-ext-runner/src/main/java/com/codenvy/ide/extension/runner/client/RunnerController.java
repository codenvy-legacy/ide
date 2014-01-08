/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.runner.ApplicationStatus;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * This class controls launching application.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunnerController.java Jul 3, 2013 3:07:52 PM azatsarynnyy $
 */
@Singleton
public class RunnerController implements Notification.OpenNotificationHandler {
    private WorkspaceAgent               workspaceAgent;
    private ResourceProvider             resourceProvider;
    private ConsolePart                  console;
    private RunnerClientService          service;
    private RunnerLocalizationConstant   constant;
    private NotificationManager          notificationManager;
    private Notification                 notification;
    private DtoFactory                   dtoFactory;
    private Project                      currentProject;
    /** Launched app. */
    private ApplicationProcessDescriptor applicationProcessDescriptor;
    /** Is launching of any application in progress? */
    private boolean                      isLaunchingInProgress;

    /**
     * Create controller.
     *
     * @param resourceProvider
     *         {@link ResourceProvider}
     * @param workspaceAgent
     *         {@link WorkspaceAgent}
     * @param eventBus
     *         {@link EventBus}
     * @param console
     *         {@link ConsolePart}
     * @param service
     *         {@link RunnerClientService}
     * @param constant
     *         {@link RunnerLocalizationConstant}
     * @param notificationManager
     *         {@link NotificationManager}
     * @param dtoFactory
     *         {@link DtoFactory}
     */
    @Inject
    public RunnerController(ResourceProvider resourceProvider, EventBus eventBus, WorkspaceAgent workspaceAgent,
                            final ConsolePart console, RunnerClientService service,
                            RunnerLocalizationConstant constant, NotificationManager notificationManager,
                            DtoFactory dtoFactory) {
        this.resourceProvider = resourceProvider;
        this.workspaceAgent = workspaceAgent;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                isLaunchingInProgress = false;
                applicationProcessDescriptor = null;
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                isLaunchingInProgress = false;
                if (isAnyAppLaunched()) {
                    stop();
                    console.clear();
                }
                currentProject = null;
                applicationProcessDescriptor = null;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
                // do nothing
            }
        });
    }

    /**
     * Check whether any application is launched.
     *
     * @return <code>true</code> if any application is launched, and <code>false</code> otherwise
     */
    public boolean isAnyAppLaunched() {
        return applicationProcessDescriptor != null && !isLaunchingInProgress;
    }

    /** Run application. */
    public void run() {
        currentProject = resourceProvider.getActiveProject();
        if (currentProject == null) {
            Window.alert("Project is not opened.");
            return;
        }

        if (isLaunchingInProgress) {
            Window.alert("Launching of another application is in progress now.");
            return;
        }

        isLaunchingInProgress = true;
        notification = new Notification(constant.applicationStarting(currentProject.getName()), PROGRESS, this);
        notificationManager.showNotification(notification);

        try {
            service.run(currentProject.getPath(),
                        new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                            @Override
                            protected void onSuccess(String result) {
                                applicationProcessDescriptor =
                                        dtoFactory.createDtoFromJson(result, ApplicationProcessDescriptor.class);
                                startCheckingStatus(applicationProcessDescriptor);
                            }

                            @Override
                            protected void onFailure(Throwable exception) {
                                isLaunchingInProgress = false;
                                applicationProcessDescriptor = null;
                                onFail(constant.startApplicationFailed(currentProject.getName()), exception);
                            }
                        });
        } catch (RequestException e) {
            isLaunchingInProgress = false;
            applicationProcessDescriptor = null;
            onFail(constant.startApplicationFailed(currentProject.getName()), e);
        }
    }

    /** Get logs of the currently launched application. */
    public void getLogs() {
        final Link viewLogsLink = getAppLink(applicationProcessDescriptor, "view logs");
        if (viewLogsLink == null) {
            onFail(constant.getApplicationLogsFailed(), null);
        }

        try {
            service.getLogs(viewLogsLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    console.printf(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    onFail(constant.getApplicationLogsFailed(), exception);
                }
            });
        } catch (RequestException e) {
            onFail(constant.getApplicationLogsFailed(), e);
        }
    }

    /** Stop the currently launched application. */
    public void stop() {
        final Link stopLink = getAppLink(applicationProcessDescriptor, "stop");
        if (stopLink == null) {
            onFail(constant.stopApplicationFailed(currentProject.getName()), null);
        }

        try {
            service.stop(stopLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    applicationProcessDescriptor = null;
                    console.print(constant.applicationStopped(currentProject.getName()));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    onFail(constant.stopApplicationFailed(currentProject.getName()), exception);
                }
            });
        } catch (RequestException e) {
            onFail(constant.stopApplicationFailed(currentProject.getName()), e);
        }
    }

    private void afterApplicationLaunched(ApplicationProcessDescriptor appDescriptor) {
        this.applicationProcessDescriptor = appDescriptor;
        final Link appLink = getAppLink(appDescriptor, "web url");
        if (appLink != null) {
            final String url = appLink.getHref();
            console.print(constant.applicationStartedOnUrls(currentProject.getName(),
                                                            "<a href=\"" + url + "\" target=\"_blank\">" + url + "</a>"));
        }
        notification.setStatus(FINISHED);
    }

    private void onFail(String message, Throwable exception) {
        if (notification != null) {
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(message);
        }

        if (exception != null && exception.getMessage() != null) {
            message += ": " + exception.getMessage();
        }
        console.printf(message);
    }

    private void startCheckingStatus(final ApplicationProcessDescriptor appDescriptor) {
        new Timer() {
            @Override
            public void run() {
                try {
                    service.getStatus(
                            getAppLink(appDescriptor, "get status"),
                            new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                @Override
                                protected void onSuccess(String response) {
                                    ApplicationProcessDescriptor newAppDescriptor =
                                            dtoFactory.createDtoFromJson(response,
                                                                         ApplicationProcessDescriptor.class);

                                    ApplicationStatus status = newAppDescriptor.getStatus();
                                    if (status == ApplicationStatus.RUNNING) {
                                        isLaunchingInProgress = false;
                                        afterApplicationLaunched(newAppDescriptor);
                                    } else if (status == ApplicationStatus.STOPPED || status == ApplicationStatus.NEW) {
                                        schedule(3000);
                                    } else if (status == ApplicationStatus.CANCELLED) {
                                        isLaunchingInProgress = false;
                                        applicationProcessDescriptor = null;
                                        onFail(constant.startApplicationFailed(currentProject.getName()), null);
                                    }
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    isLaunchingInProgress = false;
                                    applicationProcessDescriptor = null;

                                    if (exception instanceof ServerException &&
                                        ((ServerException)exception).getHTTPStatus() == 500) {
                                        ServiceError e = dtoFactory
                                                .createDtoFromJson(exception.getMessage(), ServiceError.class);
                                        onFail(constant.startApplicationFailed(currentProject.getName()) + ": " +
                                               e.getMessage(), null);
                                    } else {
                                        onFail(constant.startApplicationFailed(currentProject.getName()), exception);
                                    }
                                }
                            });
                } catch (RequestException e) {
                    isLaunchingInProgress = false;
                    applicationProcessDescriptor = null;
                    onFail(constant.startApplicationFailed(currentProject.getName()), e);
                }
            }
        }.run();
    }

    private Link getAppLink(ApplicationProcessDescriptor appDescriptor, String rel) {
        List<Link> links = appDescriptor.getLinks();
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel))
                return link;
        }
        return null;
    }

    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }
}
