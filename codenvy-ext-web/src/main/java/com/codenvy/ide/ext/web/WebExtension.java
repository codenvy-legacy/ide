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

import static com.codenvy.ide.api.action.IdeActions.GROUP_FILE_NEW;

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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

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
    /**
     * Web Extension adds JavaScript, HTML and CSS Support to IDE Application.
     * It provides syntax highlighting for CSS, JS, HTML files and code completion features for CSS files to IDE.
     */
    @Inject
    public WebExtension(HtmlEditorProvider htmlEditorProvider,
                        JsEditorProvider jsEditorProvider,
                        EditorRegistry editorRegistry,
                        WebExtensionResource resources,
                        IconRegistry iconRegistry,
                        @Named("JSFileType") FileType jsFile,
                        @Named("HTMLFileType") FileType htmlFile) {
        // register new Icon for javascript project type
        iconRegistry.registerIcon(new Icon("JavaScript.samples.category.icon", resources.samplesCategoryJs()));

        editorRegistry.registerDefaultEditor(jsFile, jsEditorProvider);
        editorRegistry.registerDefaultEditor(htmlFile, htmlEditorProvider);
    }

    @Inject
    private void registerFileTypes(FileTypeRegistry fileTypeRegistry,
                                   @Named("CSSFileType") FileType cssFile,
                                   @Named("LESSFileType") FileType lessFile,
                                   @Named("JSFileType") FileType jsFile,
                                   @Named("HTMLFileType") FileType htmlFile,
                                   @Named("PHPFileType") FileType phpFile) {
        fileTypeRegistry.registerFileType(cssFile);
        fileTypeRegistry.registerFileType(lessFile);
        fileTypeRegistry.registerFileType(jsFile);
        fileTypeRegistry.registerFileType(htmlFile);
        fileTypeRegistry.registerFileType(phpFile);
    }

    @Inject
    private void prepareActions(WebLocalizationConstant constant,
                                WebExtensionResource resources,
                                ActionManager actionManager,
                                NewCssFileAction newCssFileAction,
                                NewLessFileAction newLessFileAction,
                                NewHtmlFileAction newHtmlFileAction,
                                NewJavaScriptFileAction newJavaScriptFileAction) {
        // register actions
        actionManager.registerAction(constant.newCssFileActionId(), newCssFileAction);
        actionManager.registerAction(constant.newLessFileActionId(), newLessFileAction);
        actionManager.registerAction(constant.newHtmlFileActionId(), newHtmlFileAction);
        actionManager.registerAction(constant.newJavaScriptFileActionId(), newJavaScriptFileAction);

        // set icons
        newCssFileAction.getTemplatePresentation().setSVGIcon(resources.cssFile());
        newLessFileAction.getTemplatePresentation().setSVGIcon(resources.lessFile());
        newHtmlFileAction.getTemplatePresentation().setSVGIcon(resources.htmlFile());
        newJavaScriptFileAction.getTemplatePresentation().setSVGIcon(resources.jsFile());

        // add actions in main menu
        DefaultActionGroup newGroup = (DefaultActionGroup)actionManager.getAction(GROUP_FILE_NEW);
        newGroup.addSeparator();
        newGroup.add(newCssFileAction);
        newGroup.add(newLessFileAction);
        newGroup.add(newHtmlFileAction);
        newGroup.add(newJavaScriptFileAction);
    }
}
