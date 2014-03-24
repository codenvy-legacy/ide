/*
 * CODENVY CONFIDENTIAL
 *  __________________
 *
 *   [2014] Codenvy, S.A.
 *   All Rights Reserved.
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of Codenvy S.A. and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to Codenvy S.A.
 *  and its suppliers and may be covered by U.S. and Foreign Patents,
 *  patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from Codenvy S.A..
 */

package com.codenvy.ide.ext.web.js.editor;

import com.google.inject.Inject;

import javax.inject.Provider;
import java.util.Set;

/**
 * Provider for HTML Editor configuration.
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
     * @return
     */
    @Override
    public JsEditorConfiguration get() {
        return new JsEditorConfiguration(autoEditStrategyFactories, chainedCodeAssistProcessor);
    }
}
