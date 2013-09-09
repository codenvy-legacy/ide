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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.ec2.stop.StopInstanceEvent;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Presenter for {@link EC2ManagerView}. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2Manager.java Sep 21, 2012 10:13:36 AM azatsarynnyy $
 */
public class EC2Manager implements ViewClosedHandler, ShowEC2ManagerHandler {
    interface Display extends IsView {
        HasSelectionHandlers<InstanceInfo> getInstances();

        void setInstances(List<InstanceInfo> instanceList);

        HasSelectionHandlers<Entry<String, String>> getTags();

        void setTags(List<Entry<String, String>> tags);

        HasClickHandlers getTerminateButton();

        HasClickHandlers getRebootButton();

        HasClickHandlers getStopButton();

        HasClickHandlers getStartButton();

        HasClickHandlers getCloseButton();

        /**
         * Change the enable state of the all buttons.
         *
         * @param isEnable
         *         enabled or not
         */
        void setAllButtonsEnableState(boolean isEnable);
    }

    /** Display. */
    private Display display;

    /** AWS EC2 instance which is currently selected. */
    private InstanceInfo selectedInstance;

    public EC2Manager() {
        IDE.getInstance().addControl(new EC2ManagerControl());

        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ShowEC2ManagerEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getInstances().addSelectionHandler(new SelectionHandler<InstanceInfo>() {

            @Override
            public void onSelection(SelectionEvent<InstanceInfo> event) {
                selectedInstance = event.getSelectedItem();
                if (selectedInstance != null) {
                    Set<Entry<String, String>> entrySet = selectedInstance.getTags().entrySet();
                    ArrayList<Entry<String, String>> arrayList = new ArrayList<Entry<String, String>>(entrySet);
                    display.setTags(arrayList);
                }
            }
        });

        display.getTerminateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedInstance != null) {
                    askForTerminateInstance(selectedInstance.getId());
                }
            }
        });

        display.getRebootButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedInstance != null) {
                    askForRebootInstance(selectedInstance.getId());
                }
            }
        });

        display.getStopButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedInstance != null) {
                    IDE.fireEvent(new StopInstanceEvent(selectedInstance.getId()));
                }
            }
        });

        display.getStartButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedInstance != null) {
                    askForStartInstance(selectedInstance.getId());
                }
            }
        });

        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.ShowEC2ManagerHandler#onShowEC2Manager(org.exoplatform.ide.extension.aws.client
     * .ec2.ShowEC2ManagerEvent) */
    @Override
    public void onShowEC2Manager(ShowEC2ManagerEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }
        display.setAllButtonsEnableState(false);
        getInstances();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Get instances that authorized user owns. */
    private void getInstances() {
        try {
            List<InstanceInfo> instanceList = new ArrayList<InstanceInfo>();
            EC2ClientService.getInstance().getInstances(
                    new AwsAsyncRequestCallback<List<InstanceInfo>>(new InstanceListUnmarshaller(instanceList),
                                                                    new LoggedInHandler() {

                                                                        @Override
                                                                        public void onLoggedIn() {
                                                                            getInstances();
                                                                        }
                                                                    }, null) {

                        @Override
                        protected void onSuccess(List<InstanceInfo> result) {
                            display.setInstances(result);
                            if (result.size() > 0) {
                                display.setAllButtonsEnableState(true);
                            }
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Ask user for terminate instance and terminate it if user select 'Yes'.
     *
     * @param instanceId
     *         instance identifier
     */
    private void askForTerminateInstance(final String instanceId) {
        Dialogs.getInstance().ask(AWSExtension.LOCALIZATION_CONSTANT.terminateEC2InstanceViewTitle(),
                                  AWSExtension.LOCALIZATION_CONSTANT.terminateEC2InstanceQuestion(instanceId),
                                  new BooleanValueReceivedHandler() {
                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value == true) {
                                              terminateInstance(instanceId);
                                          }
                                      }
                                  });
    }

    /**
     * Ask user for reboot instance and reboot it if user select 'Yes'.
     *
     * @param instanceId
     *         instance identifier
     */
    private void askForRebootInstance(final String instanceId) {
        Dialogs.getInstance().ask(AWSExtension.LOCALIZATION_CONSTANT.rebootEC2InstanceViewTitle(),
                                  AWSExtension.LOCALIZATION_CONSTANT.rebootEC2InstanceQuestion(instanceId),
                                  new BooleanValueReceivedHandler() {
                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value == true) {
                                              rebootInstance(instanceId);
                                          }
                                      }
                                  });
    }

    /**
     * Ask user for start specified instance and start it if user select 'Yes'.
     *
     * @param instanceId
     *         instance identifier
     */
    private void askForStartInstance(final String instanceId) {
        Dialogs.getInstance().ask(AWSExtension.LOCALIZATION_CONSTANT.startEC2InstanceViewTitle(),
                                  AWSExtension.LOCALIZATION_CONSTANT.startEC2InstanceQuestion(instanceId),
                                  new BooleanValueReceivedHandler() {
                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value == true) {
                                              startInstance(instanceId);
                                          }
                                      }
                                  });
    }

    /**
     * Terminate specified instance.
     *
     * @param instanceId
     *         instance identifier
     */
    private void terminateInstance(final String instanceId) {
        try {
            EC2ClientService.getInstance().terminateInstance(instanceId, new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.terminateEC2InstanceViewTitle(),
                                                   AWSExtension.LOCALIZATION_CONSTANT.terminateInstanceSuccess(instanceId));
                    IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.terminateInstanceSuccess(instanceId),
                                                  Type.INFO));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message = AWSExtension.LOCALIZATION_CONSTANT.terminateInstanceFailed(instanceId);
                    if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                        message += "<br>" + ((ServerException)exception).getMessage();
                    }
                    Dialogs.getInstance().showError(message);
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Reboot specified instance.
     *
     * @param instanceId
     *         instance identifier
     */
    private void rebootInstance(final String instanceId) {
        try {
            EC2ClientService.getInstance().rebootInstance(instanceId, new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.rebootEC2InstanceViewTitle(),
                                                   AWSExtension.LOCALIZATION_CONSTANT.rebootInstanceSuccess(instanceId));
                    IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.rebootInstanceSuccess(instanceId),
                                                  Type.INFO));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message = AWSExtension.LOCALIZATION_CONSTANT.rebootInstanceFailed(instanceId);
                    if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                        message += "<br>" + ((ServerException)exception).getMessage();
                    }
                    Dialogs.getInstance().showError(message);
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Start specified instance.
     *
     * @param instanceId
     *         instance identifier
     */
    private void startInstance(final String instanceId) {
        try {
            EC2ClientService.getInstance().startInstance(instanceId, new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.startEC2InstanceViewTitle(),
                                                   AWSExtension.LOCALIZATION_CONSTANT.startInstanceSuccess(instanceId));
                    IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.startInstanceSuccess(instanceId),
                                                  Type.INFO));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message = AWSExtension.LOCALIZATION_CONSTANT.startInstanceFailed(instanceId);
                    if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                        message += "<br>" + ((ServerException)exception).getMessage();
                    }
                    Dialogs.getInstance().showError(message);
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

}
