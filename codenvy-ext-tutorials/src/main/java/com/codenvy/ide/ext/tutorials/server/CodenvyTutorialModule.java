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
package com.codenvy.ide.ext.tutorials.server;

import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;

/** @author Artem Zatsarynnyy */
@DynaModule
public class CodenvyTutorialModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectTypeDescriptionsExtension.class);
        bind(CodenvyTutorialProjectTypeExtension.class);
        bind(CodenvyPluginProjectTypeDescriptionExtension.class);
        bind(CodenvyPluginProjectTypeExtension.class);
    }
}
