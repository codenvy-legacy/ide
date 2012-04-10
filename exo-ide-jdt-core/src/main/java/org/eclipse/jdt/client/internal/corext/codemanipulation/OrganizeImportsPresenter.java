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
package org.eclipse.jdt.client.internal.corext.codemanipulation;

import com.google.gwt.user.client.ui.HasText;

import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.view.client.HasData;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.shared.HandlerManager;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.ISourceRange;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.search.TypeNameMatch;
import org.eclipse.jdt.client.event.OrganizeImportsEvent;
import org.eclipse.jdt.client.event.OrganizeImportsHandler;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsOperation.IChooseImportQuery;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsOperation.ITextEditCallback;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsOperation.TypeNameMatchCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.text.edits.TextEdit;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OrganizeImportsPresenter implements UpdateOutlineHandler, OrganizeImportsHandler,
   EditorActiveFileChangedHandler
{
   public interface Display extends IsView
   {
      String ID = "ideOrganizeImportsView";
      
      HasClickHandlers getBackButton();
      
      HasClickHandlers getNextButton();
      
      HasClickHandlers getCancelButton();
      
      HasClickHandlers getFinishButton();
      
      HasData<TypeNameMatch> getTypeList();
      
      HasValue<String> getFilterInput();
      
      HasText getPageLabel();
   }

   private HandlerManager eventBus;

   private FileModel activeFile;

   private CompilationUnit compilationUnit;

   private Editor editor;

   private TypeNameMatchCallback callback;

   private TypeNameMatch[][] openChoices;

   private Display display;

   /**
    * @param event
    */
   public OrganizeImportsPresenter(HandlerManager eventBus)
   {
      super();
      this.eventBus = eventBus;
      eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
      eventBus.addHandler(OrganizeImportsEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
    */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      activeFile = event.getFile();
      compilationUnit = event.getCompilationUnit();
   }

   /**
    * @see org.eclipse.jdt.client.event.OrganizeImportsHandler#onOrganizeImports(org.eclipse.jdt.client.event.OrganizeImportsEvent)
    */
   @Override
   public void onOrganizeImports(OrganizeImportsEvent event)
   {
      if (editor == null)
         return;
      OrganizeImportsOperation operation =
         new OrganizeImportsOperation(editor.getDocument(), compilationUnit, false, true, true,
            new IChooseImportQuery()
            {

               @Override
               public void chooseImports(TypeNameMatch[][] openChoices, ISourceRange[] ranges,
                  TypeNameMatchCallback callback)
               {
                  OrganizeImportsPresenter.this.callback = callback;
                  OrganizeImportsPresenter.this.openChoices = openChoices;
                  showForm();
               }

            }, activeFile.getProject().getId());
      operation.createTextEdit(new ITextEditCallback()
      {

         @Override
         public void textEditCreated(TextEdit edit)
         {
            try
            {
               edit.apply(editor.getDocument());
            }
            catch (MalformedTreeException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            catch (BadLocationException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      });
   }

   private void showForm()
   {
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA))
      {
         editor = event.getEditor();
      }
      else
         editor = null;
   }

}
