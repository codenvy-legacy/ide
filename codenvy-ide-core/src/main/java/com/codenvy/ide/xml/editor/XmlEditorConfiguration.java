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
package com.codenvy.ide.xml.editor;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.texteditor.TextEditorConfiguration;
import com.codenvy.ide.api.texteditor.TextEditorPartView;
import com.codenvy.ide.api.texteditor.parser.BasicTokenFactory;
import com.codenvy.ide.api.texteditor.parser.CmParser;
import com.codenvy.ide.api.texteditor.parser.Parser;

import javax.validation.constraints.NotNull;

/**
 * The XML file type editor configuration.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public class XmlEditorConfiguration extends TextEditorConfiguration {

    /** {@inheritDoc} */
    @Override
    public Parser getParser(@NotNull TextEditorPartView view) {
        CmParser parser = getParserForMime(MimeType.APPLICATION_XML);
        parser.setNameAndFactory("xml", new BasicTokenFactory());
        return parser;
    }
}
