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