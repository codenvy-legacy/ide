/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.aws.client.beanstalk.environments;

import com.codenvy.ide.api.parts.ConsolePart;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
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

    private ConsolePart console;

    private BeanstalkClientService service;

    private LoginPresenter loginPresenter;

    private AWSLocalizationConstant constant;

    public EnvironmentStatusChecker(ResourceProvider resourceProvider, Project project, EnvironmentInfo environmentToCheck,
                                    boolean showEvents, RequestStatusHandler statusHandler, EventBus eventBus, ConsolePart console,
                                    BeanstalkClientService service, LoginPresenter loginPresenter, AWSLocalizationConstant constant) {
        this.resourceProvider = resourceProvider;
        this.project = project;
        this.environmentToCheck = environmentToCheck;
        this.showEvents = showEvents;
        this.statusHandler = statusHandler;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.constant = constant;
    }

    /** Start checking environment status. */
    public void startChecking() {
        previousStatus = environmentToCheck.getStatus();
        statusHandler.requestInProgress(environmentToCheck.getId());
        checkEnvironmentStatusTimer.schedule(delay);
    }

    private Timer checkEnvironmentStatusTimer = new Timer() {
        @Override
        public void run() {
            if (showEvents) {
                outputEvents();
            }

            GWT.log("Timer start!");

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

    private void checkEnvironmentStatus(final AsyncCallback<Boolean> callback) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                checkEnvironmentStatus(callback);
            }
        };

        DtoClientImpls.EnvironmentInfoImpl environmentInfo = DtoClientImpls.EnvironmentInfoImpl.make();
        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller(environmentInfo);

        try {
            service.getEnvironmentInfo(environmentToCheck.getId(),
                                       new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                           @Override
                                           protected void processFail(Throwable exception) {
                                               String message = constant.unableToGetEnvironmentInfo(environmentToCheck.getName());
                                               if (exception instanceof ServerException && exception.getMessage() != null) {
                                                   message += "<br>" + exception.getMessage();
                                               }

                                               console.print(message);
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
            console.print(e.getMessage());
        }
    }

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

        DtoClientImpls.EventsListImpl eventsList = DtoClientImpls.EventsListImpl.make();
        EventsListUnmarshaller unmarshaller = new EventsListUnmarshaller(eventsList);

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
                                                         console.print(event.getMessage());
                                                     }
                                                     lastReceivedEventTime = (long)events.get(events.size() - 1).getEventDate() + 1;
                                                 }
                                             }
                                         });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
            //todo do somethind with update
//            eventBus.fireEvent(new EnvironmentInfoChangedEvent(env));
        }

        StringBuilder message = new StringBuilder();
        if (env.getStatus() == EnvironmentStatus.Ready) {
            statusHandler.requestFinished(env.getId());

            message.append(constant.createApplicationStartedOnUrl(
                    env.getApplicationName(), getAppUrl(env)));

            if (env.getHealth() != EnvironmentHealth.Green) {
                message.append(", but health status of the application's environment is " + env.getHealth().name());
            }
            console.print(message.toString());
        } else if (env.getStatus() == EnvironmentStatus.Terminated) {
            statusHandler.requestFinished(env.getId());

            message.append(constant.terminateEnvironmentSuccess(env.getName()));
            console.print(message.toString());
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
