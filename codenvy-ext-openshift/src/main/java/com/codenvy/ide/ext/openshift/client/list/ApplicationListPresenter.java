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
package com.codenvy.ide.ext.openshift.client.list;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.cartridge.CreateCartridgePresenter;
import com.codenvy.ide.ext.openshift.client.domain.CreateDomainPresenter;
import com.codenvy.ide.ext.openshift.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.ext.openshift.client.info.ApplicationProperty;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.UserInfoUnmarshaller;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Application list window.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationListPresenter implements ApplicationListView.ActionDelegate {
    private ApplicationListView           view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private ResourceProvider              resourceProvider;
    private CreateDomainPresenter         createDomainPresenter;
    private CreateCartridgePresenter      createCartridgePresenter;
    private ApplicationInfoPresenter      applicationInfoPresenter;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param service
     * @param constant
     * @param loginPresenter
     * @param resourceProvider
     * @param createDomainPresenter
     * @param createCartridgePresenter
     * @param applicationInfoPresenter
     */
    @Inject
    protected ApplicationListPresenter(ApplicationListView view, EventBus eventBus, ConsolePart console, OpenShiftClientServiceImpl service,
                                       OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter,
                                       ResourceProvider resourceProvider, CreateDomainPresenter createDomainPresenter,
                                       CreateCartridgePresenter createCartridgePresenter,
                                       ApplicationInfoPresenter applicationInfoPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;
        this.createDomainPresenter = createDomainPresenter;
        this.createCartridgePresenter = createCartridgePresenter;
        this.applicationInfoPresenter = applicationInfoPresenter;

        this.view.setDelegate(this);
    }

    /** Show application list window. */
    public void showDialog() {
        if (!view.isShown()) {
            getApplications();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onChangeDomainNameClicked() {
        createDomainPresenter.showDialog(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    getApplications();
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onChangeAccountClicked() {
        loginPresenter.showDialog(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    getApplications();
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateCartridgeClicked() {
        createCartridgePresenter.showDialog(view.getSelectedApplication(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    getApplications();
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onApplicationDeleteClicked(final AppInfo application) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onApplicationDeleteClicked(application);
            }
        };

        final String projectId = resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;
        final String vfsId = resourceProvider.getVfsId();

        if (projectId != null && !Window.confirm(constant.deleteApplicationPrompt(application.getName()))) {
            return;
        }

        try {
            service.destroyApplication(application.getName(), vfsId, projectId,
                                       new OpenShiftAsyncRequestCallback<String>(null, loggedInHandler, null, eventBus, console, constant,
                                                                                 loginPresenter) {
                                           @Override
                                           protected void onSuccess(String result) {
                                               String msg = constant.deleteApplicationSuccessfullyDeleted(application.getName());
                                               console.print(msg);
                                               getApplications();
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCartridgeStartClicked(final OpenShiftEmbeddableCartridge cartridge) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onCartridgeStartClicked(cartridge);
            }
        };

        try {
            service.startCartridge(view.getSelectedApplication().getName(), cartridge.getName(),
                                   new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                           loginPresenter) {
                                       @Override
                                       protected void onSuccess(Void result) {
                                           String msg = constant.cartridgeSuccessfullyStarted(cartridge.getName());
                                           console.print(msg);
                                       }
                                   });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCartridgeStopClicked(final OpenShiftEmbeddableCartridge cartridge) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onCartridgeStopClicked(cartridge);
            }
        };
        try {
            service.stopCartridge(view.getSelectedApplication().getName(), cartridge.getName(),
                                  new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                          loginPresenter) {
                                      @Override
                                      protected void onSuccess(Void result) {
                                          String msg = constant.cartridgeSuccessfullyStopped(cartridge.getName());
                                          console.print(msg);
                                      }
                                  });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCartridgeRestartClicked(final OpenShiftEmbeddableCartridge cartridge) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onCartridgeRestartClicked(cartridge);
            }
        };

        try {
            service.restartCartridge(view.getSelectedApplication().getName(), cartridge.getName(),
                                     new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                             loginPresenter) {
                                         @Override
                                         protected void onSuccess(Void result) {
                                             String msg = constant.cartridgeSuccessfullyRestarted(cartridge.getName());
                                             console.print(msg);
                                         }
                                     });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCartridgeReloadClicked(final OpenShiftEmbeddableCartridge cartridge) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onCartridgeReloadClicked(cartridge);
            }
        };

        try {
            service.reloadCartridge(view.getSelectedApplication().getName(), cartridge.getName(),
                                    new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                            loginPresenter) {
                                        @Override
                                        protected void onSuccess(Void result) {
                                            String msg = constant.cartridgeSuccessfullyReloaded(cartridge.getName());
                                            console.print(msg);
                                        }
                                    });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCartridgeDeleteClicked(final OpenShiftEmbeddableCartridge cartridge) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onCartridgeDeleteClicked(cartridge);
            }
        };

        try {
            service.deleteCartridge(view.getSelectedApplication().getName(), cartridge.getName(),
                                    new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                            loginPresenter) {
                                        @Override
                                        protected void onSuccess(Void result) {
                                            String msg = constant.cartridgeSuccessfullyDeleted(cartridge.getName());
                                            console.print(msg);
                                            getApplications();
                                        }
                                    });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get application list and fetch cartridges and properties from each app. */
    private void getApplications() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplications();
            }
        };
        UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller();

        try {
            service.getUserInfo(true,
                                new OpenShiftAsyncRequestCallback<RHUserInfo>(unmarshaller, loggedInHandler, null, eventBus, console,
                                                                              constant, loginPresenter) {
                                    @Override
                                    protected void onSuccess(RHUserInfo result) {
                                        view.showDialog();

                                        view.setApplicationInfo(JsonCollections.<ApplicationProperty>createArray());
                                        view.setCartridges(JsonCollections.<OpenShiftEmbeddableCartridge>createArray());

                                        view.setUserLogin(result.getRhlogin());
                                        view.setUserDomain(result.getNamespace());
                                        view.setApplications(result.getApps());

                                        if (result.getApps().size() > 0) {
                                            AppInfo app = result.getApps().get(0);
                                            JsonArray<ApplicationProperty> properties =
                                                    applicationInfoPresenter.getApplicationProperties(app);

                                            view.setApplicationInfo(properties);
                                            view.setCartridges(app.getEmbeddedCartridges());
                                        }
                                    }
                                });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
