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

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.extruntime.client.marshaller.ApplicationInstanceUnmarshallerWS;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * This class controls operations with a custom extension. Such as launching, stopping, getting logs, packaging into a bundle.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionsController.java Jul 3, 2013 3:07:52 PM azatsarynnyy $
 */
@Singleton
public class ExtensionsController {
    /** Project to launch. */
    private Project                        currentProject;
    private ResourceProvider               resourceProvider;
    private EventBus                       eventBus;
    private ConsolePart                    console;
    private ExtRuntimeClientService        service;
    private ExtRuntimeLocalizationConstant constant;
    private NotificationManager            notificationManager;
    private Notification                   notification;
    /** Launched app. */
    private ApplicationInstance            launchedApp;
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
     */
    @Inject
    protected ExtensionsController(ResourceProvider resourceProvider, EventBus eventBus, ConsolePart console,
                                   ExtRuntimeClientService service, ExtRuntimeLocalizationConstant constant,
                                   NotificationManager notificationManager) {
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;

        init();
    }

    private void init() {
        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                isLaunchingInProgress = false;
                launchedApp = null;
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                isLaunchingInProgress = false;
                if (currentProject != null) {
                    stop();
                    console.clear();
                }
                launchedApp = null;
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
        return launchedApp != null;
    }

    /** Launch Codenvy extension inside Codenvy Platform. */
    public void buildAndLaunch() {
        if (isLaunchingInProgress) {
            Window.alert("Launching of another app is in progress now.");
            return;
        }

        currentProject = resourceProvider.getActiveProject();
        if (currentProject == null) {
            Window.alert("Project is not opened.");
            return;
        }

        isLaunchingInProgress = true;

        notification = new Notification(constant.applicationBuilding(currentProject.getName()), PROGRESS);
        notificationManager.showNotification(notification);

        try {
            service.build(resourceProvider.getVfsId(), currentProject.getId(), false,
                          new RequestCallback<String>(new StringUnmarshaller()) {
                              @Override
                              protected void onSuccess(String url) {
                                  notification.setStatus(FINISHED);
                                  notification.setMessage(constant.applicationBuilt(currentProject.getName()));
                                  launch(url, currentProject);
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  isLaunchingInProgress = false;
                                  launchedApp = null;
                                  notification.setStatus(FINISHED);
                                  notification.setType(ERROR);
                                  String message = constant.buildApplicationFailed(currentProject.getName());
                                  if (exception != null && exception.getMessage() != null) {
                                      message += ": " + exception.getMessage();
                                  }
                                  console.print(message);
                              }
                          });
        } catch (WebSocketException e) {
            isLaunchingInProgress = false;
            launchedApp = null;
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(e.getMessage());
        }
    }

    private void launch(String warUrl, final Project project) {
        ApplicationInstanceUnmarshallerWS unmarshaller = new ApplicationInstanceUnmarshallerWS();
        isLaunchingInProgress = true;
        console.print(constant.applicationStarting(project.getName()));
        try {
            service.launch(warUrl, true, resourceProvider.getVfsId(), project.getId(),
                           new RequestCallback<ApplicationInstance>(unmarshaller) {
                               @Override
                               protected void onSuccess(ApplicationInstance result) {
                                   isLaunchingInProgress = false;
                                   launchedApp = result;
                                   afterApplicationLaunched();
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   isLaunchingInProgress = false;
                                   launchedApp = null;
                                   onFail(constant.startApplicationFailed(project.getName()), exception);
                               }
                           });
        } catch (WebSocketException e) {
            isLaunchingInProgress = false;
            launchedApp = null;
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(e.getMessage());
        }
    }

    /** Get logs of the currently launched application. */
    public void getLogs() {
        if (currentProject == null) {
            Window.alert("Project is not opened.");
            return;
        }

        try {
            service.getLogs(launchedApp.getId(), new AsyncRequestCallback<String>(
                    new com.codenvy.ide.resources.marshal.StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    console.print("<pre>" + result + "</pre>");
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message = constant.getApplicationLogsFailed();
                    if (exception != null && exception.getMessage() != null) {
                        message += ": " + exception.getMessage();
                    }
                    console.print(message);
                }
            });
        } catch (RequestException e) {
            console.print(e.getMessage());
        }
    }

    /** Stop the currently launched application. */
    public void stop() {
        if (currentProject == null) {
            Window.alert("Project is not opened.");
            return;
        }

        try {
            service.stop(launchedApp.getId(),
                         new AsyncRequestCallback<Void>() {
                             @Override
                             protected void onSuccess(Void result) {
                                 launchedApp = null;
                                 console.print(constant.applicationStopped(currentProject.getName()));
                             }

                             @Override
                             protected void onFailure(Throwable exception) {
                                 String message = constant.stopApplicationFailed(currentProject.getName());
                                 if (exception != null && exception.getMessage() != null) {
                                     message += ": " + exception.getMessage();
                                 }
                                 console.print(message);
                             }
                         });
        } catch (RequestException e) {
            console.print(e.getMessage());
        }
    }

    /** Create Tomcat bundle with Codenvy application that will contains activated custom extension. */
    public void pack() {
        currentProject = resourceProvider.getActiveProject();
        if (currentProject == null) {
            Window.alert("Project is not opened.");
            return;
        }

        final Notification packNotification =
                new Notification(constant.applicationBuilding(currentProject.getName()), PROGRESS);
        notificationManager.showNotification(packNotification);
        try {
            service.build(resourceProvider.getVfsId(), currentProject.getId(), true,
                          new RequestCallback<String>(new StringUnmarshaller()) {
                              protected void onSuccess(String url) {
                                  packNotification.setStatus(FINISHED);
                                  packNotification.setMessage(constant.applicationBuilt(currentProject.getName()));
                                  console.print(constant.getBundle(url));
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  String message = constant.buildApplicationFailed(currentProject.getName());
                                  if (exception != null && exception.getMessage() != null) {
                                      message += ": " + exception.getMessage();
                                  }
                                  packNotification.setStatus(FINISHED);
                                  packNotification.setType(ERROR);
                                  console.print(message);
                              }
                          });
        } catch (WebSocketException e) {
            packNotification.setStatus(FINISHED);
            packNotification.setType(ERROR);
            packNotification.setMessage(e.getMessage());
        }
    }

    /** Performs actions after application was successfully launched. */
    private void afterApplicationLaunched() {
        UrlBuilder builder = new UrlBuilder();
        final String uri = builder.setProtocol("http:").setHost(launchedApp.getHost())
                                  .setPort(launchedApp.getPort())
                                  .setPath("ide" + '/' + Utils.getWorkspaceName())
                                  .setParameter("h", launchedApp.getCodeServerHost())
                                  .setParameter("p", String.valueOf(launchedApp.getCodeServerPort())).buildString();
        console.print(constant.applicationStartedOnUrls(currentProject.getName(),
                                                        "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>"));
        notification.setStatus(FINISHED);
    }

    private void onFail(String message, Throwable exception) {
        if (exception != null && exception.getMessage() != null) {
            message += ": " + exception.getMessage();
        }
        notification.setStatus(FINISHED);
        notification.setType(ERROR);
        notification.setMessage(message);
    }

    private class StringUnmarshaller implements Unmarshallable<String> {
        private String payload;

        /** {@inheritDoc} */
        @Override
        public void unmarshal(Message response) throws UnmarshallerException {
            payload = response.getBody();
        }

        /** {@inheritDoc} */
        @Override
        public String getPayload() {
            return payload;
        }
    }

}
