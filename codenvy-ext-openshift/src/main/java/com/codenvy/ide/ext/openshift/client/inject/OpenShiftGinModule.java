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
package com.codenvy.ide.ext.openshift.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientService;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.cartridge.CreateCartridgeView;
import com.codenvy.ide.ext.openshift.client.cartridge.CreateCartridgeViewImpl;
import com.codenvy.ide.ext.openshift.client.domain.CreateDomainView;
import com.codenvy.ide.ext.openshift.client.domain.CreateDomainViewImpl;
import com.codenvy.ide.ext.openshift.client.info.ApplicationInfoView;
import com.codenvy.ide.ext.openshift.client.info.ApplicationInfoViewImpl;
import com.codenvy.ide.ext.openshift.client.list.ApplicationListView;
import com.codenvy.ide.ext.openshift.client.list.ApplicationListViewImpl;
import com.codenvy.ide.ext.openshift.client.login.LoginView;
import com.codenvy.ide.ext.openshift.client.login.LoginViewImpl;
import com.codenvy.ide.ext.openshift.client.project.ProjectView;
import com.codenvy.ide.ext.openshift.client.project.ProjectViewImpl;
import com.codenvy.ide.ext.openshift.client.wizard.OpenShiftPageView;
import com.codenvy.ide.ext.openshift.client.wizard.OpenShiftPageViewImpl;
import com.codenvy.ide.extension.maven.client.template.CreateEmptyProjectPresenter;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a> */
@ExtensionGinModule
public class OpenShiftGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(OpenShiftClientService.class).to(OpenShiftClientServiceImpl.class).in(Singleton.class);

        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(CreateCartridgeView.class).to(CreateCartridgeViewImpl.class).in(Singleton.class);
        bind(CreateDomainView.class).to(CreateDomainViewImpl.class).in(Singleton.class);
        bind(ApplicationInfoView.class).to(ApplicationInfoViewImpl.class).in(Singleton.class);
        bind(ApplicationListView.class).to(ApplicationListViewImpl.class).in(Singleton.class);
        bind(ProjectView.class).to(ProjectViewImpl.class).in(Singleton.class);
        bind(OpenShiftPageView.class).to(OpenShiftPageViewImpl.class).in(Singleton.class);
        bind(CreateProjectProvider.class).to(CreateEmptyProjectPresenter.class).in(Singleton.class);
    }
}
