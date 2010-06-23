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

import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.event.EditorContentChangedEvent;
import org.exoplatform.gwtframework.editor.event.EditorContentChangedHandler;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.editor.event.EditorGoToLineEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.vfs.api.File;

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
public class OutlinePresenter implements EditorActiveFileChangedHandler, EditorContentChangedHandler
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

      handlers.addHandler(EditorContentChangedEvent.TYPE, this);
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

   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      if (isShowOutline(context.getActiveTextEditor(), context.getActiveFile()))
      {
         refreshOutlineTimer.cancel();
         refreshOutlineTimer.schedule(2000);
      }
   }

   private boolean isShowOutline(TextEditor editor, File file)
   {
      if (editor == null || file == null || file.getContentType() == null)
      {
         return false;
      }
      
      if (file.getContentType().equals(MimeType.APPLICATION_JAVASCRIPT) 
               || file.getContentType().equals(MimeType.GOOGLE_GADGET))
      {
         return true;
      }
      else
      {
         return false;
      }
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
      if (isShowOutline(editor, file))
      {
         refreshOutline(editor);
      }
      else
      {
         display.getBrowserTree().setValue(new Token("", null));
      }
   }
}
