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
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.ApplicationStatus;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
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
 * This class controls operations with a custom extension. Such as launching, stopping, getting logs, packaging into a
 * bundle.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionsController.java Jul 3, 2013 3:07:52 PM azatsarynnyy $
 */
@Singleton
public class ExtensionsController {
    private ResourceProvider               resourceProvider;
    private ConsolePart                    console;
    private ExtRuntimeClientService        service;
    private ExtRuntimeLocalizationConstant constant;
    private NotificationManager            notificationManager;
    private Notification                   notification;
    private DtoFactory                     dtoFactory;
    private Project                        currentProject;
    /** Launched app. */
    private ApplicationProcessDescriptor   applicationProcessDescriptor;
    /** Is launching of any application in progress? */
    private boolean                        isLaunchingInProgress;

    /**
     * Create controller.
     *
     * @param resourceProvider
     *         {@link ResourceProvider}
     * @param eventBus
     *         {@link EventBus}
     * @param console
     *         {@link ConsolePart}
     * @param service
     *         {@link ExtRuntimeClientService}
     * @param constant
     *         {@link ExtRuntimeLocalizationConstant}
     * @param notificationManager
     *         {@link NotificationManager}
     * @param dtoFactory
     *         {@link DtoFactory}
     */
    @Inject
    protected ExtensionsController(ResourceProvider resourceProvider, EventBus eventBus, final ConsolePart console,
                                   ExtRuntimeClientService service, ExtRuntimeLocalizationConstant constant,
                                   NotificationManager notificationManager, DtoFactory dtoFactory) {
        this.resourceProvider = resourceProvider;
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
        return applicationProcessDescriptor != null;
    }

