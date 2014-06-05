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
package com.codenvy.ide.ext.github.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.github.client.GitHubClientService;
import com.codenvy.ide.ext.github.client.GitHubClientServiceImpl;
import com.codenvy.ide.ext.github.client.load.ImportView;
import com.codenvy.ide.ext.github.client.load.ImportViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class GitHubGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(GitHubClientService.class).to(GitHubClientServiceImpl.class).in(Singleton.class);
        bind(ImportView.class).to(ImportViewImpl.class).in(Singleton.class);
    }
}