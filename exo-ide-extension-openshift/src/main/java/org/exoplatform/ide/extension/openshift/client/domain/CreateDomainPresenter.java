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
package org.exoplatform.ide.extension.openshift.client.domain;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.client.user.ShowApplicationListEvent;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 7, 2011 3:49:41 PM anya $
 */
public class CreateDomainPresenter extends GitPresenter implements ViewClosedHandler, CreateDomainHandler, LoggedInHandler {
    interface Display extends IsView {
        /**
         * Get create button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCreateButton();

        /**
         * Get cancel button's click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get domain name field.
         *
         * @return {@link HasValue} click handler
         */
        HasValue<String> getDomainNameField();

        /**
         * Change the enable state of the create button.
         *
         * @param enable
         */
        void enableCreateButton(boolean enable);

        /** Give focus to domain name field. */
        void focusInDomainNameField();
    }

    private Display display;

    private boolean fromUserInfo;

    /**
     *
     */
    public CreateDomainPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(CreateDomainEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCreateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getUserInfo();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getDomainNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                boolean isNotEmpty = (event.getValue() != null && event.getValue().trim().length() > 0);
                display.enableCreateButton(isNotEmpty);
            }
        });
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onCreateDomain(CreateDomainEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
            display.enableCreateButton(false);
            display.focusInDomainNameField();
            fromUserInfo = event.isFromUserInfo();
        }
    }

    protected void getUserInfo() {
        try {
            AutoBean<RHUserInfo> rhUserInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.rhUserInfo();
            AutoBeanUnmarshaller<RHUserInfo> unmarshaller = new AutoBeanUnmarshaller<RHUserInfo>(rhUserInfo);
            OpenShiftClientService.getInstance().getUserInfo(true, new AsyncRequestCallback<RHUserInfo>(unmarshaller) {

                @Override
                protected void onSuccess(RHUserInfo result) {
                    //if user have namespace
                    if (result.getNamespace() != null && !result.getNamespace().isEmpty()) {
                        if (result.getApps() != null && result.getApps().size() > 0) {
                            askUserForDeleteHisApplications();
                        } else {
                            deleteAllApplicationsAndNamespace();
                        }
                    } else {
                        createDomain();
                    }
                }

                /**
                 * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                 */
                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.OK == serverException.getHTTPStatus()
                            && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED))) {
                            addLoggedInHandler();
                            IDE.fireEvent(new LoginEvent());
                            return;
                        }
                    }
                    IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                 .getUserInfoFail()));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT.getUserInfoFail()));
        }
    }

    private void askUserForDeleteHisApplications() {
        Dialogs.getInstance().ask(OpenShiftExtension.LOCALIZATION_CONSTANT.changeNamespaceTitle(),
                                  OpenShiftExtension.LOCALIZATION_CONSTANT.changeNamespacePrompt(), new BooleanValueReceivedHandler() {


            @Override
            public void booleanValueReceived(Boolean value) {
                if (value != null && value) {
                    deleteAllApplicationsAndNamespace();
                }
            }
        });
    }

    private void deleteAllApplicationsAndNamespace() {
        final String projectId = getSelectedProject() != null ? getSelectedProject().getId() : null;

        try {
            OpenShiftClientService.getInstance().destroyAllApplications(true, vfs.getId(), projectId,
                                                                        new AsyncRequestCallback<Void>() {


                                                                            @Override
                                                                            protected void onSuccess(Void result) {
                                                                                IDE.fireEvent(new RefreshBrowserEvent());
                                                                                createDomain();
                                                                            }

                                                                            @Override
                                                                            protected void onFailure(Throwable exception) {
                                                                                Dialogs.getInstance().showError(
                                                                                        OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                                                          .removingApplicationsFailed());
                                                                            }
                                                                        });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.removingApplicationsFailed());
        }
    }

    protected void createDomain() {
        final String domainName =
                (display.getDomainNameField().getValue() != null) ? display.getDomainNameField().getValue().trim() : display
                        .getDomainNameField().getValue();
        if (domainName == null || domainName.length() == 0) {
            return;
        }

        try {
            OpenShiftClientService.getInstance().createDomain(domainName, false, new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    IDE.fireEvent(new OutputEvent(OpenShiftExtension.LOCALIZATION_CONSTANT.createDomainSuccess(domainName),
                                                  Type.INFO));
                    IDE.getInstance().closeView(display.asView().getId());
                    if (fromUserInfo) {
                        IDE.fireEvent(new ShowApplicationListEvent());
                    }
                }

                /**
                 * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                 */
                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.OK == serverException.getHTTPStatus()
                            && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED))) {
                            addLoggedInHandler();
                            IDE.fireEvent(new LoginEvent());
                            return;
                        }
                    }
                    Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT
                                                                      .createDomainFail(domainName));
                }
            });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT
                                                              .createDomainFail(domainName));
        }
    }

    /** Register {@link LoggedInHandler} handler. */
    protected void addLoggedInHandler() {
        IDE.addHandler(LoggedInEvent.TYPE, this);
    }

    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            createDomain();
        }
    }
}
