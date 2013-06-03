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
package com.codenvy.ide.ext.openshift.client.delete;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftAutoBeanFactory;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class DeleteApplicationPresenter {
    private EventBus                      eventBus;
    private ResourceProvider              resourceProvider;
    private ConsolePart                   console;
    private OpenShiftLocalizationConstant constant;
    private AsyncCallback<String>         appDeletedCallback;
    private LoginPresenter                loginPresenter;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftAutoBeanFactory      autoBeanFactory;

    @Inject
    protected DeleteApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                         OpenShiftLocalizationConstant constant, AsyncCallback<String> appDeletedCallback,
                                         LoginPresenter loginPresenter, OpenShiftClientServiceImpl service,
                                         OpenShiftAutoBeanFactory autoBeanFactory) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.appDeletedCallback = appDeletedCallback;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.autoBeanFactory = autoBeanFactory;
    }

    private LoggedInHandler deleteAppLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            checkApplicationExist();
        }
    };

    public void checkApplicationExist() {
        final String projectId = resourceProvider.getActiveProject().getId();
        final String vfsId = resourceProvider.getVfsId();

        try {
            AutoBean<AppInfo> appInfo = autoBeanFactory.appInfo();
            AutoBeanUnmarshaller<AppInfo> unmarshaller = new AutoBeanUnmarshaller<AppInfo>(appInfo);

            service.getApplicationInfo(null, vfsId, projectId,
                                       new OpenShiftAsyncRequestCallback<AppInfo>(unmarshaller, deleteAppLoggedInHandler, null, eventBus,
                                                                                  console, constant, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppInfo result) {
                                               askDeleteApplication(result.getName());
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void askDeleteApplication(String appName) {
        boolean delete = Window.confirm(constant.deleteApplicationPrompt(appName));

        if (delete) {
            deleteApplication(appName);
        }
    }

    private void deleteApplication(final String appName) {
        final String projectId = resourceProvider.getActiveProject().getId();
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.destroyApplication(appName, vfsId, projectId,
                                       new OpenShiftAsyncRequestCallback<String>(null, deleteAppLoggedInHandler, null, eventBus, console,
                                                                                 constant, loginPresenter) {
                                           @Override
                                           protected void onSuccess(String result) {
                                               String msg = constant.deleteApplicationSuccessfullyDeleted(appName);
                                               console.print(msg);
                                               appDeletedCallback.onSuccess(appName);
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            appDeletedCallback.onFailure(e);
        }
    }
}
