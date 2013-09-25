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

package com.codenvy.ide.ext.gae.client.project.backend;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.marshaller.BackendsUnmarshaller;
import com.codenvy.ide.ext.gae.shared.Backend;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter that to allow user to control backends state.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class BackendTabPanePresenter implements Presenter, BackendTabPaneView.ActionDelegate {
    private BackendTabPaneView  view;
    private GAEClientService    service;
    private EventBus            eventBus;
    private ResourceProvider    resourceProvider;
    private GAELocalization     constant;
    private LoginAction         loginAction;
    private NotificationManager notificationManager;
    private Project             project;

    /** Constructor for backends presenter. */
    @Inject
    public BackendTabPanePresenter(BackendTabPaneView view, GAEClientService service, EventBus eventBus, ResourceProvider resourceProvider,
                                   GAELocalization constant, LoginAction loginAction, NotificationManager notificationManager) {
        this.view = view;
        this.service = service;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.loginAction = loginAction;
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

        final String vfsId = resourceProvider.getVfsId();
        BackendsUnmarshaller unmarshaller = new BackendsUnmarshaller();

        try {
            service.listBackends(vfsId, project.getId(),
                                 new GAEAsyncRequestCallback<JsonArray<Backend>>(unmarshaller, eventBus, constant, loginAction,
                                                                                 notificationManager) {
                                     @Override
                                     protected void onSuccess(JsonArray<Backend> result) {
                                         view.setBackendsList(result);
                                         view.setEnableUpdateButtons(false);
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigureBackendClicked() {
        final String vfsId = resourceProvider.getVfsId();
        final Backend backend = view.getSelectedBackend();

        if (backend == null) {
            Window.alert("You should select backend to complete this operation.");
            return;
        }

        try {
            service.configureBackend(vfsId, project.getId(), backend.getName(),
                                     new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                         @Override
                                         protected void onSuccess(Object result) {
                                             Notification notification =
                                                     new Notification(constant.configureBackendSuccessfully(backend.getName()), INFO);
                                             notificationManager.showNotification(notification);
                                             init(project);
                                         }
                                     });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteBackendClicked() {
        final String vfsId = resourceProvider.getVfsId();
        final Backend backend = view.getSelectedBackend();

        if (backend == null) {
            Window.alert("You should select backend to complete this operation.");
            return;
        }

        try {
            service.deleteBackend(vfsId, project.getId(), backend.getName(),
                                  new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                      @Override
                                      protected void onSuccess(Object result) {
                                          Notification notification =
                                                  new Notification(constant.deleteBackendSuccessfully(backend.getName()), INFO);
                                          notificationManager.showNotification(notification);
                                          init(project);
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateBackendClicked() {
        final String vfsId = resourceProvider.getVfsId();
        final Backend backend = view.getSelectedBackend();

        if (backend == null) {
            Window.alert("You should select backend to complete this operation.");
            return;
        }

        try {
            service.updateBackend(vfsId, project.getId(), backend.getName(),
                                  new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                      @Override
                                      protected void onSuccess(Object result) {
                                          Notification notification =
                                                  new Notification(constant.updateBackendSuccessfully(backend.getName()), INFO);
                                          notificationManager.showNotification(notification);
                                          init(project);
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRollBackBackendClicked() {
        final String vfsId = resourceProvider.getVfsId();
        final Backend backend = view.getSelectedBackend();

        if (backend == null) {
            Window.alert("You should select backend to complete this operation.");
            return;
        }

        try {
            service.rollbackBackend(vfsId, project.getId(), backend.getName(),
                                    new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                        @Override
                                        protected void onSuccess(Object result) {
                                            Notification notification =
                                                    new Notification(constant.rollbackBackendSuccessfully(backend.getName()), INFO);
                                            notificationManager.showNotification(notification);
                                            init(project);
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateAllBackendsClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.updateAllBackends(vfsId, project.getId(),
                                      new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                          @Override
                                          protected void onSuccess(Object result) {
                                              Notification notification =
                                                      new Notification(constant.updateAllBackendsSuccessfully(), INFO);
                                              notificationManager.showNotification(notification);
                                              init(project);
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRollBackAllBackendsClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.rollbackAllBackends(vfsId, project.getId(),
                                        new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                            @Override
                                            protected void onSuccess(Object result) {
                                                Notification notification =
                                                        new Notification(constant.rollbackAllBackendsSuccessfully(), INFO);
                                                notificationManager.showNotification(notification);
                                                init(project);
                                            }
                                        });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateBackendState(String backendName, Backend.State backendState) {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.setBackendState(vfsId, project.getId(), backendName, backendState.toString(),
                                    new GAEAsyncRequestCallback<Object>(null, eventBus, constant, loginAction, notificationManager) {
                                        @Override
                                        protected void onSuccess(Object result) {
                                            init(project);
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
