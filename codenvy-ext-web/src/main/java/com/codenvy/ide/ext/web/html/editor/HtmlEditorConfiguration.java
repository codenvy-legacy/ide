/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.web.html.editor;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.web.css.editor.CssCodeAssistantProcessor;
import com.codenvy.ide.ext.web.css.editor.CssResources;
import com.codenvy.ide.ext.web.css.editor.CssTokenFactory;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.parser.CmParser;
import com.codenvy.ide.texteditor.api.parser.Parser;

/**
 * The html css type editor configuration.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public class HtmlEditorConfiguration extends TextEditorConfiguration {

    /** {@inheritDoc} */
    @Override
    public Parser getParser(@NotNull TextEditorPartView view) {
        CmParser parser = getParserForMime("text/html");
        parser.setNameAndFactory("html", new HtmlTokenFactory());
        return parser;
    }

}
