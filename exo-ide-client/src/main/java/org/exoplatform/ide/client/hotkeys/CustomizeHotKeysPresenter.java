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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CustomizeHotKeysPresenter implements HotKeyPressedListener
{

   public interface LabelStyle 
   {
      static final String INFO = "exo-cutomizeHotKey-label-info";
      
      static final String ERROR = "exo-cutomizeHotKey-label-error";
   }
   
   public interface Display
   {
      HasClickHandlers getSaveButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getBindButton();

      HasClickHandlers getUnbindButton();

      ListGridItem<HotKeyItem> getHotKeyItemListGrid();

      HasValue<String> getHotKeyField();

      void disableSaveButton();

      void enableSaveButton();

      void enableBindButton();

      void disableBindButton();

      void enableUnbindButton();

      void disableUnbindButton();

      void clearHotKeyField();

      void closeForm();

      void enableHotKeyField();

      void disableHotKeyField();

      void focusOnHotKeyField();

      void showError(String style, String text);

   }

   private static final String EDITOR_GROUP = "Editor hotkeys";

   private HandlerManager eventBus;

   private Display display;

   private List<HotKeyItem> hotKeys = new ArrayList<HotKeyItem>();

   private HotKeyItem selectedItem;

   private ApplicationSettings applicationSettings;

   private List<Control> controls;

   public CustomizeHotKeysPresenter(HandlerManager eventBus, ApplicationSettings applicationSettings,
      List<Control> controls)
   {
      this.eventBus = eventBus;
      this.applicationSettings = applicationSettings;
      this.controls = controls;

      new Handlers(eventBus);
      HotKeyManager.getInstance().setHotKeyPressedListener(this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getSaveButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            saveHotKeys();
         }
      });

      display.getHotKeyItemListGrid().addSelectionHandler(new SelectionHandler<HotKeyItem>()
      {
         public void onSelection(SelectionEvent<HotKeyItem> event)
         {
            if (event.getSelectedItem().getGroup().equals(EDITOR_GROUP))
            {
               selectedItem = null;

               display.disableBindButton();
               display.disableUnbindButton();
               display.disableHotKeyField();
               return;
            }
            hotKeySelected(event.getSelectedItem());
            display.showError(null, null);
         }
      });

      display.getBindButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            bindHotKey();
         }
      });

      display.getUnbindButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            unbindHotKey();
         }
      });

      fillHotKeyList();

      display.disableHotKeyField();
   }

   private void fillHotKeyList()
   {
      /*
       * fill list of available controls
       */
      for (Control command : controls)
      {
         
         if (!(command instanceof SimpleControl)) {
            continue;
         }
         
         SimpleControl control = (SimpleControl)command;
         
         if (control.getEvent() != null)
         {
            String groupName = command.getId();
            if (groupName.indexOf("/") >= 0)
            {
               groupName = groupName.substring(0, groupName.lastIndexOf("/"));
            }

            String hotKey = control.getHotKey();
            if (hotKey == null)
            {
               hotKey = "";
            }

            if (command.getNormalImage() != null)
            {
               hotKeys.add(new HotKeyItem(command.getId(), hotKey, command.getNormalImage(), groupName));
            }
            else
            {
               hotKeys.add(new HotKeyItem(command.getId(), hotKey, command.getIcon(), groupName));
            }
         }
      }

      /*
       * fill default hot keys
       */
      Iterator<Entry<String, String>> it = ReservedHotKeys.getHotkeys().entrySet().iterator();
      while (it.hasNext())
      {
         Entry<String, String> entry = it.next();
         String id = entry.getValue();
         String hotkey = HotKeyHelper.convertToStringCombination(entry.getKey());
         hotKeys.add(new HotKeyItem(id, hotkey, (String)null, EDITOR_GROUP));
      }

      display.getHotKeyItemListGrid().setValue(hotKeys);
   }

   private void hotKeySelected(HotKeyItem hotKeyItem)
   {
      selectedItem = hotKeyItem;

      display.disableBindButton();
      display.enableUnbindButton();
      display.enableHotKeyField();
      display.focusOnHotKeyField();
      display.getHotKeyField().setValue(selectedItem.getHotKey());
   }

   /**
    * Bind hot key to selected item.
    */
   private void bindHotKey()
   {
      String newHotKey = display.getHotKeyField().getValue();

      for (HotKeyItem hotKey : hotKeys)
      {
         if (hotKey.getControlId().equals(selectedItem.getControlId()))
         {
            hotKey.setHotKey(newHotKey);
         }
      }
      updateState();
   }

   /**
    * Unbind hot key from selected item.
    */
   private void unbindHotKey()
   {
      String controlId = selectedItem.getControlId();

      for (HotKeyItem hotKeyItem : hotKeys)
      {
         if (hotKeyItem.getControlId().equals(controlId))
         {
            hotKeyItem.setHotKey(null);
         }
      }

      updateState();
   }

   /**
    * Validates hotkeys.
    * 
    * If key is null or empty return false and show info message.
    * 
    * If combination of controlKey and key already exists, 
    * return false and show error message.
    * 
    * If combination of hot keys doesn't start with Ctrl or Alt,
    * return false and show error message.
    * 
    * If you try to bind such hotkey return false and show info message
    * 
    * Otherwise return true;
    * 
    * @param newHotKey
    * @return is combination of hot key is valid
    */
   private boolean validateHotKey(boolean isCtrl, boolean isAlt, int keyCode)
   {
      final String firstKeyCtrlOrAltMsg = "First key should be Ctrl or Alt";

      final String hotkeyIsUsedInCkEditorMsg = "This hotkey is used by Code or WYSIWYG Editors";

      final String pressControlKeyThenKey = "Holt Ctrl or Alt, then press key";

      final String boundToAnotherCommand = "Such hotkey already bound to another command";
      
      final String tryToBindTheSameHotKey = "Such hotkey already bound to this command";
      
      final String tryAnotherKey = "Undefined key. Please, use another key";
      
      //--- check is control key pressed first ---
      //
      //17 - key code of Ctrl
      //18 - key code of Alt
      //on Linux, if single Ctrl or Alt key pressed, than isCtrl or isAlt will be false,
      //but keyCode will contain 17 or 18 key. To check keyCode - is the one way
      //to know, that Ctrl or Alt single key was pressed on Linux
      if (!isCtrl && !isAlt)
      {
         //if control is null, but keyCode is Ctrl or Alt,
         //than Ctrl or Alt is pressed first
         if (keyCode == 17 || keyCode == 18)
         {
            display.getHotKeyField().setValue(HotKeyHelper.getKeyName(String.valueOf(keyCode)) + "+");
            display.showError(LabelStyle.INFO, pressControlKeyThenKey);
            return false;
         }
         //if keyCode is not Ctrl or Alt
         //than another key is pressed
         else
         {
            display.getHotKeyField().setValue("");
            display.showError(LabelStyle.ERROR, firstKeyCtrlOrAltMsg);
            return false;
         }
      }
      
      //--- controlKey must be Ctrl or Alt ---
      String controlKey = null;
      if (isCtrl)
      {
         controlKey = "Ctrl";
      }
      else if (isAlt)
      {
         controlKey = "Alt";
      }
      
      //if control key is correct, but keyCode is not pressed yet
      if (keyCode == 0 || keyCode == 17 || keyCode == 18)
      {
         display.getHotKeyField().setValue(controlKey + "+");
         display.showError(LabelStyle.INFO, pressControlKeyThenKey);
         return false;
      }
      
      //control key is correct, keyCode is pressed
      String keyString = HotKeyHelper.getKeyName(String.valueOf(keyCode));
      //--- check, is keyCode correct (maybe pressed not standard key on keyboard) ---
      if (keyString == null)
      {
         display.getHotKeyField().setValue(controlKey + "+");
         display.showError(LabelStyle.INFO, tryAnotherKey);
         return false;
      }
      
      String stringHotKey = controlKey + "+" + keyString;
      
      //show hotkey in text field
      display.getHotKeyField().setValue(stringHotKey);
      
      //--- check, is stringHotKey is reserved by editor ---
      if (ReservedHotKeys.getHotkeys().containsKey(controlKey + "+" + keyCode))
      {
         display.showError(LabelStyle.ERROR, hotkeyIsUsedInCkEditorMsg);
         return false;
      }
      
      //--- check, if you try to bind the same hotkey ---
      if (stringHotKey.equals(selectedItem.getHotKey()))
      {
         display.showError(LabelStyle.ERROR, tryToBindTheSameHotKey);
         return false;
      }
      
      //--- check, is hotkey alread bound to another command ---
      String controlId = selectedItem.getControlId();
      
      for (HotKeyItem hotKeyIdentifier : hotKeys)
      {
         if (hotKeyIdentifier.getHotKey() != null && hotKeyIdentifier.getHotKey().equals(stringHotKey)
            && !hotKeyIdentifier.getControlId().equals(controlId))
         {
            display.showError(LabelStyle.ERROR, boundToAnotherCommand);
            return false;
         }
      }

      return true;
   }

   /**
    * Save hot keys.
    */
   private void saveHotKeys()
   {
      Map<String, String> keys = applicationSettings.getValueAsMap("hotkeys");
      keys.clear();

      for (HotKeyItem hotKeyItem : hotKeys)
      {
         if (!hotKeyItem.getGroup().equals(EDITOR_GROUP))
         {
            String hotKey = hotKeyItem.getHotKey();

            if (hotKey != null && !"".equals(hotKey))
            {
               String keyCode = HotKeyHelper.convertToCodeCombination(hotKey);
               keys.put(keyCode, hotKeyItem.getControlId());
            }
         }
      }

      display.closeForm();

      eventBus.fireEvent(new RefreshHotKeysEvent(keys));
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.REGISTRY));
   }

   /**
    * Update state after binding or unbinding.
    */
   private void updateState()
   {
      selectedItem = null;

      display.disableBindButton();
      display.disableUnbindButton();
      display.clearHotKeyField();
      display.enableSaveButton();
      display.disableHotKeyField();
      display.getHotKeyItemListGrid().setValue(hotKeys);
   }

   public void destroy()
   {
      HotKeyManager.getInstance().setHotKeyPressedListener(null);
   }

   /**
    * When hot key pressed, display this hot key in input field.
    * 
    * @see org.exoplatform.ide.client.hotkeys.HotKeyPressedListener#onHotKeyPressed(java.lang.String, java.lang.String)
    */
   public void onHotKeyPressed(boolean isCtrl, boolean isAlt, int keyCode)
   {
      if (selectedItem == null)
      {
         return;
      }
      if (validateHotKey(isCtrl, isAlt, keyCode))
      {
         display.showError(null, null);
         display.enableBindButton();
      }
      else
      {
         display.disableBindButton();
      }
   }

}
