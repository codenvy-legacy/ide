/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.inject;

import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.runner.gwt.client.RunnerServiceClientImpl;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleToolbar;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleView;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleViewImpl;
import com.codenvy.ide.extension.runner.client.run.CustomRunView;
import com.codenvy.ide.extension.runner.client.run.CustomRunViewImpl;
import com.codenvy.ide.extension.runner.client.update.UpdateServiceClient;
import com.codenvy.ide.extension.runner.client.update.UpdateServiceClientImpl;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author Artem Zatsarynnyy */
@ExtensionGinModule
public class RunnerGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(RunnerServiceClient.class).to(RunnerServiceClientImpl.class).in(Singleton.class);
        bind(UpdateServiceClient.class).to(UpdateServiceClientImpl.class).in(Singleton.class);
        bind(CustomRunView.class).to(CustomRunViewImpl.class).in(Singleton.class);
        bind(RunnerConsoleView.class).to(RunnerConsoleViewImpl.class).in(Singleton.class);
        bind(ToolbarPresenter.class).annotatedWith(RunnerConsoleToolbar.class).to(ToolbarPresenter.class).in(Singleton.class);
    }
}
