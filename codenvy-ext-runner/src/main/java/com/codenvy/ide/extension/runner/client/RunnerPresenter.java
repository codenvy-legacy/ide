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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.annotations.NotNull;
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
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * The presenter provides run java application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class RunnerPresenter  {
    /** Name of 'JRebel' project property. */
    private static final String JREBEL = "jrebel";
    /** Channel identifier to receive events when application stop. */
    private String                         applicationStoppedChannel;
    private ApplicationRunnerClientService service;
    private String                         restContext;
    private EventBus                       eventBus;
    private HandlerRegistration            projectBuildHandler;
    private Project                        project;
    private ApplicationProcessDescriptor   runningApp;
    private ResourceProvider               resourceProvider;
    private RunnerLocalizationConstant     constant;
    private ConsolePart                    console;
    private MessageBus                     messageBus;
    private NotificationManager            notificationManager;
    private DtoFactory dtoFactory;
    /** Handler for processing debugger disconnected event. */
    private SubscriptionHandler<Object> applicationStoppedHandler;
    private Notification                notification;

    /**
     * Create presenter.
     *
     * @param restContext
     * @param service
     * @param eventBus
     * @param resourceProvider
     * @param constant
     * @param console
     * @param messageBus
     * @param notificationManager
     */
    @Inject
    protected RunnerPresenter(@Named("restContext") String restContext,
                              ApplicationRunnerClientService service,
                              EventBus eventBus,
                              ResourceProvider resourceProvider,
                              RunnerLocalizationConstant constant,
                              ConsolePart console,
                              MessageBus messageBus,
                              NotificationManager notificationManager,
                              DtoFactory dtoFactory) {
        this.restContext = restContext;
        this.service = service;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.console = console;
        this.messageBus = messageBus;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        applicationStoppedHandler = new SubscriptionHandler<Object>() {
            @Override
            protected void onMessageReceived(Object result) {
                try {
                    RunnerPresenter.this.messageBus.unsubscribe(applicationStoppedChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                try {
                    RunnerPresenter.this.messageBus.unsubscribe(applicationStoppedChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }
            }
        };
        this.eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                // do nothing
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                doStopApp();
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
                // do nothing
            }
        });
    }

    /** Runs java application. */
    public void runApplication() {
        this.project = resourceProvider.getActiveProject();

    }


    /**
     * Run application by sending request over WebSocket or HTTP.
     *
     * @param warUrl
     *         location of .war file
     */
    private void runApplication(@NotNull String warUrl) {
     runApplicationREST();
    }

    /**
     * Run application by sending request over HTTP.
     *
     * @param warUrl
     *         location of .war file
     */
    private void runApplicationREST() {


        try {
            service.runApplication(project.getName(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    onApplicationStarted(dtoFactory.createDtoFromJson(result, ApplicationProcessDescriptor.class));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    onApplicationStartFailure(exception);
                }
            });
        } catch (RequestException e) {
            onApplicationStartFailure(e);
        }
    }



    /**
     * Performs action when application successfully started.
     *
     * @param app
     *         {@link ApplicationProcessDescriptor} which is started
     */
    private void onApplicationStarted(@NotNull ApplicationProcessDescriptor app) {
        runningApp = app;
        String msg = constant.applicationStarted(app.getUrl());
//        msg += "<br>" + constant.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
//        console.print(msg);

        notification.setStatus(FINISHED);
        notification.setMessage(msg);

//        try {
//            applicationStoppedChannel = RunnerExtension.APPLICATION_STOP_CHANNEL + app.getName();
//            messageBus.subscribe(applicationStoppedChannel, applicationStoppedHandler);
//        } catch (WebSocketException e) {
//            // nothing to do
//        }
    }

    /**
     * Returns application URLs as string.
     *
     * @param application
     *         {@link ApplicationInstance} application
     * @return application URLs
     */
    @NotNull
    private String getAppUrlsAsString(@NotNull ApplicationProcessDescriptor application) {
        String appUris = "";
        UrlBuilder builder = new UrlBuilder();
        String uri = builder.setProtocol("http").setHost(application.getUrl()).buildString();
        appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
        return appUris;
    }

    /**
     * Performs action when application failed to start.
     *
     * @param exception
     *         problem which happened
     */
    private void onApplicationStartFailure(Throwable exception) {
        String msg = constant.startApplicationFailed();
        if (exception != null && exception.getMessage() != null) {
            msg += " : " + exception.getMessage();
        }
        Notification notification = new Notification(msg, INFO);
        notificationManager.showNotification(notification);
    }

    /**
     * Check whether application is run.
     *
     * @return <code>true</code> if the application is run, and <code>false</code> otherwise
     */
    public boolean isAppRunning() {
        return runningApp != null;
    }

    /** @return running application */
    public ApplicationProcessDescriptor getRunningApp() {
        return runningApp;
    }

    /** Stop application. */
    public void doStopApp() {
//        if (runningApp != null) {
//            try {
//                service.stopApplication(runningApp, new AsyncRequestCallback<String>() {
//                    @Override
//                    protected void onSuccess(String result) {
//                        if (runningApp != null) {
//                            appStopped(runningApp.getName());
//                        }
//                    }
//
//                    @Override
//                    protected void onFailure(Throwable exception) {
//                        String message = exception.getMessage() != null ? exception.getMessage() : constant.stopApplicationFailed();
//                        Notification notification = new Notification(message, ERROR);
//                        notificationManager.showNotification(notification);
//
//                        if (exception instanceof ServerException) {
//                            ServerException serverException = (ServerException)exception;
//                            if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null &&
//                                serverException.getMessage().contains("not found") && runningApp != null) {
//                                appStopped(runningApp.getName());
//                            }
//                        }
//                    }
//                });
//            } catch (RequestException e) {
//                eventBus.fireEvent(new ExceptionThrownEvent(e));
//                Notification notification = new Notification(e.getMessage(), ERROR);
//                notificationManager.showNotification(notification);
//            }
//        }
    }

    /**
     * Show message about stopped some application.
     *
     * @param appName
     *         application name
     */
    private void appStopped(@NotNull String appName) {
//        String msg = constant.applicationStoped(appName);
//        Notification notification = new Notification(msg, INFO);
//        notificationManager.showNotification(notification);
//        console.clear();
//        runningApp = null;
    }
}