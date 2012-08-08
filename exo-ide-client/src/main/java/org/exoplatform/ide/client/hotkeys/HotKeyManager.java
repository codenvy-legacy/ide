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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 * 
 */
public class HotKeyManager implements EditorHotKeyPressedHandler
{

   /**
    * Instance of {@link HotKeyManager}
    */
   private static HotKeyManager instance;

   /**
    * Returns instance of {@link HotKeyManager}
    * 
    * @return
    */
   public static HotKeyManager getInstance()
   {
      return instance;
   }

//   private static final class WindowCloseHandlerImpl implements ClosingHandler
//   {
//      public native void onWindowClosing(ClosingEvent event)
//      /*-{
//         $doc.onkeydown = null; 
//      }-*/;
//   }
   
   private native void setKeyDownHandler()
   /*-{
      $doc.onkeydown = function(ev)
      { 
         var hotKeyNamager = @org.exoplatform.ide.client.hotkeys.HotKeyManager::getInstance()();
         hotKeyNamager.@org.exoplatform.ide.client.hotkeys.HotKeyManager::onKeyDown(Lcom/google/gwt/user/client/Event;)(ev || $wnd.event);                  
      }                              
   }-*/;

   private HotKeyPressedListener hotKeyPressedListener;

   private Map<String, String> hotKeyMap;

   private Map<String, Control<?>> controlsMap = new LinkedHashMap<String, Control<?>>();

   private ApplicationSettings applicationSettings;

   public HotKeyManager(List<Control> controls, ApplicationSettings applicationSettings)
   {
      instance = this;

      for (Control control : controls)
      {
         controlsMap.put(control.getId(), control);
      }

      this.applicationSettings = applicationSettings;

      IDE.addHandler(EditorHotKeyPressedEvent.TYPE, this);

//      final WindowCloseHandlerImpl closeListener = new WindowCloseHandlerImpl();
//      Window.addWindowClosingHandler(closeListener);
//      setKeyDownHandler();
      Event.addNativePreviewHandler(new NativePreviewHandler()
      {
         @Override
         public void onPreviewNativeEvent(NativePreviewEvent event)
         {
            if(event.getTypeInt() != Event.ONKEYDOWN)
               return;
            onKeyDown(Event.as(event.getNativeEvent()));
         }
      });
      
      hotKeyMap = applicationSettings.getValueAsMap("hotkeys");
      if (hotKeyMap == null)
      {
         initDefaultHotKeys();
      }
      else
      {
         storeHotKeysToControls();
      }
   }

   public void setHotKeyPressedListener(HotKeyPressedListener listener)
   {
      hotKeyPressedListener = listener;
   }

   private void storeHotKeysToControls()
   {
      /*
       * Clear old HotKey values
       */
      for (Control control : controlsMap.values())
      {
         if (!(control instanceof SimpleControl))
         {
            continue;
         }

         SimpleControl simpleControl = (SimpleControl)control;
         simpleControl.setHotKey(null);
      }

      /*
       * Set new hotkeys
       */
      for (String key : hotKeyMap.keySet())
      {
         String value = hotKeyMap.get(key);
         Control control = controlsMap.get(value);
         if (control == null || !(control instanceof SimpleControl))
         {
            continue;
         }

         SimpleControl simpleControl = (SimpleControl)control;
         simpleControl.setHotKey(key);
      }
   }

   private void initDefaultHotKeys()
   {
      hotKeyMap = new LinkedHashMap<String, String>();
      applicationSettings.setValue("hotkeys", hotKeyMap, Store.SERVER);

      for (Control<?> control : controlsMap.values())
      {
         if (control instanceof SimpleControl && ((SimpleControl)control).getHotKey() != null
            && !((SimpleControl)control).getHotKey().isEmpty())
         {
            hotKeyMap.put(((SimpleControl)control).getHotKey(), ((SimpleControl)control).getId());
         }
      }
   }

   @Override
   public void onEditorHotKeyPressed(EditorHotKeyPressedEvent event)
   {
      if (handleKeyPressing(event.isCtrl(), event.isAlt(), event.isShift(), event.getKeyCode()))
      {
         event.setHotKeyHandled(true);
      }
   }

   /**
    * @param event
    */
   public void onKeyDown(final Event event)
   {
      int keyCode = DOM.eventGetKeyCode(event);
      boolean ctrl = event.getCtrlKey();
      boolean alt = event.getAltKey();
      boolean shift = event.getShiftKey();
      
      if (hotKeyPressedListener != null)
      {
         hotKeyPressedListener.onHotKeyPressed(ctrl, alt, shift, keyCode);
         event.preventDefault();
      }
      else
      {
         if (handleKeyPressing(ctrl, alt, shift, keyCode))
         {
            event.preventDefault();
         }
      }
   }

   private boolean handleKeyPressing(boolean ctrl, boolean alt, boolean shift, int keyCode)
   {
      if (keyCode < HotKeyHelper.KeyCode.F1 || keyCode > HotKeyHelper.KeyCode.F12)
      {
         if (!ctrl && !alt)
         {
            return false;
         }
      }

      if (keyCode == 16 || keyCode == 17 || keyCode == 18 || keyCode == 224)
      {
         return false;
      }

      String shortcut = "";
      if (ctrl)
      {
         shortcut = "Ctrl";
      }

      if (alt)
      {
         if (shortcut.isEmpty())
         {
            shortcut = "Alt";
         }
         else
         {
            shortcut += "+Alt";
         }
      }

      if (shift)
      {
         if (shortcut.isEmpty())
         {
            shortcut = "Shift";
         }
         else
         {
            shortcut += "+Shift";
         }
      }

      if (shortcut.isEmpty())
      {
         shortcut = HotKeyHelper.getKeyName(String.valueOf(keyCode));
      }
      else
      {
         shortcut += "+" + HotKeyHelper.getKeyName(String.valueOf(keyCode));
      }

      boolean hotKeyBinded = false;

      // search associated command
      if (hotKeyMap.containsKey(shortcut))
      {
         String controlId = hotKeyMap.get(shortcut);
         Control control = controlsMap.get(controlId);
         if (control instanceof SimpleControl)
         {
            SimpleControl simpleControl = (SimpleControl)control;

            if (shortcut.equals(simpleControl.getHotKey()))
            {
               hotKeyBinded = true;

               if (simpleControl.getEvent() != null && (simpleControl.isEnabled() || simpleControl.isIgnoreDisable()))
               {
                  IDE.fireEvent(simpleControl.getEvent());
                  return true;
               }
            }
         }
      }

      return hotKeyBinded;
   }

   public void setHotKeys(Map<String, String> newHotKeys)
   {
      Map<String, String> temp = new LinkedHashMap<String, String>();
      temp.putAll(newHotKeys);

      if (hotKeyMap == null)
      {
         hotKeyMap = new LinkedHashMap<String, String>();
         applicationSettings.setValue("hotkeys", hotKeyMap, Store.SERVER);
      }
      else
      {
         hotKeyMap.clear();
      }

      hotKeyMap.putAll(temp);
      storeHotKeysToControls();
   }

}
