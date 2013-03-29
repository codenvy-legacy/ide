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
package com.codenvy.ide.java.client.editor;

import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;

import com.codenvy.ide.Resources;
import com.codenvy.ide.java.client.JavaClientBundle;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class JavaEditorProvider implements EditorProvider
{

   private final DocumentProvider documentProvider;

   private final Resources resources;

   private final UserActivityManager activityManager;

   private Provider<CodenvyTextEditor> editorProvider;

   /**
    * @param resources
    * @param activityManager
    */
   @Inject
   public JavaEditorProvider(Resources resources, UserActivityManager activityManager,
      Provider<CodenvyTextEditor> editorProvider, DocumentFactory documentFactory)
   {
      super();
      this.resources = resources;
      this.activityManager = activityManager;
      this.editorProvider = editorProvider;
      this.documentProvider = new CompilationUnitDocumentProvider(resources.workspaceEditorCss(), documentFactory);
   }

   /**
    * @see com.codenvy.ide.api.editor.EditorProvider#getEditor()
    */
   @Override
   public EditorPartPresenter getEditor()
   {
      CodenvyTextEditor textEditor = editorProvider.get();
      textEditor.initialize(new JavaEditorConfiguration(activityManager, JavaClientBundle.INSTANCE, textEditor),
         documentProvider);
      return textEditor;
   }

}
