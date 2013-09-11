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
package org.exoplatform.ide.extension.cloudfoundry.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;

import java.util.ArrayList;
import java.util.List;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY;

/**
 * Presenter for login view. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 25, 2011 3:56:55 PM anya $
 */
public class LoginPresenter implements LoginHandler, ViewClosedHandler {
    interface Display extends IsView {
        /**
         * Get login button click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getLoginButton();

        /**
         * Get cancel button click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get email field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getEmailField();

        /**
         * Get password field.
         *
         * @return {@link HasValue}
         */
        TextFieldItem getPasswordField();

        /**
         * Get target select item.
         *
         * @return
         */
        HasValue<String> getTargetSelectField();

        /**
         * Get the label, where error message will be displayed.
         *
         * @return
         */
        HasValue<String> getErrorLabelField();

        /**
         * Change the enable state of the login button.
         *
         * @param enabled
         */
        void enableLoginButton(boolean enabled);

        /** Give focus to login field. */
        void focusInEmailField();

        /**
         * Set the list of available targets.
         *
         * @param targets
         */
        void setTargetValues(String[] targets);
    }

    private static final CloudFoundryLocalizationConstant lb = CloudFoundryExtension.LOCALIZATION_CONSTANT;

    private Display display;

    private LoggedInHandler loggedIn;

    private LoginCanceledHandler loginCanceled;

    /** The last server, that user logged in. */
    private String server;

    private PAAS_PROVIDER paasProvider;

    /**
     * Creates new instance {@link LoginPresenter} with the specified deploy <code>target</code>.
     * 
     * @param target deploy target
     */
    public LoginPresenter() {
        IDE.addHandler(LoginEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /**
     * Bind display with presenter.
     *
     * @param d
     */
    public void bindDisplay(Display d) {
        this.display = d;

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (loginCanceled != null)
                    loginCanceled.onLoginCanceled();

                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getLoginButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doLogin();
            }
        });

        display.getEmailField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableLoginButton(isFieldsFullFilled());
            }
        });

        display.getPasswordField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableLoginButton(isFieldsFullFilled());
            }
        });

        display.getPasswordField().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 && isFieldsFullFilled()) {
                    doLogin();
                }
            }
        });

        display.getTargetSelectField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue() != null) {
                    server = event.getValue();
                }
            }
        });
    }

    /**
     * Check whether necessary fields are fullfilled.
     *
     * @return if <code>true</code> all necessary fields are fullfilled
     */
    private boolean isFieldsFullFilled() {
        return (display.getEmailField().getValue() != null && !display.getEmailField().getValue().isEmpty()
                && display.getPasswordField().getValue() != null && !display.getPasswordField().getValue().isEmpty());
    }

    /**
     * @see org.exoplatform.ide.extension.cloudfoundry.client.login.LoginHandler#onLogin(org.exoplatform.ide.extension.cloudfoundry.client.login.LoginEvent)
     */
    @Override
    public void onLogin(LoginEvent event) {
        paasProvider = event.getPaasProvider();
        loggedIn = event.getLoggedIn();
        loginCanceled = event.getLoginCanceled();
        if (event.getLoginUrl() != null) {
            server = event.getLoginUrl();
            if (server != null && !server.startsWith("http"))
                server = "http://" + server;
        }
        if (display == null) {
            Display display = GWT.create(Display.class);
            bindDisplay(display);
            IDE.getInstance().openView(display.asView());
            display.enableLoginButton(false);
            display.focusInEmailField();
            getSystemInformation();
        }

    }

    /** Get CloudFoundry system information to fill the login field, if user is logged in. */
    protected void getSystemInformation() {
        try {
            AutoBean<SystemInfo> systemInfo = CloudFoundryExtension.AUTO_BEAN_FACTORY.systemInfo();
            AutoBeanUnmarshaller<SystemInfo> unmarshaller = new AutoBeanUnmarshaller<SystemInfo>(systemInfo);
            CloudFoundryClientService.getInstance().getSystemInfo(server, paasProvider,
                  new AsyncRequestCallback<SystemInfo>(unmarshaller) {
                      @Override
                      protected void onSuccess(SystemInfo result) {
                          display.getEmailField().setValue(result.getUser());
                          getTargets();
                      }

                      /**
                       * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                       */
                      @Override
                      protected void onFailure(Throwable exception) {
                          if (exception instanceof UnmarshallerException) {
                              Dialogs.getInstance().showError(exception.getMessage());
                          } else {
                              getTargets();
                          }
                      }
                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getTargets() {
        try {
            CloudFoundryClientService.getInstance().getTargets(paasProvider,
                    new AsyncRequestCallback<List<String>>(new TargetsUnmarshaller(new ArrayList<String>())) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            if (result.isEmpty()) {
                                if (paasProvider == CLOUD_FOUNDRY) {
                                    display.setTargetValues(new String[]{CloudFoundryExtension.DEFAULT_CF_SERVER});
                                }
                                if ((server == null || server.isEmpty()) && paasProvider == CLOUD_FOUNDRY) {
                                    display.getTargetSelectField().setValue(CloudFoundryExtension.DEFAULT_CF_SERVER);
                                } else
                                    display.getTargetSelectField().setValue(server);
                            } else {
                                String[] targets = new String[result.size()];
                                targets = result.toArray(targets);
                                display.setTargetValues(targets);
                                if (server == null || server.isEmpty()) {
                                    display.getTargetSelectField().setValue(result.get(0));
                                } else
                                    display.getTargetSelectField().setValue(server);
                            }
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Perform log in. */
    protected void doLogin() {
        final String enteredServer = display.getTargetSelectField().getValue();
        final String email = display.getEmailField().getValue();
        final String password = display.getPasswordField().getValue();

        try {
            CloudFoundryClientService.getInstance().login(enteredServer, email, password, paasProvider,
                  new AsyncRequestCallback<String>() {

                      /**
                       * @see org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback#onSuccess(java.lang.Object)
                       */
                      @Override
                      protected void onSuccess(String result) {
                          server = enteredServer;
                          IDE.fireEvent(new OutputEvent(paasProvider == CLOUD_FOUNDRY ? lb.loginSuccess() : lb.tier3WebFabricLoginSuccess(), Type.INFO));
                          if (loggedIn != null) {
                              loggedIn.onLoggedIn(server);
                          }
                          IDE.getInstance().closeView(display.asView().getId());
                      }

                      /**
                       * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                       */
                      @Override
                      protected void onFailure(Throwable exception) {
                          if (exception instanceof ServerException) {
                              ServerException serverException = (ServerException)exception;
                              if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                                  && serverException.getMessage() != null
                                  && serverException.getMessage().contains("Can't access target.")) {
                                  display.getErrorLabelField().setValue(lb.loginViewErrorUnknownTarget());
                                  return;
                              } else if (HTTPStatus.OK != serverException.getHTTPStatus() &&
                                         serverException.getMessage() != null
                                         && serverException.getMessage().contains("Operation not permitted")) {
                                  display.getErrorLabelField().setValue(lb.loginViewErrorInvalidUserOrPassword());
                                  return;
                              }
                              // otherwise will be called method from superclass.
                          }
                          IDE.fireEvent(new ExceptionThrownEvent(exception));
                      }
                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            loggedIn = null;
            loginCanceled = null;
            server = "";
        }
    }
}
