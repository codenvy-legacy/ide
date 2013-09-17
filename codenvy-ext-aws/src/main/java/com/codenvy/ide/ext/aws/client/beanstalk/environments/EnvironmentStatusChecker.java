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
package com.codenvy.ide.ext.aws.client.beanstalk.environments;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.EventsListUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Checker for getting information about starting environment and it logs.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class EnvironmentStatusChecker {
    /** Need to check application name on server-side. */
    private ResourceProvider resourceProvider;

    /** Need to check application name on server-side. */
    private Project project;

    /** AWS Elastic Beanstalk application's environment to check status. */
    private EnvironmentInfo environmentToCheck;

    /** Show events to output view. */
    private boolean showEvents;

    /** Delay in millisecond between environment status checking. */
    private static final int delay = 2000;

    /** Handler for checking the status of AWS Elastic Beanstalk application's environment. */
    private RequestStatusHandler statusHandler;

    /** Time of last received event. */
    private long lastReceivedEventTime;

    /** Previous environment status. */
    private EnvironmentStatus previousStatus;

    private EventBus eventBus;

    private BeanstalkClientService service;

    private LoginPresenter loginPresenter;

    private AWSLocalizationConstant constant;

    private NotificationManager notificationManager;

    /**
     * Create checker.
     *
     * @param resourceProvider
     * @param project
     * @param environmentToCheck
     * @param showEvents
     * @param statusHandler
     * @param eventBus
     * @param service
     * @param loginPresenter
     * @param constant
     * @param notificationManager
     */
    public EnvironmentStatusChecker(ResourceProvider resourceProvider, Project project, EnvironmentInfo environmentToCheck,
                                    boolean showEvents, RequestStatusHandler statusHandler, EventBus eventBus,
                                    BeanstalkClientService service, LoginPresenter loginPresenter, AWSLocalizationConstant constant,
                                    NotificationManager notificationManager) {
        this.resourceProvider = resourceProvider;
        this.project = project;
        this.environmentToCheck = environmentToCheck;
        this.showEvents = showEvents;
        this.statusHandler = statusHandler;
        this.eventBus = eventBus;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Start checking environment status. */
    public void startChecking() {
        previousStatus = environmentToCheck.getStatus();
        statusHandler.requestInProgress(environmentToCheck.getId());
        checkEnvironmentStatusTimer.schedule(delay);
    }

    /** Timer that perform checking for environment ready status. */
    private Timer checkEnvironmentStatusTimer = new Timer() {
        @Override
        public void run() {
            if (showEvents) {
                outputEvents();
            }

            checkEnvironmentStatus(new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    //ignore
                }

                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        schedule(delay);
                    }
                }
            });
        }
    };

    /**
     * Check environment status.
     *
     * @param callback
     *         callback.
     */
    private void checkEnvironmentStatus(final AsyncCallback<Boolean> callback) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                checkEnvironmentStatus(callback);
            }
        };
        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller();

        try {
            service.getEnvironmentInfo(environmentToCheck.getId(),
                                       new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                           @Override
                                           protected void processFail(Throwable exception) {
                                               String message = constant.unableToGetEnvironmentInfo(environmentToCheck.getName());
                                               if (exception instanceof ServerException && exception.getMessage() != null) {
                                                   message += "<br>" + exception.getMessage();
                                               }

                                               Notification notification = new Notification(message, ERROR);
                                               notificationManager.showNotification(notification);

                                               statusHandler.requestError(environmentToCheck.getId(), exception);
                                           }

                                           @Override
                                           protected void onSuccess(EnvironmentInfo result) {
                                               updateEnvironmentStatus(result);
                                               if (result.getStatus() == EnvironmentStatus.Launching
                                                   || result.getStatus() == EnvironmentStatus.Updating
                                                   || result.getStatus() == EnvironmentStatus.Terminating) {
                                                   callback.onSuccess(true);
                                               } else {
                                                   callback.onSuccess(false);
                                               }
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Show environment events when timer scheduled. */
    private void outputEvents() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                outputEvents();
            }
        };

        DtoClientImpls.ListEventsRequestImpl listEventsRequest = DtoClientImpls.ListEventsRequestImpl.make();
        listEventsRequest.setEnvironmentId(environmentToCheck.getId());
        listEventsRequest.setStartTime(lastReceivedEventTime);

        EventsListUnmarshaller unmarshaller = new EventsListUnmarshaller();

        try {
            service.getApplicationEvents(resourceProvider.getVfsId(), project.getId(), listEventsRequest,
                                         new AwsAsyncRequestCallback<EventsList>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                             @Override
                                             protected void processFail(Throwable exception) {
                                                 //nothing to do
                                             }

                                             @Override
                                             protected void onSuccess(EventsList result) {
                                                 JsonArray<Event> events = result.getEvents();
                                                 if (events.size() > 0) {
                                                     // shows events in chronological order
                                                     for (int i = events.size() - 1; i >= 0; i--) {
                                                         Event event = events.get(i);
                                                         Notification notification = new Notification(event.getMessage(), ERROR);
                                                         notificationManager.showNotification(notification);
                                                     }
                                                     lastReceivedEventTime = (long)events.get(events.size() - 1).getEventDate() + 1;
                                                 }
                                             }
                                         });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Checks the environment status and if it {@link EnvironmentStatus#Ready} or
     * {@link EnvironmentStatus#Terminated} and then print appropriate message to the output view.
     *
     * @param env
     *         {@link EnvironmentInfo}
     */
    private void updateEnvironmentStatus(EnvironmentInfo env) {
        if (env.getStatus() != previousStatus) {
            previousStatus = env.getStatus();
        }

        StringBuilder message = new StringBuilder();
        if (env.getStatus() == EnvironmentStatus.Ready) {
            statusHandler.requestFinished(env.getId());

            message.append(constant.createApplicationStartedOnUrl(
                    env.getApplicationName(), getAppUrl(env)));

            if (env.getHealth() != EnvironmentHealth.Green) {
                message.append(", but health status of the application's environment is " + env.getHealth().name());
            }
            Notification notification = new Notification(message.toString(), INFO);
            notificationManager.showNotification(notification);
        } else if (env.getStatus() == EnvironmentStatus.Terminated) {
            statusHandler.requestFinished(env.getId());

            message.append(constant.terminateEnvironmentSuccess(env.getName()));
            Notification notification = new Notification(message.toString(), INFO);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Returns formatted environment's URL.
     *
     * @param env
     *         environment
     * @return environment URL
     */
    private String getAppUrl(EnvironmentInfo env) {
        String appUrl = env.getCname();
        if (appUrl == null) {
            appUrl = env.getEndpointUrl();
        }
        if (!appUrl.startsWith("http")) {
            appUrl = "http://" + appUrl;
        }
        appUrl = "<a href=\"" + appUrl + "\" target=\"_blank\">" + appUrl + "</a>";
        return appUrl;
    }

}
