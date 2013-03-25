/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.core.editor;

import com.codenvy.ide.editor.CodenvyTextEditor;
import com.codenvy.ide.editor.DocumentProvider;
import com.codenvy.ide.editor.EditorPartPresenter;
import com.codenvy.ide.editor.EditorProvider;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class DefaultEditorProvider implements EditorProvider
{

   private final DocumentProvider documentProvider;

   private Provider<CodenvyTextEditor> editorProvider;

   private final TextEditorConfiguration configuration = new TextEditorConfiguration();

   @Inject
   public DefaultEditorProvider(DocumentProvider documentProvider, Provider<CodenvyTextEditor> editorProvider)
   {
      super();
      this.documentProvider = documentProvider;
      this.editorProvider = editorProvider;
   }

   /**
    * @see com.codenvy.ide.editor.EditorProvider#getEditor()
    */
   @Override
   public EditorPartPresenter getEditor()
   {
      CodenvyTextEditor editor = editorProvider.get();
      editor.initialize(configuration, documentProvider);
      return editor;
   }

}
