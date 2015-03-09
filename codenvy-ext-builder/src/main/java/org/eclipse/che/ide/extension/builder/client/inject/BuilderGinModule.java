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
package org.eclipse.che.ide.extension.builder.client.inject;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.extension.builder.client.console.BuilderConsoleToolbar;
import org.eclipse.che.ide.extension.builder.client.console.BuilderConsoleView;
import org.eclipse.che.ide.extension.builder.client.console.BuilderConsoleViewImpl;
import org.eclipse.che.ide.toolbar.ToolbarPresenter;
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