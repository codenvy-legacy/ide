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

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.rebuild.RebuildEnvironmentPresenter;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.restart.RestartEnvironmentPresenter;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.terminate.TerminateEnvironmentPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentsInfoListUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentsLogListUnmarshaller;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.InstanceLog;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.*;

/**
 * Presenter that allow user to control environment state.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EnvironmentTabPainPresenter implements Presenter, EnvironmentTabPainView.ActionDelegate {
    private EnvironmentTabPainView        view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private BeanstalkClientService        service;
    private ResourceProvider              resourceProvider;
    private EditConfigurationPresenter    editConfigurationPresenter;
    private RestartEnvironmentPresenter   restartEnvironmentPresenter;
    private RebuildEnvironmentPresenter   rebuildEnvironmentPresenter;
    private TerminateEnvironmentPresenter terminateEnvironmentPresenter;
    private AWSLocalizationConstant       constant;
    private NotificationManager           notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param service
     * @param resourceProvider
     * @param editConfigurationPresenter
     * @param restartEnvironmentPresenter
     * @param rebuildEnvironmentPresenter
     * @param terminateEnvironmentPresenter
     * @param constant
     * @param notificationManager
     */
    @Inject
    public EnvironmentTabPainPresenter(EnvironmentTabPainView view, EventBus eventBus, ConsolePart console,
                                       BeanstalkClientService service, ResourceProvider resourceProvider,
                                       EditConfigurationPresenter editConfigurationPresenter,
                                       RestartEnvironmentPresenter restartEnvironmentPresenter,
                                       RebuildEnvironmentPresenter rebuildEnvironmentPresenter,
                                       TerminateEnvironmentPresenter terminateEnvironmentPresenter, AWSLocalizationConstant constant,
                                       NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.editConfigurationPresenter = editConfigurationPresenter;
        this.restartEnvironmentPresenter = restartEnvironmentPresenter;
        this.rebuildEnvironmentPresenter = rebuildEnvironmentPresenter;
        this.terminateEnvironmentPresenter = terminateEnvironmentPresenter;
        this.constant = constant;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditConfigurationButtonClicked(EnvironmentInfo environment) {
        editConfigurationPresenter.showDialog(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void onRestartButtonClicked(EnvironmentInfo environment) {
        restartEnvironmentPresenter.showDialog(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void onRebuildButtonClicked(EnvironmentInfo environment) {
        rebuildEnvironmentPresenter.showDialog(environment, new AsyncCallback<EnvironmentInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(EnvironmentInfo result) {
                getEnvironments();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onTerminateButtonClicked(EnvironmentInfo environment) {
        terminateEnvironmentPresenter.showDialog(environment, new AsyncCallback<EnvironmentInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(EnvironmentInfo result) {
                getEnvironments();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onGetLogsButtonCLicked(final EnvironmentInfo environment) {
        if (environment == null) {
            return;
        }

        EnvironmentsLogListUnmarshaller unmarshaller = new EnvironmentsLogListUnmarshaller();

        try {
            service.getEnvironmentLogs(environment.getId(), new AsyncRequestCallback<JsonArray<InstanceLog>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonArray<InstanceLog> result) {
                    if (result.size() == 0) {
                        Notification notification = new Notification(constant.logsPreparing(), WARNING);
                        notificationManager.showNotification(notification);
                        return;
                    }

                    StringBuilder message = new StringBuilder();
                    for (int i = 0; i < result.size(); i++) {
                        message.append(getUrl(result.get(i))).append("\n");
                    }

                    console.print(message.toString());
                    Notification notification = new Notification(message.toString(), INFO);
                    notificationManager.showNotification(notification);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message = constant.logsEnvironmentFailed(environment.getName());
                    if (exception instanceof ServerException && exception.getMessage() != null) {
                        message += "<br>" + exception.getMessage();
                    }

                    Notification notification = new Notification(message, ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Get html url link for logs from instance info..
     *
     * @param instanceLog
     *         url for
     * @return html href for logs.
     */
    private String getUrl(InstanceLog instanceLog) {
        String logUrl = instanceLog.getLogUrl();
        if (!logUrl.startsWith("http")) {
            logUrl = "http://" + logUrl;
        }
        logUrl =
                "<a href=\"" + logUrl + "\" target=\"_blank\">"
                + constant.viewLogFromInstance(instanceLog.getInstanceId()) + "</a>";
        return logUrl;
    }

    /** Get environments list. */
    public void getEnvironments() {
        EnvironmentsInfoListUnmarshaller unmarshaller = new EnvironmentsInfoListUnmarshaller();
        try {
            service.getEnvironments(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                                    new AsyncRequestCallback<JsonArray<EnvironmentInfo>>(unmarshaller) {
                                        @Override
                                        protected void onSuccess(JsonArray<EnvironmentInfo> result) {
                                            List<EnvironmentInfo> environmentInfoList = new ArrayList<EnvironmentInfo>(result.size());

                                            for (int i = 0; i < result.size(); i++) {
                                                environmentInfoList.add(result.get(i));
                                            }

                                            view.setEnvironments(environmentInfoList);
                                        }

                                        @Override
                                        protected void onFailure(Throwable exception) {
                                            eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                            Notification notification = new Notification(exception.getMessage(), ERROR);
                                            notificationManager.showNotification(notification);
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
