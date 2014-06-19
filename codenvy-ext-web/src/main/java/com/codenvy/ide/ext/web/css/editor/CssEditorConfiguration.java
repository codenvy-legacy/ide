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
package com.codenvy.ide.ext.web.css.editor;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.parser.CmParser;
import com.codenvy.ide.texteditor.api.parser.Parser;

import javax.validation.constraints.NotNull;

/**
 * The css css type editor configuration.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public class CssEditorConfiguration extends TextEditorConfiguration {

    private CssResources resources;

    public CssEditorConfiguration(CssResources resources) {
        super();
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public Parser getParser(@NotNull TextEditorPartView view) {
        CmParser parser = getParserForMime("text/css");
        parser.setNameAndFactory("css", new CssTokenFactory());
        return parser;
    }

    /** {@inheritDoc} */
    @Override
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors(@NotNull TextEditorPartView view) {
        StringMap<CodeAssistProcessor> map = Collections.createStringMap();
        map.put(Document.DEFAULT_CONTENT_TYPE, new CssCodeAssistantProcessor(resources));
        return map;
    }
}
