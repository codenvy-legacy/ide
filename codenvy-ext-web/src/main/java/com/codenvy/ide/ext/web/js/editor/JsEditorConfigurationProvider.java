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
package com.codenvy.ide.ext.web.js.editor;

import com.google.inject.Inject;

import javax.inject.Provider;
import java.util.Set;

/**
 * Provider for HTML Editor configuration.
 *
 * @author Florent Benoit
 */

public class JsEditorConfigurationProvider implements Provider<JsEditorConfiguration> {

    /**
     * Auto Edit strategies
     */
    @Inject(optional = true)
    private Set<AutoEditStrategyFactory> autoEditStrategyFactories;

    @Inject
    private DefaultCodeAssistProcessor chainedCodeAssistProcessor;


    /**
     * Build a new instance of JsEditor Configuration
     *
     * @return
     */
    @Override
    public JsEditorConfiguration get() {
        return new JsEditorConfiguration(autoEditStrategyFactories, chainedCodeAssistProcessor);
    }
}
