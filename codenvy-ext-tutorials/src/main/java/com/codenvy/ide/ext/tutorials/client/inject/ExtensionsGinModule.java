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
package com.codenvy.ide.ext.tutorials.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.tutorials.client.update.UpdateServiceClient;
import com.codenvy.ide.ext.tutorials.client.update.UpdateServiceClientImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * @author Vitaly Parfonov
 */
@ExtensionGinModule
public class ExtensionsGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(UpdateServiceClient.class).to(UpdateServiceClientImpl.class).in(Singleton.class);
    }
}
