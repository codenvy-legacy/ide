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
package com.codenvy.ide.ext.web;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.web.css.NewCssFileAction;
import com.codenvy.ide.ext.web.css.NewLessFileAction;
import com.codenvy.ide.ext.web.css.editor.CssEditorProvider;
import com.codenvy.ide.ext.web.html.NewHtmlFileAction;
import com.codenvy.ide.ext.web.html.editor.HtmlEditorProvider;
import com.codenvy.ide.ext.web.js.NewJavaScriptFileAction;
import com.codenvy.ide.ext.web.js.editor.JsEditorProvider;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_FILE_NEW;

/**
 * Extension add editing JavaScript, HTML, CSS css type support to the IDE Application.
 * It provides configured TextEditorView with {@link com.codenvy.ide.ext.web.css.editor.CssEditorProvider}
 * with syntax coloring and autocomplete.
 *
 * @author Nikolay Zamosenchuk
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
                        ParserResource res,
                        WebLocalizationConstant constant,
                        ActionManager actionManager,
                        NewCssFileAction newCssFileAction,
                        NewLessFileAction newLessFileAction,
                        NewHtmlFileAction newHtmlFileAction,
                        NewJavaScriptFileAction newJavaScriptFileAction) {
        // Register and add actions
        actionManager.registerAction(constant.newCssFileActionId(), newCssFileAction);
        actionManager.registerAction(constant.newLessFileActionId(), newLessFileAction);
        actionManager.registerAction(constant.newHtmlFileActionId(), newHtmlFileAction);
        actionManager.registerAction(constant.newJavaScriptFileActionId(), newJavaScriptFileAction);
        DefaultActionGroup newGroup = (DefaultActionGroup)actionManager.getAction(GROUP_FILE_NEW);
        newGroup.addSeparator();
        newGroup.add(newCssFileAction);
        newGroup.add(newLessFileAction);
        newGroup.add(newHtmlFileAction);
        newGroup.add(newJavaScriptFileAction);

        // Create and register new File types
        // CSS
        FileType cssFile = new FileType("CSS file", null, MimeType.TEXT_CSS, "css");
        resourceProvider.registerFileType(cssFile);

        // Also register .less files
        FileType lessFile = new FileType("Leaner CSS file", null, MimeType.TEXT_CSS, "less");
        resourceProvider.registerFileType(lessFile);

        // JS
        FileType jsFile = new FileType("javaScript", null, MimeType.TEXT_JAVASCRIPT, "js");
        resourceProvider.registerFileType(jsFile);

        // HTML
        FileType htmlFile = new FileType("HTML file", null, MimeType.TEXT_HTML, "html");
        resourceProvider.registerFileType(htmlFile);

        // register Editor Provider
        editorRegistry.register(cssFile, cssEditorProvider);
        editorRegistry.register(lessFile, cssEditorProvider);
        editorRegistry.register(jsFile, jsEditorProvider);
        editorRegistry.register(htmlFile, htmlEditorProvider);
        Elements.injectJs(res.webParser().getText());
    }
}
