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
package com.codenvy.ide.extension.css.editor;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.parser.CmParser;
import com.codenvy.ide.texteditor.api.parser.Parser;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CssEditorConfiguration extends TextEditorConfiguration {

    private CssResources resourcess;

    /** @param resourcess */
    public CssEditorConfiguration(CssResources resourcess) {
        super();
        this.resourcess = resourcess;
    }

    private static native CmParser getParserForMime(String mime) /*-{
        conf = $wnd.CodeMirror.defaults;
        return $wnd.CodeMirror.getMode(conf, mime);
    }-*/;


    /** {@inheritDoc} */
    @Override
    public Parser getParser(TextEditorPartView view) {
        CmParser parser = getParserForMime("text/css");
        parser.setNameAndFactory("css", new CssTokenFactory());
        return parser;
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorConfiguration#getContentAssistantProcessors(com.codenvy.ide.texteditor.api
     * .TextEditorPartView) */
    @Override
    public JsonStringMap<CodeAssistProcessor> getContentAssistantProcessors(TextEditorPartView view) {
        JsonStringMap<CodeAssistProcessor> map = JsonCollections.createStringMap();
        map.put(Document.DEFAULT_CONTENT_TYPE, new CssCodeAssistantProcessor(resourcess));
        return map;
    }
}
