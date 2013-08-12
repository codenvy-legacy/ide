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

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.InstanceListInfoUnmarshaller;
import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for controlling EC2 instances.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EC2ManagerPresenter implements EC2ManagerView.ActionDelegate {
    private EC2ManagerView          view;
    private ConsolePart             console;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;
    private EC2ClientService        service;
    private LoginPresenter          loginPresenter;

    /**
     * Create presenter.
     *
     * @param view
     * @param console
     * @param eventBus
     * @param constant
     * @param service
     * @param loginPresenter
     */
    @Inject
    protected EC2ManagerPresenter(EC2ManagerView view, ConsolePart console, EventBus eventBus, AWSLocalizationConstant constant,
                                  EC2ClientService service, LoginPresenter loginPresenter) {
        this.view = view;
        this.console = console;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.loginPresenter = loginPresenter;

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

        try {
            JsonArray<InstanceInfo> instanceList = JsonCollections.createArray();
            InstanceListInfoUnmarshaller unmarshaller = new InstanceListInfoUnmarshaller(instanceList);
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
                    console.print(exception.getMessage());
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            console.print(e.getMessage());
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
                                              console.print(constant.terminateInstanceSuccess(instanceInfo.getId()));
                                          }

                                          @Override
                                          protected void processFail(Throwable exception) {
                                              console.print(constant.terminateInstanceFailed(instanceInfo.getId()));
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
                    console.print(constant.rebootInstanceSuccess(instanceInfo.getId()));
                }

                @Override
                protected void processFail(Throwable exception) {
                    console.print(constant.rebootInstanceFailed(instanceInfo.getId()));
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
                    console.print(constant.startInstanceSuccess(instanceInfo.getId()));
                }

                @Override
                protected void processFail(Throwable exception) {
                    console.print(constant.startInstanceFailed(instanceInfo.getId()));
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
                                         console.print(constant.stopInstanceSuccess(instanceInfo.getId()));
                                     }

                                     @Override
                                     protected void processFail(Throwable exception) {
                                         console.print(constant.stopInstanceFailed(instanceInfo.getId()));
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
