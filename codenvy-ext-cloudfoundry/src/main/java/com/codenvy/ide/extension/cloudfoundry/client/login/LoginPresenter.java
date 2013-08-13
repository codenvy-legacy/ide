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
package com.codenvy.ide.extension.cloudfoundry.client.login;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.SystemInfoUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.dto.client.DtoClientImpls;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for logging on CloudFoundry.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 25, 2011 3:56:55 PM anya $
 */
@Singleton
public class LoginPresenter implements LoginView.ActionDelegate {
    private LoginView                           view;
    private ConsolePart                         console;
    /** The last server, that user logged in. */
    private String                              server;
    private LoggedInHandler                     loggedIn;
    private LoginCanceledHandler                loginCanceled;
    private EventBus                            eventBus;
    private CloudFoundryLocalizationConstant    constant;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param constant
     * @param service
     */
    @Inject
    protected LoginPresenter(LoginView view, EventBus eventBus, ConsolePart console, CloudFoundryLocalizationConstant constant,
                             CloudFoundryClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onLogInClicked() {
        doLogin();
    }

    /** Perform log in CloudFoundry. */
    protected void doLogin() {
        final String enteredServer = view.getServer();
        final String email = view.getEmail();
        final String password = view.getPassword();

        try {
            service.login(enteredServer, email, password, paasProvider, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    server = enteredServer;
                    console.print(paasProvider == CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY ? constant.loginSuccess() : constant
                            .tier3WebFabricLoginSuccess());
                    if (loggedIn != null) {
                        loggedIn.onLoggedIn();
                    }

                    view.close();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() &&
                            serverException.getMessage() != null
                            && serverException.getMessage().contains("Can't access target.")) {
                            view.setError(constant.loginViewErrorUnknownTarget());
                            return;
                        } else if (HTTPStatus.OK != serverException.getHTTPStatus() &&
                                   serverException.getMessage() != null
                                   && serverException.getMessage().contains("Operation not permitted")) {
                            view.setError(constant.loginViewErrorInvalidUserOrPassword());
                            return;
                        }
                        // otherwise will be called method from superclass.
                    }

                    view.setError("");
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        if (loginCanceled != null) {
            loginCanceled.onLoginCanceled();
        }

        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        updateComponent();
    }

    /** Updates component on the view. */
    private void updateComponent() {
        view.enableLoginButton(isFieldsFullFilled());
    }

    /**
     * Check whether necessary fields are fullfilled.
     *
     * @return if <code>true</code> all necessary fields are fullfilled
     */
    private boolean isFieldsFullFilled() {
        return (view.getEmail() != null && !view.getEmail().isEmpty() && view.getPassword() != null && !view
                .getPassword().isEmpty());
    }

    /** Shows dialog. */
    public void showDialog(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled, String loginUrl,
                           CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        if (loginUrl != null) {
            server = loginUrl;
            if (!server.startsWith("http")) {
                server = "http://" + server;
            }
        }

        showDialog(paasProvider);
    }

    /** Shows dialog. */
    public void showDialog(CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        this.paasProvider = paasProvider;

        fillViewFields();

        view.showDialog();
    }

    /** Fills fields on the view. */
    private void fillViewFields() {
        view.enableLoginButton(false);
        view.focusInEmailField();
        view.setPassword("");
        view.setError("");

        getSystemInformation();
    }

    /** Get Cloud Foundry system information to fill the login field, if user is logged in. */
    protected void getSystemInformation() {
        DtoClientImpls.SystemInfoImpl systemInfo = DtoClientImpls.SystemInfoImpl.make();
        SystemInfoUnmarshaller unmarshaller = new SystemInfoUnmarshaller(systemInfo);
        try {
            service.getSystemInfo(server, paasProvider, new AsyncRequestCallback<SystemInfo>(unmarshaller) {
                @Override
                protected void onSuccess(SystemInfo result) {
                    view.setEmail(result.getUser());
                    getServers();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof UnmarshallerException) {
                        Window.alert(exception.getMessage());
                    } else {
                        getServers();
                    }
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Get the list of server and put them to field. */
    private void getServers() {
        TargetsUnmarshaller unmarshaller = new TargetsUnmarshaller(JsonCollections.<String>createArray());
        try {
            service.getTargets(paasProvider, new AsyncRequestCallback<JsonArray<String>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonArray<String> result) {
                    if (result.isEmpty()) {
                        if (paasProvider == CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY) {
                            JsonArray<String> servers = JsonCollections.createArray(CloudFoundryExtension.DEFAULT_CF_SERVER);
                            view.setServerValues(servers);
                        }
                        if ((server == null || server.isEmpty()) && paasProvider == CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY) {
                            view.setServer(CloudFoundryExtension.DEFAULT_CF_SERVER);
                        } else {
                            view.setServer(server);
                        }
                    } else {
                        view.setServerValues(result);
                        if (server == null || server.isEmpty()) {
                            view.setServer(result.get(0));
                        } else {
                            view.setServer(server);
                        }
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}