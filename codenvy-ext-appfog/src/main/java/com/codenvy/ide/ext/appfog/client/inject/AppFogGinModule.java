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