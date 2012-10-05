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
package org.exoplatform.ide.texteditor;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.AppContext;
import org.exoplatform.ide.editor.DocumentProvider;
import org.exoplatform.ide.editor.DocumentProvider.DocumentCallback;
import org.exoplatform.ide.editor.EditorInitException;
import org.exoplatform.ide.editor.EditorInput;
import org.exoplatform.ide.editor.SelectionProvider;
import org.exoplatform.ide.editor.TextEditorPartPresenter;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.DocumentImpl;
import org.exoplatform.ide.texteditor.api.TextEditorConfiguration;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class BaseTextEditor implements TextEditorPartPresenter
{

   protected TextEditorPartDisplay editor;

   protected final DocumentProvider documentProvider;

   protected EditorInput input;

   /**
    * @param documentProvider 
    * 
    */
   public BaseTextEditor(AppContext appContext, DocumentProvider documentProvider, TextEditorConfiguration configuration)
   {
      this.documentProvider = documentProvider;
      editor = new Editor(appContext);
      editor.configure(configuration);
   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#init(org.exoplatform.ide.editor.api.EditorSite, org.exoplatform.ide.editor.EditorInput)
    */
   @Override
   public void init(EditorInput input) throws EditorInitException
   {
      documentProvider.getDocument(input, new DocumentCallback()
      {

         @Override
         public void onDocument(Document document)
         {
            editor.setDocument((DocumentImpl)document);
         }
      });
      this.input = input;
   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#doSave()
    */
   @Override
   public void doSave()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#doSaveAs()
    */
   @Override
   public void doSaveAs()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#isDirty()
    */
   @Override
   public boolean isDirty()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#isSaveAsAllowed()
    */
   @Override
   public boolean isSaveAsAllowed()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#isSaveOnCloseNeeded()
    */
   @Override
   public boolean isSaveOnCloseNeeded()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#getDocumentProvider()
    */
   @Override
   public DocumentProvider getDocumentProvider()
   {
      return documentProvider;
   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#close(boolean)
    */
   @Override
   public void close(boolean save)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#isEditable()
    */
   @Override
   public boolean isEditable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#doRevertToSaved()
    */
   @Override
   public void doRevertToSaved()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#getSelectionProvider()
    */
   @Override
   public SelectionProvider getSelectionProvider()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#selectAndReveal(int, int)
    */
   @Override
   public void selectAndReveal(int offset, int length)
   {
      // TODO Auto-generated method stub

   }

   protected Widget getWidget()
   {
      HTML h = new HTML();
      h.getElement().appendChild(editor.getElement());
      return h;
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#getTitle()
    */
   @Override
   public String getTitle()
   {
      return input.getName();
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#close()
    */
   @Override
   public boolean close()
   {
      return true;
   }

   /**
    * @see org.exoplatform.ide.presenter.Presenter#go(com.google.gwt.user.client.ui.HasWidgets)
    */
   @Override
   public void go(HasWidgets container)
   {
      container.add(getWidget());
   }

}
