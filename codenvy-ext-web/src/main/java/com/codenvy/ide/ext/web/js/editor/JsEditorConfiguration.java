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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.web.html.editor.AutoEditStrategyFactory;
import com.codenvy.ide.jseditor.client.changeintercept.ChangeInterceptorProvider;
import com.codenvy.ide.jseditor.client.changeintercept.TextChangeInterceptor;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistProcessor;
import com.codenvy.ide.jseditor.client.editorconfig.DefaultTextEditorConfiguration;
import com.codenvy.ide.jseditor.client.partition.DocumentPartitioner;

/**
 * The css css type editor configuration.
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public class JsEditorConfiguration extends DefaultTextEditorConfiguration {


    private Set<AutoEditStrategyFactory> autoEditStrategyFactories;
    private DefaultCodeAssistProcessor defaultProcessor;

    /**
     * Build a new Configuration with the given set of strategies.
     * 
     * @param autoEditStrategyFactories the strategy factories
     */
    public JsEditorConfiguration(Set<AutoEditStrategyFactory> autoEditStrategyFactories,
                                 DefaultCodeAssistProcessor defaultProcessor) {
        this.autoEditStrategyFactories = autoEditStrategyFactories;
        this.defaultProcessor = defaultProcessor;
    }

    @Override
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors() {
        if (defaultProcessor.getProcessors() == null || defaultProcessor.getProcessors().size() == 0) {
            return null;
        }
        StringMap<CodeAssistProcessor> map = Collections.createStringMap();
        map.put(DocumentPartitioner.DEFAULT_CONTENT_TYPE, defaultProcessor);
        return map;
    }

    @Override
    public ChangeInterceptorProvider getChangeInterceptorProvider() {
        final ChangeInterceptorProvider parentProvider = super.getChangeInterceptorProvider();
        if (this.autoEditStrategyFactories == null) {
            return parentProvider;
        }
        return new ChangeInterceptorProvider() {
            @Override
            public List<TextChangeInterceptor> getInterceptors(final String contentType) {
                final List<TextChangeInterceptor> result = new ArrayList<>();
                if (parentProvider != null) {
                    final List<TextChangeInterceptor> parentProvided = parentProvider.getInterceptors(contentType);
                    if (parentProvided != null) {
                        result.addAll(parentProvided);
                    }
                }

                for (AutoEditStrategyFactory strategyFactory : autoEditStrategyFactories) {
                    final TextChangeInterceptor interceptor = strategyFactory.build(contentType);
                    result.add(interceptor);
                }
                return result;
            }
        };
    }
}
