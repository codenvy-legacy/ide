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
package com.codenvy.ide.ext.openshift.client.key;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.UserInfoUnmarshaller;
import com.codenvy.ide.ext.openshift.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Handler which execute update public ssh key for OpenShift account.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class UpdateKeyPresenter {
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private OpenShiftClientServiceImpl    service;
    private AsyncCallback<Boolean>        publicKeyUpdateCallback;

    /**
     * Create handler.
     *
     * @param eventBus
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected UpdateKeyPresenter(EventBus eventBus, ConsolePart console, OpenShiftLocalizationConstant constant,
                                 LoginPresenter loginPresenter, OpenShiftClientServiceImpl service) {
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** If user is not logged in to OpenShift, this handler will be called, after user logged in. */
    private LoggedInHandler updatePublicKeyLoginHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            updatePublicKey(publicKeyUpdateCallback);
        }
    };

    /**
     * Perform to update ssh public key for current loggined account.
     *
     * @param callback
     *         callback which will be executed if update is successful or fails
     */
    public void updatePublicKey(AsyncCallback<Boolean> callback) {
        this.publicKeyUpdateCallback = callback;

        try {
            DtoClientImpls.RHUserInfoImpl userInfo = DtoClientImpls.RHUserInfoImpl.make();
            UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller(userInfo);

            service.getUserInfo(false,
                                new OpenShiftAsyncRequestCallback<RHUserInfo>(unmarshaller, updatePublicKeyLoginHandler, null, eventBus,
                                                                              console, constant, loginPresenter) {
                                    @Override
                                    protected void onSuccess(RHUserInfo result) {
                                        if (result.getNamespace() != null && !result.getNamespace().isEmpty()) {
                                            updateDomainWithNewKey(result.getNamespace());
                                        } else {
                                            if (publicKeyUpdateCallback != null) {
                                                publicKeyUpdateCallback.onSuccess(false);
                                            }
                                            Window.alert("You must create domain first to update public key for it.");
                                        }
                                    }
                                });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Perform update in domain public ssh key.
     *
     * @param nameSpace
     *         domain name in which key should be updated
     */
    private void updateDomainWithNewKey(String nameSpace) {
        try {
            service.createDomain(nameSpace, true,
                                 new OpenShiftAsyncRequestCallback<String>(null, updatePublicKeyLoginHandler, null, eventBus, console,
                                                                           constant, loginPresenter) {
                                     @Override
                                     protected void onSuccess(String result) {
                                         String msg = "Public key successfully updated.";
                                         console.print(msg);
                                         if (publicKeyUpdateCallback != null) {
                                             publicKeyUpdateCallback.onSuccess(true);
                                         }
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         super.onFailure(exception);
                                         if (publicKeyUpdateCallback != null) {
                                             publicKeyUpdateCallback.onSuccess(false);
                                         }
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
            if (publicKeyUpdateCallback != null) {
                publicKeyUpdateCallback.onFailure(e);
            }
        }
    }
}
