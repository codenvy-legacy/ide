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
package com.codenvy.ide.ext.web;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.ext.web.css.CssFileProvider;
import com.codenvy.ide.ext.web.css.editor.CssEditorProvider;
import com.codenvy.ide.ext.web.html.HtmlFileProvider;
import com.codenvy.ide.ext.web.html.editor.HtmlEditorProvider;
import com.codenvy.ide.ext.web.js.JsFileProvider;
import com.codenvy.ide.ext.web.js.editor.JsEditorProvider;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Extension add editing JavaScript, HTML, CSS css type support to the IDE Application.
 * It provides configured TextEditorView with {@link com.codenvy.ide.ext.web.css.editor.CssEditorProvider} with syntax coloring and autocomplete.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
@Extension(title = "Web", version = "3.0.0", description = "syntax highlighting and autocomplete.")
public class WebExtension {
    public interface ParserResource extends ClientBundle {
        @Source("com/codenvy/ide/ext/web/web_parser.js")
        TextResource webParser();
    }

    /**
     * Web Extension adds JavaScript, HTML and CSS Support to IDE Application.
     * It provides syntax highlighting for CSS, JS, HTML files and code completion features for CSS files to IDE.
     */
    @Inject
    public WebExtension(ResourceProvider resourceProvider,
                        HtmlEditorProvider htmlEditorProvider,
                        JsEditorProvider jsEditorProvider,
                        CssEditorProvider cssEditorProvider,
                        EditorRegistry editorRegistry,
                        NewResourceAgent newResourceAgent,
                        CssFileProvider cssFileProvider,
                        JsFileProvider jsFileProvider,
                        HtmlFileProvider htmlFileProvider,
                        ParserResource res) {
        // Create and register new File types
        //CSS
        FileType cssFile = new FileType(null, MimeType.TEXT_CSS, "css");
        resourceProvider.registerFileType(cssFile);
        newResourceAgent.register(cssFileProvider);

        //JS
        FileType jsFile = new FileType(null, MimeType.TEXT_JAVASCRIPT, "js");
        resourceProvider.registerFileType(jsFile);
        newResourceAgent.register(jsFileProvider);

        //HTML
        FileType htmlFile = new FileType(null, MimeType.TEXT_HTML, "html");
        resourceProvider.registerFileType(htmlFile);
        newResourceAgent.register(htmlFileProvider);

        // register Editor Provider
        editorRegistry.register(cssFile, cssEditorProvider);
        editorRegistry.register(jsFile, jsEditorProvider);
        editorRegistry.register(htmlFile, htmlEditorProvider);
        Elements.injectJs(res.webParser().getText());
    }
}
