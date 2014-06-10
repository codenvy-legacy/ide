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
package com.codenvy.ide.tutorial.editor.editor;

import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.parser.BasicTokenFactory;
import com.codenvy.ide.texteditor.api.parser.CmParser;
import com.codenvy.ide.texteditor.api.parser.Parser;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.tutorial.editor.EditorTutorialExtension.GROOVY_MIME_TYPE;

/**
 * The groovy file type editor configuration.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class GroovyEditorConfiguration extends TextEditorConfiguration {

    public GroovyEditorConfiguration() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Parser getParser(@NotNull TextEditorPartView view) {
        CmParser parser = getParserForMime(GROOVY_MIME_TYPE);
        parser.setNameAndFactory("groovy", new BasicTokenFactory());
        return parser;
    }
}