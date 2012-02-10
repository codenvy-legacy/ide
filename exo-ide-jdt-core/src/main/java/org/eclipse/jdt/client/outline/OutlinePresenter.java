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
package org.eclipse.jdt.client.outline;

import com.google.gwt.user.client.Timer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;

import java.util.List;

/**
 * Presenter for Java Outline View.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 4:29:06 PM anya $
 * 
 */
public class OutlinePresenter implements UpdateOutlineHandler, ViewClosedHandler, EditorCursorActivityHandler,
   EditorActiveFileChangedHandler
{
   interface Display extends IsView
   {
      void updateOutline(CompilationUnit cUnit);

      SingleSelectionModel<Object> getSingleSelectionModel();

      void selectNode(ASTNode node);

      void focusInTree();

      List<Object> getNodes();

      void openNode(Object object);
   }

   /**
    * Display.
    */
   private Display display;

   private CompilationUnit compilationUnit;

   private boolean processEditorActivity = true;

   private int currentRow = -1;

   private Editor currentEditor;

   private Timer selectOutlineTimer = new Timer()
   {
      @Override
      public void run()
      {
         if (compilationUnit != null)
         {
            selectToken(currentRow);
            currentEditor.setFocus();
         }
      }
   };

   public OutlinePresenter()
   {
      IDE.addHandler(UpdateOutlineEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(EditorCursorActivityEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getSingleSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler()
      {

         @Override
         public void onSelectionChange(SelectionChangeEvent event)
         {
            if (display.getSingleSelectionModel().getSelectedObject() instanceof ASTNode)
            {
               ASTNode node = ((ASTNode)display.getSingleSelectionModel().getSelectedObject());
               selectEditorLine(compilationUnit.getLineNumber(node.getStartPosition()));
            }
         }
      });
   }

   /**
    * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
    */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      compilationUnit = event.getCompilationUnit();
      display.updateOutline(event.getCompilationUnit());
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler#onEditorCursorActivity(org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent)
    */
   @Override
   public void onEditorCursorActivity(EditorCursorActivityEvent event)
   {
      if (display == null)
      {
         return;
      }
      if (!processEditorActivity)
      {
         display.focusInTree();
         processEditorActivity = true;
         return;
      }

      if (currentRow == event.getRow())
      {
         return;
      }
      currentRow = event.getRow();
      selectOutlineTimer.cancel();
      selectOutlineTimer.schedule(100);
   }

   public void selectEditorLine(int line)
   {
      processEditorActivity = false;
      IDE.fireEvent(new EditorGoToLineEvent(line));
   }

   protected void selectToken(int lineNumber)
   {
      // TODO
      compilationUnit.getPosition(lineNumber, 0);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() != null && MimeType.APPLICATION_JAVA.equals(event.getFile().getMimeType()))
      {
         this.currentEditor = event.getEditor();
         if (display == null)
         {
            display = GWT.create(Display.class);
            bindDisplay();
         }
         IDE.getInstance().openView(display.asView());
      }
      else
      {
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      }
   }
}
