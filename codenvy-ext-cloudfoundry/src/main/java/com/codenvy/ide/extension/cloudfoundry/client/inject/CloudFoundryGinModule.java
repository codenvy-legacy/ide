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
package com.codenvy.ide.extension.cloudfoundry.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientServiceImpl;
import com.codenvy.ide.extension.cloudfoundry.client.apps.ApplicationsView;
import com.codenvy.ide.extension.cloudfoundry.client.apps.ApplicationsViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.create.CreateApplicationView;
import com.codenvy.ide.extension.cloudfoundry.client.create.CreateApplicationViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationView;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.info.ApplicationInfoView;
import com.codenvy.ide.extension.cloudfoundry.client.info.ApplicationInfoViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginView;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.project.CloudFoundryProjectView;
import com.codenvy.ide.extension.cloudfoundry.client.project.CloudFoundryProjectViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.services.CreateServiceView;
import com.codenvy.ide.extension.cloudfoundry.client.services.CreateServiceViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.services.ManageServicesView;
import com.codenvy.ide.extension.cloudfoundry.client.services.ManageServicesViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.url.UnmapUrlView;
import com.codenvy.ide.extension.cloudfoundry.client.url.UnmapUrlViewImpl;
import com.codenvy.ide.extension.cloudfoundry.client.wizard.CloudFoundryPageView;
import com.codenvy.ide.extension.cloudfoundry.client.wizard.CloudFoundryPageViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class CloudFoundryGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(CloudFoundryClientService.class).to(CloudFoundryClientServiceImpl.class).in(Singleton.class);

        // Views
        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(ApplicationsView.class).to(ApplicationsViewImpl.class).in(Singleton.class);
        bind(CreateApplicationView.class).to(CreateApplicationViewImpl.class).in(Singleton.class);
        bind(CloudFoundryProjectView.class).to(CloudFoundryProjectViewImpl.class).in(Singleton.class);
        bind(DeleteApplicationView.class).to(DeleteApplicationViewImpl.class).in(Singleton.class);
        bind(ManageServicesView.class).to(ManageServicesViewImpl.class).in(Singleton.class);
        bind(CreateServiceView.class).to(CreateServiceViewImpl.class).in(Singleton.class);
        bind(ApplicationInfoView.class).to(ApplicationInfoViewImpl.class).in(Singleton.class);
        bind(UnmapUrlView.class).to(UnmapUrlViewImpl.class).in(Singleton.class);
        bind(CloudFoundryPageView.class).to(CloudFoundryPageViewImpl.class).in(Singleton.class);
    }
}