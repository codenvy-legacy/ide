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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedHandler;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

   private static final class WindowCloseHandlerImpl implements ClosingHandler
   {
      public native void onWindowClosing(ClosingEvent event) /*-{
         $doc.onkeydown = null; 
      }-*/;

      private native void init() /*-{
         $doc.onkeydown = function(ev)
         { 
            var hotKeyNamager = @org.exoplatform.ide.client.hotkeys.HotKeyManager::getInstance()();
            hotKeyNamager.@org.exoplatform.ide.client.hotkeys.HotKeyManager::onKeyDown(Lcom/google/gwt/user/client/Event;)(ev || $wnd.event);
         }                        
      }-*/;
   }

   private HotKeyPressedListener hotKeyPressedListener;

   private Map<String, String> hotKeys;

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

      final WindowCloseHandlerImpl closeListener = new WindowCloseHandlerImpl();
      Window.addWindowClosingHandler(closeListener);
      closeListener.init();

      hotKeys = applicationSettings.getValueAsMap("hotkeys");
      if (hotKeys == null)
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

      // Sets new HotKeys

      Iterator<String> keyIter = hotKeys.keySet().iterator();
      while (keyIter.hasNext())
      {
         String key = keyIter.next();
         String controlId = hotKeys.get(key);
         Control control = controlsMap.get(controlId);
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
      hotKeys = new LinkedHashMap<String, String>();
      applicationSettings.setValue("hotkeys", hotKeys, Store.SERVER);

      for (Control<?> control : controlsMap.values())
      {
         if (control instanceof SimpleControl && ((SimpleControl)control).getHotKey() != null
            && !((SimpleControl)control).getHotKey().isEmpty())
         {
            hotKeys.put(((SimpleControl)control).getHotKey(), ((SimpleControl)control).getId());
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

   // This method is not unused but called by the javaScript function : WindowCloseHandlerImpl::Init
   public void onKeyDown(final Event event)
   {
      if (hotKeyPressedListener != null)
      {
         hotKeyPressedListener.onHotKeyPressed(event.getCtrlKey(), event.getAltKey(), event.getShiftKey(),
            DOM.eventGetKeyCode(event));
         event.preventDefault();
      }
      else
      {
         int keyCode = DOM.eventGetKeyCode(event);
         if (handleKeyPressing(event.getCtrlKey(), event.getAltKey(), event.getShiftKey(), keyCode))
         {
            event.preventDefault();
         }
      }
   }

   private boolean handleKeyPressing(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode)
   {
      if (!isCtrl && !isAlt)
      {
         return false;
      }

      if (keyCode == 16 || keyCode == 17 || keyCode == 18 || keyCode == 224)
      {
         return false;
      }

      String shortcut = "";
      if (isCtrl)
      {
         shortcut = "Ctrl";
      }

      if (isAlt)
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

      if (isShift)
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
         return false;
      }

      shortcut += "+" + HotKeyHelper.getKeyName(String.valueOf(keyCode));

      // search associated command

      if (hotKeys.containsKey(shortcut))
      {
         String controlId = hotKeys.get(shortcut);
         Control control = controlsMap.get(controlId);
         if (control instanceof SimpleControl)
         {
            SimpleControl simpleControl = (SimpleControl)control;
            if (shortcut.equals(simpleControl.getHotKey()) && simpleControl.getEvent() != null
               && (simpleControl.isEnabled() || simpleControl.isIgnoreDisable()))
            {
               IDE.fireEvent(simpleControl.getEvent());
               return true;
            }

         }
      }

      return false;
   }

   public void setHotKeys(Map<String, String> hotKeys)
   {
      hotKeys.clear();
      hotKeys.putAll(hotKeys);
   }

}
