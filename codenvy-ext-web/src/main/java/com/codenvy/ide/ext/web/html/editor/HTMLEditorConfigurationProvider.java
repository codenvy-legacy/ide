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
package com.codenvy.ide.ext.web.html.editor;

import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.google.inject.Inject;

import javax.inject.Provider;
import java.util.Set;

/**
 * Guice Provider for HTML Editor configuration.
 * @author Florent Benoit
 */

public class HTMLEditorConfigurationProvider implements Provider<HtmlEditorConfiguration> {

    /**
     * Auto Edit strategies with HTML editor scope
     */
    @Inject(optional = true)
    private Set<AutoEditStrategyFactory> autoEditStrategyFactories;

    @Inject
    private DefaultCodeAssistProcessor chainedCodeAssistProcessor;


    /**
     * Build a new instance of HtmlEditor Configuration
     * @return
     */
    @Override
    public HtmlEditorConfiguration get() {
        return new HtmlEditorConfiguration(autoEditStrategyFactories, chainedCodeAssistProcessor);
    }
}
