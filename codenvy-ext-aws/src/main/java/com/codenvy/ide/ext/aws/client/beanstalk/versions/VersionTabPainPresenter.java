r/*
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
package com.codenvy.ide.ext.aws.client.beanstalk.versions;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.delete.DeleteVersionPresenter;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.deploy.DeployVersionPresenter;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationVersionListUnmarshaller;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for displaying version information for selected application.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class VersionTabPainPresenter implements Presenter, VersionTabPainView.ActionDelegate {
    private VersionTabPainView      view;
    private EventBus                eventBus;
    private LoginPresenter          loginPresenter;
    private BeanstalkClientService  service;
    private DeployVersionPresenter  deployVersionPresenter;
    private DeleteVersionPresenter  deleteVersionPresenter;
    private AWSLocalizationConstant constant;
    private ResourceProvider        resourceProvider;
    private NotificationManager     notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param loginPresenter
     * @param service
     * @param deployVersionPresenter
     * @param deleteVersionPresenter
     * @param constant
     * @param resourceProvider
     * @param notificationManager
     */
    @Inject
    public VersionTabPainPresenter(VersionTabPainView view, EventBus eventBus, LoginPresenter loginPresenter,
                                   BeanstalkClientService service, DeployVersionPresenter deployVersionPresenter,
                                   DeleteVersionPresenter deleteVersionPresenter, AWSLocalizationConstant constant,
                                   ResourceProvider resourceProvider, NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.deployVersionPresenter = deployVersionPresenter;
        this.deleteVersionPresenter = deleteVersionPresenter;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeployVersionClicked(final ApplicationVersionInfo object) {
        deployVersionPresenter.showDialog(object.getApplicationName(), object.getVersionLabel(), new AsyncCallback<EnvironmentInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(EnvironmentInfo result) {
                if (result == null) {
                    Notification notification = new Notification(constant.deployVersionFailed(object.getVersionLabel()), ERROR);
                    notificationManager.showNotification(notification);
                } else {
                    getVersions();
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteVersionClicked(ApplicationVersionInfo object) {
        deleteVersionPresenter.showDialog(object, new AsyncCallback<ApplicationVersionInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(ApplicationVersionInfo result) {
                getVersions();
            }
        });
    }

    /** Get list of versions for selected application. */
    public void getVersions() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getVersions();
            }
        };
        ApplicationVersionListUnmarshaller unmarshaller = new ApplicationVersionListUnmarshaller();

        try {
            service.getVersions(resourceProvider.getVfsInfo().getId(), resourceProvider.getActiveProject().getId(),
                                new AwsAsyncRequestCallback<JsonArray<ApplicationVersionInfo>>(unmarshaller, loggedInHandler, null,
                                                                                               loginPresenter) {
                                    @Override
                                    protected void onSuccess(JsonArray<ApplicationVersionInfo> result) {
                                        List<ApplicationVersionInfo> versions = new ArrayList<ApplicationVersionInfo>(result.size());
                                        for (int i = 0; i < result.size(); i++) {
                                            versions.add(result.get(i));
                                        }

                                        view.setVersions(versions);
                                    }

                                    @Override
                                    protected void processFail(Throwable exception) {
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
