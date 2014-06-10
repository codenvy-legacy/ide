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
package com.codenvy.ide.ext.web.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.web.html.editor.HTMLEditorConfigurationProvider;
import com.codenvy.ide.ext.web.html.editor.HtmlEditorConfiguration;
import com.codenvy.ide.ext.web.js.editor.JsEditorConfiguration;
import com.codenvy.ide.ext.web.js.editor.JsEditorConfigurationProvider;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * Adds custom binding for Editors.
 * @author Florent Benoit
 */
@ExtensionGinModule
public class WebModule extends AbstractGinModule {

        /** {@inheritDoc} */
        @Override
        protected void configure() {
            bind(HtmlEditorConfiguration.class).toProvider(HTMLEditorConfigurationProvider.class).in(Singleton.class);
            bind(JsEditorConfiguration.class).toProvider(JsEditorConfigurationProvider.class).in(Singleton.class);
        }

}
