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
package com.codenvy.ide.texteditor;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.outline.OutlineModel;
import com.codenvy.ide.api.outline.OutlinePresenter;
import com.codenvy.ide.editor.AbstractTextEditorPresenter;
import com.codenvy.ide.editor.DocumentProvider;
import com.codenvy.ide.editor.DocumentProvider.DocumentCallback;
import com.codenvy.ide.editor.SelectionProvider;
import com.codenvy.ide.outline.OutlineImpl;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.store.TextChange;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextListener;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class TextEditorPresenter extends AbstractTextEditorPresenter
{

   protected TextEditorViewImpl editor;

   private final TextListener textListener = new TextListener()
   {

      @Override
      public void onTextChange(TextChange textChange)
      {
         if (!isDirty())
         {
            updateDirtyState(true);
         }
      }
   };

   private Resources resources;

   private UserActivityManager userActivityManager;

   @Inject
   public TextEditorPresenter(Resources resources, UserActivityManager userActivityManager)
   {
      this.resources = resources;
      this.userActivityManager = userActivityManager;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void initializeEditor()
   {
      editor.configure(configuration);
      documentProvider.getDocument(input, new DocumentCallback()
      {

         @Override
         public void onDocument(Document document)
         {
            TextEditorPresenter.this.document = document;
            AnnotationModel annotationModel = documentProvider.getAnnotationModel(input);
            editor.setDocument(document, annotationModel);
            firePropertyChange(PROP_INPUT);
         }
      });
   }

   /**
    * @see com.codenvy.ide.editor.TextEditorPartPresenter#close(boolean)
    */
   @Override
   public void close(boolean save)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see com.codenvy.ide.editor.TextEditorPartPresenter#isEditable()
    */
   @Override
   public boolean isEditable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see com.codenvy.ide.editor.TextEditorPartPresenter#doRevertToSaved()
    */
   @Override
   public void doRevertToSaved()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see com.codenvy.ide.editor.TextEditorPartPresenter#getSelectionProvider()
    */
   @Override
   public SelectionProvider getSelectionProvider()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OutlinePresenter getOutline()
   {
      OutlineModel outlineModel = configuration.getOutline(editor);
      if (outlineModel != null)
      {
         OutlineImpl outline = new OutlineImpl(resources, outlineModel, editor, this);
         return outline;
      }
      else
      {
         return null;
      }
   }

   protected Widget getWidget()
   {
      HTML h = new HTML();
      h.getElement().appendChild(editor.getElement());
      return h;
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(getWidget());
   }

   /**
    * @see com.codenvy.ide.api.ui.perspective.PartPresenter#getTitleToolTip()
    */
   @Override
   public String getTitleToolTip()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void initialize(TextEditorConfiguration configuration, DocumentProvider documentProvider)
   {
      super.initialize(configuration, documentProvider);
      editor = new TextEditorViewImpl(resources, userActivityManager);
      editor.getTextListenerRegistrar().add(textListener);
   }
}
