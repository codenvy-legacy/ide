/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.extension.css;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.WizardAgent;
import com.codenvy.ide.core.editor.EditorRegistry;
import com.codenvy.ide.extension.Extension;
import com.codenvy.ide.resources.FileType;
import com.codenvy.ide.texteditor.TextEditorViewImpl;

import com.codenvy.ide.extension.css.editor.CssEditorProvider;
import com.codenvy.ide.extension.css.wizard.NewCSSFilePagePresenter;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Extension add CSS support to the IDE Application.
 * It porivdes configured {@link TextEditorViewImpl} with {@link CssEditorProvider} with syntax coloring and
 * autocomplete.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
@Extension(title = "Css Support : syntax highlighting and autocomplete.", version = "2.0.0")
public class CssExtension
{

   /**
    * CSS Extension adds CSS Support to IDE Applicaiton. It provides syntax highlighting and code completion features 
    * for CSS files to IDE
    * 
    * @param resourceProvider
    * @param cssEditorProvider
    * @param editorRegistry
    */
   @Inject
   public CssExtension(ResourceProvider resourceProvider, CssEditorProvider cssEditorProvider,
      EditorRegistry editorRegistry, WizardAgent wizardAgent, Provider<NewCSSFilePagePresenter> provider,
      Resources resources)
   {
      // Create and register new File type
      FileType cssFile = new FileType(null, "text/css", "css");
      resourceProvider.registerFileType(cssFile);
      wizardAgent.registerNewResourceWizard("General", "Css file", resources.file(), provider);

      // register Editor Provider
      editorRegistry.register(cssFile, cssEditorProvider);
   }
}
