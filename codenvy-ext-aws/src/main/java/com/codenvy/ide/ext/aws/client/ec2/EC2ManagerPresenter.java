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
package com.codenvy.ide.ext.aws.client.ec2;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.InstanceListInfoUnmarshaller;
import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for controlling EC2 instances.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EC2ManagerPresenter implements EC2ManagerView.ActionDelegate {
    private EC2ManagerView          view;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;
    private EC2ClientService        service;
    private LoginPresenter          loginPresenter;
    private NotificationManager     notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param constant
     * @param service
     * @param loginPresenter
     * @param notificationManager
     */
    @Inject
    protected EC2ManagerPresenter(EC2ManagerView view, EventBus eventBus, AWSLocalizationConstant constant,
                                  EC2ClientService service, LoginPresenter loginPresenter, NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /** Show main dialog window. */
    public void showDialog() {
        if (!view.isShown()) {
            view.showDialog();

            getInstanceInfo();
        }
    }

    /** Get list of all instances. */
    private void getInstanceInfo() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getInstanceInfo();
            }
        };
        InstanceListInfoUnmarshaller unmarshaller = new InstanceListInfoUnmarshaller();

        try {
            service.getInstances(new AwsAsyncRequestCallback<JsonArray<InstanceInfo>>(unmarshaller, loggedInHandler, null, loginPresenter) {
                @Override
                protected void onSuccess(JsonArray<InstanceInfo> result) {
                    view.setEC2Instances(result);
                    if (result.size() > 0) {
                        view.setAllButtonsEnableState(true);
                    }
                }

                @Override
                protected void processFail(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onTerminateClicked(final InstanceInfo instanceInfo) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onTerminateClicked(instanceInfo);
            }
        };

        boolean terminate = Window.confirm(constant.terminateEC2InstanceQuestion(instanceInfo.getId()));

        if (!terminate) {
            return;
        }

        try {
            service.terminateInstance(instanceInfo.getId(),
                                      new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void onSuccess(Object result) {
                                              Notification notification = new Notification(constant.terminateInstanceSuccess(
                                                      instanceInfo.getId()), INFO);
                                              notificationManager.showNotification(notification);
                                          }

                                          @Override
                                          protected void processFail(Throwable exception) {
                                              Notification notification = new Notification(constant.terminateInstanceFailed(
                                                      instanceInfo.getId()), ERROR);
                                              notificationManager.showNotification(notification);
                                              eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRebootClicked(final InstanceInfo instanceInfo) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onRebootClicked(instanceInfo);
            }
        };

        boolean reboot = Window.confirm(constant.rebootEC2InstanceQuestion(instanceInfo.getId()));

        if (!reboot) {
            return;
        }

        try {
            service.rebootInstance(instanceInfo.getId(), new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                @Override
                protected void onSuccess(Object result) {
                    Notification notification = new Notification(constant.rebootInstanceSuccess(instanceInfo.getId()), INFO);
                    notificationManager.showNotification(notification);
                }

                @Override
                protected void processFail(Throwable exception) {
                    Notification notification = new Notification(constant.rebootInstanceFailed(instanceInfo.getId()), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStartClicked(final InstanceInfo instanceInfo) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onStartClicked(instanceInfo);
            }
        };

        boolean start = Window.confirm(constant.startEC2InstanceQuestion(instanceInfo.getId()));

        if (!start) {
            return;
        }

        try {
            service.startInstance(instanceInfo.getId(), new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                @Override
                protected void onSuccess(Object result) {
                    Notification notification = new Notification(constant.startInstanceSuccess(instanceInfo.getId()), INFO);
                    notificationManager.showNotification(notification);
                }

                @Override
                protected void processFail(Throwable exception) {
                    Notification notification = new Notification(constant.startInstanceFailed(instanceInfo.getId()), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStopClicked(final InstanceInfo instanceInfo) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onStopClicked(instanceInfo);
            }
        };

        boolean stop = Window.confirm(constant.stopEC2InstanceQuestion(instanceInfo.getId()));

        if (!stop) {
            return;
        }

        boolean force = Window.confirm(constant.stopEC2Force());

        try {
            service.stopInstance(instanceInfo.getId(), force,
                                 new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                                     @Override
                                     protected void onSuccess(Object result) {
                                         Notification notification =
                                                 new Notification(constant.stopInstanceSuccess(instanceInfo.getId()), INFO);
                                         notificationManager.showNotification(notification);
                                     }

                                     @Override
                                     protected void processFail(Throwable exception) {
                                         Notification notification =
                                                 new Notification(constant.stopInstanceFailed(instanceInfo.getId()), ERROR);
                                         notificationManager.showNotification(notification);
                                         eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}
