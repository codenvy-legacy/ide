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
package com.codenvy.ide.ext.openshift.client.cartridge;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.ListUnmarshaller;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateCartridgePresenter implements CreateCartridgeView.ActionDelegate {
    private CreateCartridgeView           view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private AppInfo                       application;
    private AsyncCallback<Boolean>        callback;

    @Inject
    protected CreateCartridgePresenter(CreateCartridgeView view, EventBus eventBus, ConsolePart console, OpenShiftClientServiceImpl service,
                                       OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.service = service;
        this.loginPresenter = loginPresenter;

        this.view.setDelegate(this);
    }

    public void showDialog(AppInfo application, AsyncCallback<Boolean> callback) {
        this.application = application;
        this.callback = callback;

        if (!view.isShown()) {
            setCartridges();
        }
    }

    private void setCartridges() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                setCartridges();
            }
        };

        try {
            ListUnmarshaller unmarshaller = new ListUnmarshaller(new ArrayList<String>());
            service.getCartridges(
                    new OpenShiftAsyncRequestCallback<List<String>>(unmarshaller, loggedInHandler, null, eventBus, console,
                                                                    constant, loginPresenter) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            JsonArray<OpenShiftEmbeddableCartridge> cartridges = application.getEmbeddedCartridges();
                            for (int i = 0; i < cartridges.size(); i++) {
                                if (result.contains(cartridges.get(i).getName())) {
                                    result.remove(cartridges.get(i).getName());
                                }
                            }

                            JsonArray<String> list = JsonCollections.createArray();
                            for (String cartridge : result) {
                                list.add(cartridge);
                            }

                            view.setCartridgesList(list);
                            view.showDialog();
                        }
                    });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onCreateCartridgeClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onCreateCartridgeClicked();
            }
        };

        final String appName = application.getName();
        final String cartridgeName = view.getCartridgeName();
        try {
            service.addCartridge(appName, cartridgeName,
                                 new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                         loginPresenter) {
                                     @Override
                                     protected void onSuccess(Void result) {
                                         if (callback != null) {
                                             callback.onSuccess(true);
                                         }
                                         view.close();
                                         String msg = constant.createCartridgeViewSuccessfullyAdded(cartridgeName, appName);
                                         console.print(msg);
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         super.onFailure(exception);
                                         if (callback != null) {
                                             callback.onSuccess(false);
                                         }
                                         view.close();
                                     }
                                 });
        } catch (RequestException e) {
            view.close();

            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onCancelClicked() {

    }
}
