/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.preferences.PreferencePagePresenter;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleToolbar;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleView;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleViewImpl;
import com.codenvy.ide.extension.runner.client.run.customenvironments.CustomEnvironmentsView;
import com.codenvy.ide.extension.runner.client.run.customenvironments.CustomEnvironmentsViewImpl;
import com.codenvy.ide.extension.runner.client.run.customenvironments.EnvironmentActionFactory;
import com.codenvy.ide.extension.runner.client.run.customenvironments.EnvironmentActionsManager;
import com.codenvy.ide.extension.runner.client.run.customrun.CustomRunView;
import com.codenvy.ide.extension.runner.client.run.customrun.CustomRunViewImpl;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/** @author Artem Zatsarynnyy */
@ExtensionGinModule
public class RunnerGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(CustomRunView.class).to(CustomRunViewImpl.class).in(Singleton.class);

        bind(RunnerConsoleView.class).to(RunnerConsoleViewImpl.class).in(Singleton.class);
        bind(ToolbarPresenter.class).annotatedWith(RunnerConsoleToolbar.class).to(ToolbarPresenter.class).in(Singleton.class);

        bind(EnvironmentActionsManager.class).asEagerSingleton();
        install(new GinFactoryModuleBuilder().build(EnvironmentActionFactory.class));
        bind(CustomEnvironmentsView.class).to(CustomEnvironmentsViewImpl.class).in(Singleton.class);
    }

    /** Provides project-relative path to the folder for project-scoped runner environments. */
    @Provides
    @Named("envFolderPath")
    @Singleton
    protected String provideEnvironmentsFolderRelPath() {
        return ".codenvy/runners/environments";
    }
}
