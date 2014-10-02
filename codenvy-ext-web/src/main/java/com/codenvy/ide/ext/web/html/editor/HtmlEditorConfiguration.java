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

import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.texteditor.AutoEditStrategy;
import com.codenvy.ide.api.texteditor.TextEditorConfiguration;
import com.codenvy.ide.api.texteditor.TextEditorPartView;
import com.codenvy.ide.api.texteditor.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.api.texteditor.parser.CmParser;
import com.codenvy.ide.api.texteditor.parser.Parser;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * The html type editor configuration.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public class HtmlEditorConfiguration extends TextEditorConfiguration {

    /**
     * Auto edit factories.
     */
    private Set<AutoEditStrategyFactory> autoEditStrategyFactories;

    /**
     * Chained processor.
     */
    private DefaultCodeAssistProcessor defaultProcessor;

    /**
     * Build a new Configuration with the given set of strategies.
     *
     * @param autoEditStrategyFactories
     *         the strategy factories
     */
    public HtmlEditorConfiguration(Set<AutoEditStrategyFactory> autoEditStrategyFactories,
                                   DefaultCodeAssistProcessor defaultProcessor) {
        this.autoEditStrategyFactories = autoEditStrategyFactories;
        this.defaultProcessor = defaultProcessor;
    }

    /** {@inheritDoc} */
    @Override
    public Parser getParser(@Nonnull TextEditorPartView view) {
        CmParser parser = getParserForMime("text/html");
        parser.setNameAndFactory("html", new HtmlTokenFactory());
        return parser;
    }


    @Override
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors(@Nonnull TextEditorPartView view) {
        if (defaultProcessor.getProcessors() == null || defaultProcessor.getProcessors().size() == 0) {
            return null;
        }
        StringMap<CodeAssistProcessor> map = Collections.createStringMap();
        map.put(Document.DEFAULT_CONTENT_TYPE, defaultProcessor);
        return map;
    }


    /**
     * Adds strategy for Interpolation brace completion
     *
     * @param view
     *         the source viewer to be configured by this configuration
     * @param contentType
     *         the content type for which the strategies are applicable
     * @return
     */
    @Override
    public AutoEditStrategy[] getAutoEditStrategies(TextEditorPartView view, String contentType) {
        // Get super class strategy
        AutoEditStrategy[] parentStrategy = super.getAutoEditStrategies(view, contentType);

        // No injected strategies, go with default
        if (autoEditStrategyFactories == null || autoEditStrategyFactories.size() == 0) {
            return parentStrategy;
        }

        AutoEditStrategy[] strategies = new AutoEditStrategy[parentStrategy.length + autoEditStrategyFactories.size()];
        System.arraycopy(parentStrategy, 0, strategies, 0, parentStrategy.length);
        int i = parentStrategy.length;
        for (AutoEditStrategyFactory strategyFactory : autoEditStrategyFactories) {
            strategies[i++] = strategyFactory.build(view, contentType);
        }
        return strategies;
    }

}
