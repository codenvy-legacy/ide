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
package com.codenvy.ide.ext.gae.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAEClientServiceImpl;
import com.codenvy.ide.ext.gae.client.create.CreateApplicationView;
import com.codenvy.ide.ext.gae.client.create.CreateApplicationViewImpl;
import com.codenvy.ide.ext.gae.client.project.ProjectView;
import com.codenvy.ide.ext.gae.client.project.ProjectViewImpl;
import com.codenvy.ide.ext.gae.client.project.backend.BackendTabPaneView;
import com.codenvy.ide.ext.gae.client.project.backend.BackendTabPaneViewImpl;
import com.codenvy.ide.ext.gae.client.project.cron.CronTabPaneView;
import com.codenvy.ide.ext.gae.client.project.cron.CronTabPaneViewImpl;
import com.codenvy.ide.ext.gae.client.project.general.GeneralTabPaneView;
import com.codenvy.ide.ext.gae.client.project.general.GeneralTabPaneViewImpl;
import com.codenvy.ide.ext.gae.client.project.general.logs.LogsView;
import com.codenvy.ide.ext.gae.client.project.general.logs.LogsViewImpl;
import com.codenvy.ide.ext.gae.client.project.limit.LimitTabPaneView;
import com.codenvy.ide.ext.gae.client.project.limit.LimitTabPaneViewImpl;
import com.codenvy.ide.ext.gae.client.wizard.GAEWizardView;
import com.codenvy.ide.ext.gae.client.wizard.GAEWizardViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class GAEGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(GAEClientService.class).to(GAEClientServiceImpl.class).in(Singleton.class);

        bind(CreateApplicationView.class).to(CreateApplicationViewImpl.class).in(Singleton.class);
        bind(GAEWizardView.class).to(GAEWizardViewImpl.class).in(Singleton.class);

        bind(ProjectView.class).to(ProjectViewImpl.class).in(Singleton.class);
        bind(GeneralTabPaneView.class).to(GeneralTabPaneViewImpl.class).in(Singleton.class);
        bind(LimitTabPaneView.class).to(LimitTabPaneViewImpl.class).in(Singleton.class);
        bind(CronTabPaneView.class).to(CronTabPaneViewImpl.class).in(Singleton.class);
        bind(BackendTabPaneView.class).to(BackendTabPaneViewImpl.class).in(Singleton.class);

        bind(LogsView.class).to(LogsViewImpl.class).in(Singleton.class);
    }
}