/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.openshift.client.list;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftAutoBeanFactory;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.cartridge.CreateCartridgePresenter;
import com.codenvy.ide.ext.openshift.client.domain.CreateDomainPresenter;
import com.codenvy.ide.ext.openshift.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.ext.openshift.client.info.ApplicationProperty;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationListPresenter implements ApplicationListView.ActionDelegate {
    private ApplicationListView           view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private OpenShiftAutoBeanFactory      autoBeanFactory;
    private LoginPresenter                loginPresenter;
    private ResourceProvider              resourceProvider;
    private CreateDomainPresenter         createDomainPresenter;
    private CreateCartridgePresenter      createCartridgePresenter;
    private ApplicationInfoPresenter      applicationInfoPresenter;
//    private AppInfo                       selectedApplication;

    @Inject
    protected ApplicationListPresenter(ApplicationListView view, EventBus eventBus, ConsolePart console, OpenShiftClientServiceImpl service,
                                       OpenShiftLocalizationConstant constant, OpenShiftAutoBeanFactory autoBeanFactory,
                                       LoginPresenter loginPresenter, ResourceProvider resourceProvider,
                                       CreateDomainPresenter createDomainPresenter, CreateCartridgePresenter createCartridgePresenter,
                                       ApplicationInfoPresenter applicationInfoPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;
        this.createDomainPresenter = createDomainPresenter;
        this.createCartridgePresenter = createCartridgePresenter;
        this.applicationInfoPresenter = applicationInfoPresenter;

        this.view.setDelegate(this);
    }

    public void showDialog() {
        if (!view.isShown()) {
            getApplications();

            view.showDialog();
        }
    }

    @Override
    public void onCloseClicked() {
        view.close();
    }

    @Override
    public void onChangeDomainNameClicked() {
        createDomainPresenter.showDialog();
    }

    @Override
    public void onChangeAccountClicked() {
        loginPresenter.showDialog();
    }

    @Override
    public void onCreateCartridgeClicked() {
        createCartridgePresenter.showDialog(view.getSelectedApplication());
    }

    @Override
    public void onApplicationDeleteClicked(final AppInfo application) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onApplicationDeleteClicked(application);
            }
        };

        final String projectId = resourceProvider.getActiveProject().getId();
        final String vfsId = resourceProvider.getVfsId();

        boolean delete = Window.confirm(constant.deleteApplicationPrompt(application.getName()));

        if (!delete) {
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
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

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
                                        }
                                    });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getApplications() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplications();
            }
        };

        try {
            AutoBean<RHUserInfo> rhUserInfo = autoBeanFactory.rhUserInfo();
            AutoBeanUnmarshaller<RHUserInfo> unmarshaller = new AutoBeanUnmarshaller<RHUserInfo>(rhUserInfo);

            service.getUserInfo(true,
                                new OpenShiftAsyncRequestCallback<RHUserInfo>(unmarshaller, loggedInHandler, null, eventBus, console,
                                                                              constant, loginPresenter) {
                                    @Override
                                    protected void onSuccess(RHUserInfo result) {
                                        view.setUserLogin(result.getRhlogin());
                                        view.setUserDomain(result.getRhcDomain());
                                        view.setApplications(result.getApps());

                                        if (result.getApps().size() > 0) {
                                            AppInfo app = result.getApps().get(0);
                                            List<ApplicationProperty> properties = applicationInfoPresenter.getApplicationProperties(app);

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
