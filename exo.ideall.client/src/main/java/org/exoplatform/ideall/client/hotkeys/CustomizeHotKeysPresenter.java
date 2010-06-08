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
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.command.Command;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleCommand;
import org.exoplatform.ideall.client.hotkeys.event.RefreshHotKeysEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.settings.SettingsService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
      
   }
   
   private HandlerManager eventBus;
   
   private Handlers handlers;
   
   private ApplicationContext context;
   
   private Display display;
   
   private List<HotKeyItem> hotKeys = new ArrayList<HotKeyItem>();
   
   private HotKeyItem selectedItem;
   
   public CustomizeHotKeysPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      
      handlers = new Handlers(eventBus);
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
            hotKeySelected(event.getSelectedItem());
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
      for (Command command : context.getCommands())
      {
         if (command instanceof SimpleCommand && ((SimpleCommand)command).getEvent() != null)
         {
            hotKeys.add(new HotKeyItem(command.getId(), findHotKey(command.getId()), 
               command.getIcon()));
         }
      }

      display.getHotKeyItemListGrid().setValue(hotKeys);
   }
   
   /**
    * Find hot key in hotKeysMap by id of control.
    * 
    * @param controlId id of control
    * @return hotKey for control or empty string
    */
   private String findHotKey(String controlId)
   {
      Map<String, String> hotKeysMap = context.getHotKeys();
      
      Iterator<Entry<String, String>> it = hotKeysMap.entrySet().iterator();
      while (it.hasNext())
      {
         Entry<String, String> entry = it.next();
         if (entry.getValue().equals(controlId))
         {
            return HotKeyHelper.convertCodeHotKeyToStringHotKey(entry.getKey());
         }
      }
      return "";
   }
   
   private void hotKeySelected(HotKeyItem hotKeyItem)
   {
      selectedItem = hotKeyItem;
      
      display.enableBindButton();
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
      
      if (!validateHotKey(newHotKey))
         return;
      
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
      
      for (HotKeyItem hotKeyIdentifier : hotKeys)
      {
         if (hotKeyIdentifier.getControlId().equals(controlId))
         {
            hotKeyIdentifier.setHotKey(null);
         }
      }
      
      updateState();
   }
   
   /**
    * Validates hot keys.
    * 
    * If key is null or empty return false and show error dialog.
    * 
    * If combination of controlKey and key already exists, 
    * return false and show error dialog.
    * 
    * If combination of hot keys doesn't start with Ctrl or Alt,
    * return false and show error dialog.
    * 
    * Otherwise return true;
    * 
    * @param newHotKey
    * @return is combination of hot key is valid
    */
   private boolean validateHotKey(String newHotKey)
   {
      String controlId = selectedItem.getControlId();
      
      if (newHotKey == null || newHotKey.length() < 1)
      {
         Dialogs.getInstance().showError("Enter value for key");
         return false;
      }
      
      if (!newHotKey.startsWith("Ctrl") && !newHotKey.startsWith("Alt"))
      {
         Dialogs.getInstance().showError("First key must be Ctrl of Alt ");
         return false;
      }
      
      if (newHotKey.endsWith("+") && !newHotKey.endsWith("++"))
      {
         Dialogs.getInstance().showError("Hot key is invalid");
         return false;
      }
      
      if (HotKeyHelper.isCtrlSpace(newHotKey))
      {
         Dialogs.getInstance().showError("Combination of Ctrl+Space is reserved for code autocomplete");
         return false;
      }
      
      for (HotKeyItem hotKeyIdentifier : hotKeys)
      {
         if (hotKeyIdentifier.getHotKey()!= null 
                  && hotKeyIdentifier.getHotKey().equals(newHotKey) 
                  && !hotKeyIdentifier.getControlId().equals(controlId))
         {
            Dialogs.getInstance().showError("Such hot keys already bind to another control");
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
      context.getHotKeys().clear();
      for (HotKeyItem hotKeyIdentifier : hotKeys)
      {
         String hotKey = hotKeyIdentifier.getHotKey();
         
         if (hotKey != null && hotKey.length() > 0)
         {
            String keyCode = HotKeyHelper.convertStringHotKeyToCodeHotKey(hotKey);
            context.getHotKeys().put(keyCode, hotKeyIdentifier.getControlId());
         }
      }
      display.closeForm();
      
      eventBus.fireEvent(new RefreshHotKeysEvent());
      
      SettingsService.getInstance().saveSetting(context);
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
    * @see org.exoplatform.ideall.client.hotkeys.HotKeyPressedListener#onHotKeyPressed(java.lang.String)
    */
   public void onHotKeyPressed(String hotKey)
   {
      display.getHotKeyField().setValue(hotKey);
   }
   
}