    /** Launch Codenvy extension. */
    public void launch() {
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
        notification = new Notification(constant.applicationStarting(currentProject.getName()), PROGRESS);
        notificationManager.showNotification(notification);

        try {
            service.run(currentProject.getName(),
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
                                onFail(constant.startApplicationFailed(currentProject.getName()), exception, true);
                            }
                        });
        } catch (RequestException e) {
            isLaunchingInProgress = false;
            applicationProcessDescriptor = null;
            onFail(constant.startApplicationFailed(currentProject.getName()), e, true);
        }
    }

    /** Get logs of the currently launched application. */
    public void getLogs() {
        final Link viewLogsLink = getAppLink(applicationProcessDescriptor, LinkRel.VIEW_LOGS);
        if (viewLogsLink == null) {
            onFail(constant.getApplicationLogsFailed(), null, false);
        }

        try {
            service.getLogs(viewLogsLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    console.printf(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    onFail(constant.getApplicationLogsFailed(), exception, false);
                }
            });
        } catch (RequestException e) {
            onFail(constant.getApplicationLogsFailed(), e, false);
        }
    }

    /** Stop the currently launched application. */
    public void stop() {
        final Link stopLink = getAppLink(applicationProcessDescriptor, LinkRel.STOP);
        if (stopLink == null) {
            onFail(constant.stopApplicationFailed(currentProject.getName()), null, false);
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
                    onFail(constant.stopApplicationFailed(currentProject.getName()), exception, false);
                }
            });
        } catch (RequestException e) {
            onFail(constant.stopApplicationFailed(currentProject.getName()), e, false);
        }
    }

    /** Create Tomcat bundle with Codenvy application that will contains activated custom extension. */
    public void pack() {
//        currentProject = resourceProvider.getActiveProject();
//        if (currentProject == null) {
//            Window.alert("Project is not opened.");
//            return;
//        }
//
//        final Notification packNotification =
//                new Notification(constant.applicationBuilding(currentProject.getName()), PROGRESS);
//        notificationManager.showNotification(packNotification);
//        try {
//            service.build(resourceProvider.getVfsId(), currentProject.getId(), true,
//                          new RequestCallback<String>(new StringUnmarshaller()) {
//                              protected void onSuccess(String url) {
//                                  packNotification.setStatus(FINISHED);
//                                  packNotification.setMessage(constant.applicationBuilt(currentProject.getName()));
//                                  console.print(constant.getBundle(url));
//                              }
//
//                              @Override
//                              protected void onFailure(Throwable exception) {
//                                  String message = constant.buildApplicationFailed(currentProject.getName());
//                                  if (exception != null && exception.getMessage() != null) {
//                                      message += ": " + exception.getMessage();
//                                  }
//                                  packNotification.setStatus(FINISHED);
//                                  packNotification.setType(ERROR);
//                                  console.print(message);
//                              }
//                          });
//        } catch (WebSocketException e) {
//            packNotification.setStatus(FINISHED);
//            packNotification.setType(ERROR);
//            packNotification.setMessage(e.getMessage());
//        }
    }

    /**
     * Performs actions after application was successfully launched.
     *
     * @param applicationProcessDescriptor
     */
    private void afterApplicationLaunched(ApplicationProcessDescriptor applicationProcessDescriptor) {
        this.applicationProcessDescriptor = applicationProcessDescriptor;
//        UrlBuilder builder = new UrlBuilder();
//        final String uri = builder.setProtocol("http:").setHost(launchedApp.getHost())
//                                  .setPort(launchedApp.getPort())
//                                  .setPath("ide" + '/' + Utils.getWorkspaceName())
//                                  .setParameter("h", launchedApp.getCodeServerHost())
//                                  .setParameter("p", String.valueOf(launchedApp.getCodeServerPort())).buildString();
        final String uri = "http://127.0.0.1:8080" /*applicationProcessDescriptor.getUrl()*/;
        console.print(constant.applicationStartedOnUrls(currentProject.getName(),
                                                        "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>"));
        notification.setStatus(FINISHED);
    }

    private void onFail(String message, Throwable exception, boolean notify) {
        if (exception != null && exception.getMessage() != null) {
            message += ": " + exception.getMessage();
        }
        if (notify) {
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(message);
        } else {
            console.print(message);
        }
    }

    private void startCheckingStatus(final ApplicationProcessDescriptor app) {
        new Timer() {
            @Override
            public void run() {
                try {
                    service.getStatus(
                            getAppLink(app, LinkRel.STATUS),
                            new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                @Override
                                protected void onSuccess(String response) {
                                    ApplicationProcessDescriptor newDescriptor =
                                            dtoFactory.createDtoFromJson(response,
                                                                         ApplicationProcessDescriptor.class);

                                    ApplicationStatus status = newDescriptor.getStatus();
                                    if (status == ApplicationStatus.RUNNING) {
                                        afterApplicationLaunched(newDescriptor);
                                    } else if (status == ApplicationStatus.STOPPED || status == ApplicationStatus.NEW) {
                                        schedule(3000);
                                    } else if (status == ApplicationStatus.CANCELLED) {
                                        isLaunchingInProgress = false;
                                        applicationProcessDescriptor = null;
                                        onFail(constant.startApplicationFailed(currentProject.getName()), null, true);
                                    }
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    isLaunchingInProgress = false;
                                    applicationProcessDescriptor = null;
                                    onFail(constant.startApplicationFailed(currentProject.getName()),
                                           exception, true);
                                }
                            });
                } catch (RequestException e) {
                    isLaunchingInProgress = false;
                    applicationProcessDescriptor = null;
                    onFail(constant.startApplicationFailed(currentProject.getName()), e, true);
                }
            }
        }.run();
    }

    private Link getAppLink(ApplicationProcessDescriptor app, LinkRel linkRel) {
        Link linkToReturn = null;
        List<Link> links = app.getLinks();
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            if (link.getRel().equalsIgnoreCase(linkRel.getValue()))
                linkToReturn = link;
        }
        return linkToReturn;
    }

    /** Enum of known runner links with its rels. */
    private static enum LinkRel {
        STOP("stop"),
        VIEW_LOGS("view logs"),
        STATUS("get status");
        private final String value;

        private LinkRel(String rel) {
            this.value = rel;
        }

        private String getValue() {
            return value;
        }
    }
}
