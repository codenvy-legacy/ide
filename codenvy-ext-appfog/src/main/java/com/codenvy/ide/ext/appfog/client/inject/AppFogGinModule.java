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
package com.codenvy.ide.ext.appfog.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogClientServiceImpl;
import com.codenvy.ide.ext.appfog.client.apps.ApplicationsView;
import com.codenvy.ide.ext.appfog.client.apps.ApplicationsViewImpl;
import com.codenvy.ide.ext.appfog.client.create.CreateApplicationView;
import com.codenvy.ide.ext.appfog.client.create.CreateApplicationViewImpl;
import com.codenvy.ide.ext.appfog.client.delete.DeleteApplicationView;
import com.codenvy.ide.ext.appfog.client.delete.DeleteApplicationViewImpl;
import com.codenvy.ide.ext.appfog.client.info.ApplicationInfoView;
import com.codenvy.ide.ext.appfog.client.info.ApplicationInfoViewImpl;
import com.codenvy.ide.ext.appfog.client.login.LoginView;
import com.codenvy.ide.ext.appfog.client.login.LoginViewImpl;
import com.codenvy.ide.ext.appfog.client.project.AppFogProjectView;
import com.codenvy.ide.ext.appfog.client.project.AppFogProjectViewImpl;
import com.codenvy.ide.ext.appfog.client.services.CreateServiceView;
import com.codenvy.ide.ext.appfog.client.services.CreateServiceViewImpl;
import com.codenvy.ide.ext.appfog.client.services.ManageServicesView;
import com.codenvy.ide.ext.appfog.client.services.ManageServicesViewImpl;
import com.codenvy.ide.ext.appfog.client.url.UnmapUrlView;
import com.codenvy.ide.ext.appfog.client.url.UnmapUrlViewImpl;
import com.codenvy.ide.ext.appfog.client.wizard.AppFogPageView;
import com.codenvy.ide.ext.appfog.client.wizard.AppFogPageViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class AppFogGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(AppfogClientService.class).to(AppfogClientServiceImpl.class).in(Singleton.class);

        // Views
        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(ApplicationsView.class).to(ApplicationsViewImpl.class).in(Singleton.class);
        bind(DeleteApplicationView.class).to(DeleteApplicationViewImpl.class).in(Singleton.class);
        bind(AppFogPageView.class).to(AppFogPageViewImpl.class).in(Singleton.class);
        bind(CreateApplicationView.class).to(CreateApplicationViewImpl.class).in(Singleton.class);
        bind(AppFogProjectView.class).to(AppFogProjectViewImpl.class).in(Singleton.class);
        bind(ApplicationInfoView.class).to(ApplicationInfoViewImpl.class).in(Singleton.class);
        bind(ManageServicesView.class).to(ManageServicesViewImpl.class).in(Singleton.class);
        bind(CreateServiceView.class).to(CreateServiceViewImpl.class).in(Singleton.class);
        bind(UnmapUrlView.class).to(UnmapUrlViewImpl.class).in(Singleton.class);
    }
}