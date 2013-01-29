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
package org.exoplatform.ide.client.editor;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantEvent;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantHandler;
import org.exoplatform.ide.editor.client.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.client.api.event.EditorContextMenuEvent;
import org.exoplatform.ide.editor.client.api.event.EditorContextMenuHandler;
import org.exoplatform.ide.editor.client.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.client.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.client.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorFocusReceivedHandler;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedHandler;
import org.exoplatform.ide.editor.client.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorInitializedHandler;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberContextMenuEvent;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberContextMenuHandler;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class EditorEventHandler implements EditorInitializedHandler, EditorLineNumberContextMenuHandler,
   EditorContentChangedHandler, EditorCursorActivityHandler, EditorHotKeyPressedHandler, EditorFocusReceivedHandler,
   EditorContextMenuHandler, RunCodeAssistantHandler
{

   private Editor editor;

   private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();
   
   private boolean handleEvents = false;

   public EditorEventHandler(Editor editor)
   {
      this.editor = editor;
      
      handlers.add(editor.asWidget().addHandler(this, EditorInitializedEvent.TYPE));
      handlers.add(editor.asWidget().addHandler(this, EditorLineNumberContextMenuEvent.TYPE));
      handlers.add(editor.asWidget().addHandler(this, EditorContentChangedEvent.TYPE));
      handlers.add(editor.asWidget().addHandler(this, EditorCursorActivityEvent.TYPE));
      handlers.add(editor.asWidget().addHandler(this, EditorHotKeyPressedEvent.TYPE));
      handlers.add(editor.asWidget().addHandler(this, EditorFocusReceivedEvent.TYPE));
      handlers.add(editor.asWidget().addHandler(this, EditorContextMenuEvent.TYPE));
      handlers.add(editor.asWidget().addHandler(this, RunCodeAssistantEvent.TYPE));
   }

   public void removeHandlers()
   {
      for (HandlerRegistration handler : handlers)
      {
         handler.removeHandler();
      }

      handlers.clear();
   }
   
   public void enableHandling()
   {
      handleEvents = true;
   }
   
   public void disableHandling()
   {
      handleEvents = false;
   }

   @Override
   public void onEditorInitialized(EditorInitializedEvent event)
   {
      IDE.fireEvent(event);
      
      enableHandling();
   }

   @Override
   public void onEditorLineNumberContextMenu(EditorLineNumberContextMenuEvent event)
   {
      if (!handleEvents)
      {
         return;
      }
      
      IDE.fireEvent(event);
   }

   @Override
   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      if (!handleEvents)
      {
         return;
      }

      IDE.fireEvent(event);
   }

   @Override
   public void onEditorCursorActivity(EditorCursorActivityEvent event)
   {
      if (!handleEvents)
      {
         return;
      }
      
      IDE.fireEvent(event);
   }

   @Override
   public void onEditorHotKeyPressed(EditorHotKeyPressedEvent event)
   {
      if (!handleEvents)
      {
         return;
      }
      
      IDE.fireEvent(event);
   }

   @Override
   public void onEditorFocusReceived(EditorFocusReceivedEvent event)
   {
      if (!handleEvents)
      {
         return;
      }
      
      IDE.fireEvent(event);
   }

   @Override
   public void onEditorContextMenu(EditorContextMenuEvent event)
   {
      if (!handleEvents)
      {
         return;
      }
      
      IDE.fireEvent(event);
   }

   @Override
   public void onRunCodeAssistant(RunCodeAssistantEvent event)
   {
      if (!handleEvents)
      {
         return;
      }
      
      IDE.fireEvent(event);
   }

}
