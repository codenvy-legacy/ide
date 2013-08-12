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
package com.codenvy.ide.extension.css;

import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.WizardAgent;
import com.codenvy.ide.extension.css.editor.CssEditorProvider;
import com.codenvy.ide.extension.css.wizard.NewCSSFilePagePresenter;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Extension add CSS support to the IDE Application.
 * It porivdes configured TextEditorView with {@link CssEditorProvider} with syntax coloring and
 * autocomplete.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
@Extension(title = "Css Support : syntax highlighting and autocomplete.", version = "3.0.0")
public class CssExtension {
    public interface ParserResource extends ClientBundle {
        @Source("com/codenvy/ide/extension/css/css_parser.js")
        TextResource cssParser();
    }

    /**
     * CSS Extension adds CSS Support to IDE Applicaiton. It provides syntax highlighting and code completion features
     * for CSS files to IDE
     */
    @Inject
    public CssExtension(ResourceProvider resourceProvider, CssEditorProvider cssEditorProvider,
                        EditorRegistry editorRegistry, WizardAgent wizardAgent, Provider<NewCSSFilePagePresenter> provider,
                        CssExtensionResource resources, ParserResource res) {
        // Create and register new File type
        FileType cssFile = new FileType(null, "text/css", "css");
        resourceProvider.registerFileType(cssFile);
        wizardAgent.registerNewResourceWizard("General", "Css file", resources.file(), provider);

        // register Editor Provider
        editorRegistry.register(cssFile, cssEditorProvider);
        Elements.injectJs(res.cssParser().getText());
    }
}
