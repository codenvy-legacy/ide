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
package org.exoplatform.ideall.client.hotkeys;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.event.EditorHotKeyCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorHotKeyCalledHandler;
import org.exoplatform.gwtframework.ui.client.component.command.Command;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleCommand;
import org.exoplatform.ideall.client.common.command.file.CreateNewFolderCommand;
import org.exoplatform.ideall.client.common.command.file.SearchFilesCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.CreateFileFromTemplateCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewGroovyFileCommand;
import org.exoplatform.ideall.client.common.command.help.ShowAboutCommand;
import org.exoplatform.ideall.client.common.command.window.SelectWorkspaceCommand;
import org.exoplatform.ideall.client.model.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class HotKeyManagerImpl extends HotKeyManager implements EditorHotKeyCalledHandler
{
   private static final class WindowCloseHandlerImpl implements ClosingHandler
   {
      public native void onWindowClosing(ClosingEvent event) /*-{
            $doc.onkeydown = null; 
      }-*/;

      private native void init() /*-{
            $doc.onkeydown = function(evt) { 
                  var hotKeyNamager = @org.exoplatform.ideall.client.hotkeys.HotKeyManager::getInstance()();
                  hotKeyNamager.@org.exoplatform.ideall.client.hotkeys.HotKeyManager::onKeyDown(Lcom/google/gwt/user/client/Event;)(evt || $wnd.event);
            } 
                  
      }-*/;
   }
   
   private HotKeyPressedListener hotKeyPressedListener;
   
   private HandlerManager eventBus;  
   
   private ApplicationContext context;
   
   private Map<String, String> controls = new HashMap<String, String>();
   
   private Handlers handlers;
   
   public HotKeyManagerImpl(HandlerManager bus, ApplicationContext applicationContext)
   {
      eventBus = bus;
      context = applicationContext;
      
      final WindowCloseHandlerImpl closeListener = new WindowCloseHandlerImpl();
      Window.addWindowClosingHandler(closeListener);
      closeListener.init();
      
      initDefaultHotKeys();
      
      handlers = new Handlers(eventBus);
      handlers.addHandler(EditorHotKeyCalledEvent.TYPE, this);
   }
   
   //This method is not unused but called by the javaScript function : WindowCloseHandlerImpl::Init
   public void onKeyDown(final Event event)
   {
      if (hotKeyPressedListener != null) {
         hotKeyCustomizing(event);
      } else {
         hotKeyPressed(event);
      }
   }
   
   private void hotKeyPressed(final Event event)
   {
      int keyCode = DOM.eventGetKeyCode(event);
      String controlKey = null;
      if (event.getCtrlKey() && !event.getAltKey()) controlKey = "Ctrl";
      if (event.getAltKey() && !event.getCtrlKey()) controlKey = "Alt";
      if (controlKey == null) return;
      
      String hotKey = controlKey + "+" + String.valueOf(keyCode);
      
      if (!context.getHotKeys().containsKey(hotKey))
         return;
      
      callEventByHotKey(hotKey);
      event.preventDefault();
      
   }
   
   public void setHotKeyPressedListener(HotKeyPressedListener listener) 
   {
      hotKeyPressedListener = listener;
   }
   
   private void hotKeyCustomizing(final Event event)
   {
      int keyCode = DOM.eventGetKeyCode(event);
      
      String controlKey = null;
      if (event.getCtrlKey()) controlKey = "Ctrl";
      if (event.getAltKey()) controlKey = "Alt";
      
      if (controlKey == null)
      {
         hotKeyPressedListener.onHotKeyPressed("");
         event.preventDefault();
         return;
      }
      
      String stringHotKey = controlKey + "+";
      
      if (keyCode != 17 && keyCode != 18 
               && HotKeyHelper.convertKeyCodeToKeySymbol(String.valueOf(keyCode)) != null)
      {
         stringHotKey += HotKeyHelper.convertKeyCodeToKeySymbol(String.valueOf(keyCode));
      }
      
      hotKeyPressedListener.onHotKeyPressed(stringHotKey);      
      event.preventDefault();
   }

   public void onKeyPress(final Event event)
   {
   }

   public void onKeyUp(final Event event)
   {
   }

   private void initDefaultHotKeys() {
      controls.put("Ctrl+70", CreateNewFolderCommand.ID);
      controls.put("Ctrl+72", ShowAboutCommand.ID);
      controls.put("Ctrl+87", SelectWorkspaceCommand.ID);
      controls.put("Ctrl+83", SearchFilesCommand.ID);
      controls.put("Alt+84", CreateFileFromTemplateCommand.ID);
      controls.put("Ctrl+88", NewGroovyFileCommand.ID);
      context.setHotKeys(controls);      
   }

   public void onEditorHotKeyCalled(EditorHotKeyCalledEvent event)
   {
      callEventByHotKey(event.getHotKey());
   }
   
   private void callEventByHotKey(String hotKey)
   {
      for (Command command : context.getCommands())
      {
         if (command instanceof SimpleCommand && ((SimpleCommand)command).getEvent() != null
                  && command.getId().equals(context.getHotKeys().get(hotKey))
                  && command.isEnabled())
         {
            eventBus.fireEvent(((SimpleCommand)command).getEvent());
            return;
         }
      }
   }

}
