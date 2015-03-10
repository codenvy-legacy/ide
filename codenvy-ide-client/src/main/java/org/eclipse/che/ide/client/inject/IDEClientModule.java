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
package org.eclipse.che.ide.client.inject;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.client.StyleInjector;
import org.eclipse.che.ide.client.BootstrapController;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;


/**
 * GIN Client module for ide-client subproject. Used to maintain relations of
 * ide-client specific components.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@ExtensionGinModule
public class IDEClientModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(BootstrapController.class).in(Singleton.class);
        bind(StyleInjector.class).in(Singleton.class);
    }
}
