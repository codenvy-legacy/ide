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
package com.codenvy.ide.ext.cloudbees.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientServiceImpl;
import com.codenvy.ide.ext.cloudbees.client.account.CreateAccountView;
import com.codenvy.ide.ext.cloudbees.client.account.CreateAccountViewImpl;
import com.codenvy.ide.ext.cloudbees.client.apps.ApplicationsView;
import com.codenvy.ide.ext.cloudbees.client.apps.ApplicationsViewImpl;
import com.codenvy.ide.ext.cloudbees.client.create.CreateApplicationView;
import com.codenvy.ide.ext.cloudbees.client.create.CreateApplicationViewImpl;
import com.codenvy.ide.ext.cloudbees.client.info.ApplicationInfoView;
import com.codenvy.ide.ext.cloudbees.client.info.ApplicationInfoViewImpl;
import com.codenvy.ide.ext.cloudbees.client.login.LoginView;
import com.codenvy.ide.ext.cloudbees.client.login.LoginViewImpl;
import com.codenvy.ide.ext.cloudbees.client.project.CloudBeesProjectView;
import com.codenvy.ide.ext.cloudbees.client.project.CloudBeesProjectViewImpl;
import com.codenvy.ide.ext.cloudbees.client.wizard.CloudBeesPageView;
import com.codenvy.ide.ext.cloudbees.client.wizard.CloudBeesPageViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class CloudBeesGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(CloudBeesClientService.class).to(CloudBeesClientServiceImpl.class).in(Singleton.class);

        // Views
        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(ApplicationsView.class).to(ApplicationsViewImpl.class).in(Singleton.class);
        bind(ApplicationInfoView.class).to(ApplicationInfoViewImpl.class).in(Singleton.class);
        bind(CreateAccountView.class).to(CreateAccountViewImpl.class).in(Singleton.class);
        bind(CloudBeesPageView.class).to(CloudBeesPageViewImpl.class).in(Singleton.class);
        bind(CreateApplicationView.class).to(CreateApplicationViewImpl.class).in(Singleton.class);
        bind(CloudBeesProjectView.class).to(CloudBeesProjectViewImpl.class).in(Singleton.class);
    }
}