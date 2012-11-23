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
package org.exoplatform.ide.extension.css;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.editor.EditorRegistry;
import org.exoplatform.ide.extension.Extension;
import org.exoplatform.ide.extension.css.editor.CssEditorProvider;
import org.exoplatform.ide.resources.FileType;
import org.exoplatform.ide.texteditor.TextEditorView;

/**
 * Extension add CSS support to the IDE Application.
 * It porivdes configured {@link TextEditorView} with {@link CssEditorProvider} with syntax coloring and
 * autocomplete.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
@Extension(title = "Css Support : syntax highlighting and autocomplete.", id = "ide.ext.css", version = "2.0.0")
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
      EditorRegistry editorRegistry)
   {
      // Create and register new File type
      FileType cssFile = new FileType(null, "text/css", "css");
      resourceProvider.registerFileType(cssFile);

      // register Editor Provider
      editorRegistry.register(cssFile, cssEditorProvider);
   }
}
