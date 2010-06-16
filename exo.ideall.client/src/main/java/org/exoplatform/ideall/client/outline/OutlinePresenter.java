/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.outline;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.event.EditorInitializedEvent;
import org.exoplatform.gwtframework.editor.event.EditorInitializedHandler;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ideall.client.editor.event.EditorGoToLineEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.vfs.api.File;

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlinePresenter implements EditorFileContentChangedHandler, EditorInitializedHandler,
EditorActiveFileChangedHandler
{
   interface Display
   {
      TreeGridItem<Token> getBrowserTree();

      void selectTreeItem(int number);
   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private Display display;

   public OutlinePresenter(HandlerManager bus, ApplicationContext applicationContext)
   {
      eventBus = bus;
      context = applicationContext;

      handlers = new Handlers(eventBus);

      handlers.addHandler(EditorFileContentChangedEvent.TYPE, this);
      handlers.addHandler(EditorInitializedEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);

   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getBrowserTree().addSelectionHandler(new SelectionHandler<Token>()
      {
         public void onSelection(SelectionEvent<Token> event)
         {
            int line = event.getSelectedItem().getLineNumber();
            int maxLineNumber = context.getActiveFile().getContent().split("\n").length;
            eventBus.fireEvent(new EditorGoToLineEvent(line < maxLineNumber ? line : maxLineNumber));
         }
      });
   }

   private void refreshOutline(TextEditor editor)
   {
      List<Token> tokens = editor.getTokenList();

      display.getBrowserTree().setValue(new Token("", null, -1, tokens));
   }

   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      if (isShowOutline())
      {
         refreshOutlineTimer.cancel();
         refreshOutlineTimer.schedule(2000);
      }
   }
   
   /*
    *  Fired when editor is initialized
    */
   public void onEditorInitialized(EditorInitializedEvent event)
   {
      //TODO: when open existed js file, show outline
   }
   
   private boolean isShowOutline()
   {
      return (context.getActiveTextEditor() != null && context.getActiveFile() != null 
          && (context.getActiveFile().getContentType().equals(MimeType.APPLICATION_JAVASCRIPT) 
                 || context.getActiveFile().getContentType().equals(MimeType.GOOGLE_GADGET)));
   }
   
   private Timer refreshOutlineTimer = new Timer() {
      @Override
      public void run()
      {
         refreshOutline(context.getActiveTextEditor());
      }
   };

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      File file = event.getFile();
      TextEditor editor = event.getEditor();
      if (editor != null && file != null && file.getContentType() != null
               && (file.getContentType().equals(MimeType.APPLICATION_JAVASCRIPT) 
               || file.getContentType().equals(MimeType.GOOGLE_GADGET)))
      {
         refreshOutline(editor);
      }
      else
      {
         display.getBrowserTree().setValue(new Token("", null, -1, null));
      }
   }

}
