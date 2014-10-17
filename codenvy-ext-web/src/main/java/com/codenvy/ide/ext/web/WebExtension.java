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

import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.icon.Icon;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.ext.web.css.NewCssFileAction;
import com.codenvy.ide.ext.web.css.NewLessFileAction;
import com.codenvy.ide.ext.web.html.NewHtmlFileAction;
import com.codenvy.ide.ext.web.html.editor.HtmlEditorProvider;
import com.codenvy.ide.ext.web.js.NewJavaScriptFileAction;
import com.codenvy.ide.ext.web.js.editor.JsEditorProvider;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.vectomatic.dom.svg.ui.SVGResource;

import static com.codenvy.ide.api.action.IdeActions.GROUP_FILE_NEW;

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

        @Source("css.svg")
        SVGResource cssFile();

        @Source("less.svg")
        SVGResource lessFile();

        @Source("html.svg")
        SVGResource htmlFile();

        @Source("js.svg")
        SVGResource jsFile();

        @Source("php.svg")
        SVGResource phpFile();

        @Source("category/js.svg")
        SVGResource samplesCategoryJs();
    }

    /**
     * Web Extension adds JavaScript, HTML and CSS Support to IDE Application.
     * It provides syntax highlighting for CSS, JS, HTML files and code completion features for CSS files to IDE.
     */
    @Inject
    public WebExtension(FileTypeRegistry fileTypeRegistry,
                        HtmlEditorProvider htmlEditorProvider,
                        JsEditorProvider jsEditorProvider,
                        EditorRegistry editorRegistry,
                        ParserResource res,
                        WebLocalizationConstant constant,
                        ActionManager actionManager,
                        NewCssFileAction newCssFileAction,
                        NewLessFileAction newLessFileAction,
                        NewHtmlFileAction newHtmlFileAction,
                        WebExtension.ParserResource resources,
                        IconRegistry iconRegistry,
                        NewJavaScriptFileAction newJavaScriptFileAction,
                        @Named("CSSFileType") FileType cssFile,
                        @Named("LESSFileType") FileType lessFile,
                        @Named("JSFileType") FileType jsFile,
                        @Named("HTMLFileType") FileType htmlFile,
                        @Named("PHPFileType") FileType phpFile) {
        // register new Icon for javascript projecttype
        iconRegistry.registerIcon(new Icon("JavaScript.samples.category.icon", resources.samplesCategoryJs()));

        // Register and add actions
        actionManager.registerAction(constant.newCssFileActionId(), newCssFileAction);
        newCssFileAction.getTemplatePresentation().setSVGIcon(res.cssFile());
        actionManager.registerAction(constant.newLessFileActionId(), newLessFileAction);
        newLessFileAction.getTemplatePresentation().setSVGIcon(res.lessFile());
        actionManager.registerAction(constant.newHtmlFileActionId(), newHtmlFileAction);
        newHtmlFileAction.getTemplatePresentation().setSVGIcon(res.htmlFile());
        actionManager.registerAction(constant.newJavaScriptFileActionId(), newJavaScriptFileAction);
        newJavaScriptFileAction.getTemplatePresentation().setSVGIcon(res.jsFile());
        DefaultActionGroup newGroup = (DefaultActionGroup)actionManager.getAction(GROUP_FILE_NEW);
        newGroup.addSeparator();
        newGroup.add(newCssFileAction);
        newGroup.add(newLessFileAction);
        newGroup.add(newHtmlFileAction);
        newGroup.add(newJavaScriptFileAction);

        // Register new File types
        // CSS
        fileTypeRegistry.registerFileType(cssFile);

        // Also register .less files
        fileTypeRegistry.registerFileType(lessFile);

        // JS
        fileTypeRegistry.registerFileType(jsFile);

        // HTML
//        FileType htmlFile = new FileType("HTML file", res.htmlFile(), MimeType.TEXT_HTML, "html");
        fileTypeRegistry.registerFileType(htmlFile);

        fileTypeRegistry.registerFileType(phpFile);

        // register Editor Provider
        editorRegistry.registerDefaultEditor(jsFile, jsEditorProvider);
        editorRegistry.registerDefaultEditor(htmlFile, htmlEditorProvider);
        Elements.injectJs(res.webParser().getText());
    }
}
