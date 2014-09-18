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
package com.codenvy.ide.extension.builder.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.extension.builder.client.console.BuilderConsoleToolbar;
import com.codenvy.ide.extension.builder.client.console.BuilderConsoleView;
import com.codenvy.ide.extension.builder.client.console.BuilderConsoleViewImpl;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author Andrey Plotnikov */
@ExtensionGinModule
public class BuilderGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(BuilderConsoleView.class).to(BuilderConsoleViewImpl.class).in(Singleton.class);
        bind(ToolbarPresenter.class).annotatedWith(BuilderConsoleToolbar.class).to(ToolbarPresenter.class).in(Singleton.class);
    }
}