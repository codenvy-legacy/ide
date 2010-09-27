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
package org.exoplatform.ide.client.hotkeys;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.event.EditorHotKeyCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorHotKeyCalledHandler;
import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysEvent;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysHandler;
import org.exoplatform.ide.client.module.edit.control.FindTextCommand;
import org.exoplatform.ide.client.module.edit.control.GoToLineControl;
import org.exoplatform.ide.client.module.navigation.control.DeleteLineControl;
import org.exoplatform.ide.client.module.navigation.control.SaveFileCommand;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateFileFromTemplateControl;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class HotKeyManagerImpl extends HotKeyManager implements EditorHotKeyCalledHandler, RefreshHotKeysHandler
{

   private static final class WindowCloseHandlerImpl implements ClosingHandler
   {
      public native void onWindowClosing(ClosingEvent event) /*-{
       $doc.onkeydown = null; 
       }-*/;

      private native void init() /*-{
         $doc.onkeydown = function(evt) { 
         var hotKeyNamager = @org.exoplatform.ide.client.hotkeys.HotKeyManager::getInstance()();
         hotKeyNamager.@org.exoplatform.ide.client.hotkeys.HotKeyManager::onKeyDown(Lcom/google/gwt/user/client/Event;)(evt || $wnd.event);
         }                        
         }-*/;
   }

   private HotKeyPressedListener hotKeyPressedListener;

   private HandlerManager eventBus;

   private Handlers handlers;

   private Map<String, String> hotKeys;

   private List<Control> registeredControls = new ArrayList<Control>();

   public HotKeyManagerImpl(HandlerManager eventBus, List<Control> registeredControls,
      ApplicationSettings applicationSettings)
   {
      this.eventBus = eventBus;
      this.registeredControls = registeredControls;

      hotKeys = applicationSettings.getValueAsMap("hotkeys");

      if (hotKeys == null)
      {
         hotKeys = new LinkedHashMap<String, String>();
         applicationSettings.setValue("hotkeys", hotKeys, Store.REGISTRY);
         initDefaultHotKeys();
      }

      final WindowCloseHandlerImpl closeListener = new WindowCloseHandlerImpl();
      Window.addWindowClosingHandler(closeListener);
      closeListener.init();

      handlers = new Handlers(eventBus);
      handlers.addHandler(EditorHotKeyCalledEvent.TYPE, this);
      handlers.addHandler(RefreshHotKeysEvent.TYPE, this);

      refreshHotKeys();
   }

   private void refreshHotKeys()
   {
      new Timer()
      {
         @Override
         public void run()
         {
            eventBus.fireEvent(new RefreshHotKeysEvent(hotKeys));
         }
      }.schedule(1000);
   }

   //This method is not unused but called by the javaScript function : WindowCloseHandlerImpl::Init
   public void onKeyDown(final Event event)
   {
      if (hotKeyPressedListener != null)
      {
         hotKeyCustomizing(event);
      }
      else
      {
         hotKeyPressed(event);
      }
   }

   private void hotKeyPressed(final Event event)
   {
      int keyCode = DOM.eventGetKeyCode(event);
      String controlKey = null;
      if (event.getCtrlKey() && !event.getAltKey())
         controlKey = "Ctrl";
      if (event.getAltKey() && !event.getCtrlKey())
         controlKey = "Alt";
      if (controlKey == null)
         return;

      String hotKey = controlKey + "+" + String.valueOf(keyCode);
      if (!hotKeys.containsKey(hotKey))
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

      hotKeyPressedListener.onHotKeyPressed(event.getCtrlKey(), event.getAltKey(), 
         DOM.eventGetKeyCode(event));
      event.preventDefault();
   }

   public void onKeyPress(final Event event)
   {
   }

   public void onKeyUp(final Event event)
   {
   }

   private void initDefaultHotKeys()
   {
      //      controls.put("Ctrl+90", UndoTypingCommand.ID);  //Ctrl+Z
      //      controls.put("Ctrl+89", RedoTypingCommand.ID);  //Ctrl+Y
      //      controls.put("Ctrl+67", CopyItemsCommand.ID);   //Ctrl+C
      //      controls.put("Ctrl+86", PasteItemsCommand.ID);  //Ctrl+V

      hotKeys.clear();

      hotKeys.put("Ctrl+83", SaveFileCommand.ID); //Ctrl+S
      hotKeys.put("Ctrl+70", FindTextCommand.ID); //Ctrl+F
      hotKeys.put("Ctrl+68", DeleteLineControl.ID); //Ctrl+D
      hotKeys.put("Ctrl+76", GoToLineControl.ID); //Ctrl+L
      hotKeys.put("Ctrl+78", CreateFileFromTemplateControl.ID); //Ctrl+N

      hotKeys.putAll(hotKeys);
   }

   public void onEditorHotKeyCalled(EditorHotKeyCalledEvent event)
   {
      callEventByHotKey(event.getHotKey());
   }

   /**
    * @param hotKey
    */
   private void callEventByHotKey(String hotKey)
   {
      for (Control command : registeredControls)
      {
         if (command instanceof SimpleControl && ((SimpleControl)command).getEvent() != null
            && command.getId().equals(hotKeys.get(hotKey))
            && (command.isEnabled() || ((SimpleControl)command).isIgnoreDisable()))
         {
            eventBus.fireEvent(((SimpleControl)command).getEvent());
            return;
         }
      }
   }

   public void onRefreshHotKeys(RefreshHotKeysEvent event)
   {
      /*
       * Clear old values
       */
      for (Control control : registeredControls)
      {
         if (!(control instanceof SimpleControl)) {
            continue;
         }
         
         SimpleControl simpleControl = (SimpleControl)control;         
         simpleControl.setHotKey(null);
      }

      Iterator<String> keyIter = event.getHotKeys().keySet().iterator();
      while (keyIter.hasNext())
      {
         String key = keyIter.next();
         String controlId = event.getHotKeys().get(key);

         Control control = getControl(controlId);
         if (control == null || !(control instanceof SimpleControl))
         {
            continue;
         }
         
         SimpleControl simpleControl = (SimpleControl)control;

         String k = HotKeyHelper.convertToStringCombination(key);
         simpleControl.setHotKey(k);
      }
   }

   private Control getControl(String controlId)
   {
      for (Control control : registeredControls)
      {
         if (control.getId().equals(controlId))
         {
            return control;
         }
      }

      return null;
   }

}
