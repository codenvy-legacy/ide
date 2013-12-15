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

package com.codenvy.ide.ext.gae.client.project.general;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.create.CreateApplicationPresenter;
import com.codenvy.ide.ext.gae.client.project.general.logs.LogsPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter that allow user to control general state for application in Google App Engine.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class GeneralTabPanePresenter implements Presenter, GeneralTabPaneView.ActionDelegate {
    private GeneralTabPaneView         view;
    private CreateApplicationPresenter createApplicationPresenter;
    private EventBus                   eventBus;
    private GAEClientService           service;
    private ResourceProvider           resourceProvider;
    private LoginAction                loginAction;
    private GAELocalization            constant;
    private LogsPresenter              logsPresenter;
    private NotificationManager        notificationManager;
    private Project                    project;

    /** Constructor for general control presenter. */
    @Inject
    public GeneralTabPanePresenter(GeneralTabPaneView view, CreateApplicationPresenter createApplicationPresenter, EventBus eventBus,
                                   GAEClientService service, ResourceProvider resourceProvider, LoginAction loginAction,
                                   GAELocalization constant, LogsPresenter logsPresenter, NotificationManager notificationManager) {
        this.view = view;
        this.createApplicationPresenter = createApplicationPresenter;
        this.eventBus = eventBus;
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.loginAction = loginAction;
        this.constant = constant;
        this.logsPresenter = logsPresenter;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /**
     * Initialize Backend tab presenter.
     *
     * @param project
     *         project that opened in current moment.
     */
    public void init(Project project) {
        this.project = project;
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateApplicationClicked() {
        createApplicationPresenter.deploy(project);
    }

    /** {@inheritDoc} */
    @Override
    public void onRollBackApplicationClicked() {
        final String vfsId = resourceProvider.getVfsInfo().getId();

        try {
            service.rollback(vfsId, project.getId(),
                             new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                 @Override
                                 protected void onSuccess(Object result) {
                                     Notification notification = new Notification(constant.rollbackUpdateSuccess(), INFO);
                                     notificationManager.showNotification(notification);
                                 }
                             });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onGetApplicationLogsClicked() {
        logsPresenter.showDialog(project);
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateIndexesClicked() {
        final String vfsId = resourceProvider.getVfsInfo().getId();

        try {
            service.updateIndexes(vfsId, project.getId(),
                                  new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                      @Override
                                      protected void onSuccess(Object result) {
                                          Notification notification = new Notification(constant.updateIndexesSuccessfully(), INFO);
                                          notificationManager.showNotification(notification);
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onVacuumIndexesClicked() {
        final String vfsId = resourceProvider.getVfsInfo().getId();

        try {
            service.vacuumIndexes(vfsId, project.getId(),
                                  new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                      @Override
                                      protected void onSuccess(Object result) {
                                          Notification notification = new Notification(constant.vacuumIndexesSuccessfully(), INFO);
                                          notificationManager.showNotification(notification);
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdatePageSpeedClicked() {
        final String vfsId = resourceProvider.getVfsInfo().getId();

        try {
            service.updatePagespeed(vfsId, project.getId(),
                                    new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                        @Override
                                        protected void onSuccess(Object result) {
                                            Notification notification = new Notification(constant.updatePageSpeedSuccessfully(), INFO);
                                            notificationManager.showNotification(notification);
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateQueuesClicked() {
        final String vfsId = resourceProvider.getVfsInfo().getId();

        try {
            service.updateQueues(vfsId, project.getId(),
                                 new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                     @Override
                                     protected void onSuccess(Object result) {
                                         Notification notification = new Notification(constant.updateQueuesSuccessfully(), INFO);
                                         notificationManager.showNotification(notification);
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateDoSClicked() {
        final String vfsId = resourceProvider.getVfsInfo().getId();

        try {
            service.updateDos(vfsId, project.getId(),
                              new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                  @Override
                                  protected void onSuccess(Object result) {
                                      Notification notification = new Notification(constant.updateDosSuccessfully(), INFO);
                                      notificationManager.showNotification(notification);
                                  }
                              });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
