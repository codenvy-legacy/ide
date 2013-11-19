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
package com.codenvy.ide.tutorial.editor.editor;

import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.parser.BasicTokenFactory;
import com.codenvy.ide.texteditor.api.parser.CmParser;
import com.codenvy.ide.texteditor.api.parser.Parser;

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

    private static native CmParser getParserForMime(String mime) /*-{
        conf = $wnd.CodeMirror.defaults;
        return $wnd.CodeMirror.getMode(conf, mime);
    }-*/;

    /** {@inheritDoc} */
    @Override
    public Parser getParser(TextEditorPartView view) {
        CmParser parser = getParserForMime(GROOVY_MIME_TYPE);
        parser.setNameAndFactory("groovy", new BasicTokenFactory());
        return parser;
    }
}