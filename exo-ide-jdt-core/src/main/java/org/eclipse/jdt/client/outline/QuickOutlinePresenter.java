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

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.SingleSelectionModel;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.NodeFinder;
import org.eclipse.jdt.client.event.ShowQuickOutlineEvent;
import org.eclipse.jdt.client.event.ShowQuickOutlineHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.text.BadLocationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class QuickOutlinePresenter implements ShowQuickOutlineHandler, UpdateOutlineHandler,
   EditorActiveFileChangedHandler, EditorFileClosedHandler
{

   interface Display
   {

      /**
       * Returns the selection model of the Outline tree.
       * 
       * @return {@link SingleSelectionModel} selection model
       */
      SingleSelectionModel<ASTNode> getSingleSelectionModel();

      /**
       * Select node in Outline tree. It is also will be shown, if parent node is closed, then will be expanded.
       * 
       * @param node node to select
       */
      void selectNode(ASTNode node);

      HandlerRegistration addKeysHandler(Event.NativePreviewHandler handler);

      HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler);

      HasCloseHandlers<PopupPanel> getPanel();

      void close();

      /**
       * 
       */
      void setFocusInTree();

      HasValue<String> getFilterInput();

      QuickOutlineViewModel getViewModel();

      /**
       * 
       */
      void openAllNodes();

   }

   private Map<String, CompilationUnit> asts = new HashMap<String, CompilationUnit>();

   private String activeFileId;

   private Display display;

   private Editor editor;

   private HandlerRegistration keysHandler;

   private HandlerRegistration valueChangeHandler;

   /**
    * 
    */
   public QuickOutlinePresenter(HandlerManager eventBus)
   {
      eventBus.addHandler(ShowQuickOutlineEvent.TYPE, this);
      eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
   }

   /**
    * @see org.eclipse.jdt.client.event.ShowQuickOutlineHandler#onShowQuickOutline(org.eclipse.jdt.client.event.ShowQuickOutlineEvent)
    */
   @Override
   public void onShowQuickOutline(ShowQuickOutlineEvent event)
   {
      if (activeFileId == null)
         throw new IllegalStateException("Can't show Outline with null file");

      if (!asts.containsKey(activeFileId))
         throw new UnsupportedOperationException("Can't find Compilationt unit");
      CompilationUnit unit = asts.get(activeFileId);
      display = new QuickOutlineView(editor, unit);
      bind();
      try
      {
         NodeFinder nf = new NodeFinder(unit, editor.getDocument().getLineOffset(editor.getCursorRow() - 1), 0);

         ASTNode coveringNode = nf.getCoveringNode();
         if (coveringNode instanceof CompilationUnit)
            display.selectNode((ASTNode)unit.types().get(0));
         else
            display.selectNode(coveringNode);
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * 
    */
   private void bind()
   {
      keysHandler = display.addKeysHandler(new Event.NativePreviewHandler()
      {
         @Override
         public void onPreviewNativeEvent(NativePreviewEvent event)
         {

            switch (event.getNativeEvent().getKeyCode())
            {
               case KeyCodes.KEY_ESCAPE :
                  display.close();
                  break;
               case KeyCodes.KEY_DOWN :
               case KeyCodes.KEY_UP :
                  display.setFocusInTree();
                  break;
               case KeyCodes.KEY_ENTER :
                  event.cancel();
                  goToSelectedAstNode();
                  break;

               default :
                  break;
            }
         }
      });

      display.getPanel().addCloseHandler(new CloseHandler<PopupPanel>()
      {

         @Override
         public void onClose(CloseEvent<PopupPanel> event)
         {
            keysHandler.removeHandler();
            editor.setFocus();
            display = null;
            valueChangeHandler.removeHandler();
         }
      });

      display.addDoubleClickHandler(new DoubleClickHandler()
      {

         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            goToSelectedAstNode();
         }
      });

      valueChangeHandler = display.getFilterInput().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         private Timer timer = new Timer()
         {
            
            @Override
            public void run()
            {
               display.getViewModel().filter(filter);
               display.openAllNodes();               
            }
         };
         
         private String filter;
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            try
            {
               if (event.getValue() == null || event.getValue().isEmpty())
               {
                  display.getViewModel().removeFilter();
                  display.selectNode((ASTNode)asts.get(activeFileId).types().get(0));
               }
               else
               {
                  filter = event.getValue();
                  timer.cancel();
                  timer.schedule(500);
               }
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      });
   }

   /**
    * 
    */
   private void goToSelectedAstNode()
   {
      ASTNode node = display.getSingleSelectionModel().getSelectedObject();
      try
      {
         editor.goToPosition(editor.getDocument().getLineOfOffset(node.getStartPosition()) + 1, 1);
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
      display.close();
   }

   /**
    * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
    */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      asts.put(event.getFile().getId(), event.getCompilationUnit());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      asts.remove(event.getFile().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() != null)
      {
         activeFileId = event.getFile().getId();
         editor = event.getEditor();
      }
      else
         activeFileId = null;
   }
}
