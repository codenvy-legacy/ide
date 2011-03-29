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
package org.exoplatform.ide.client.toolbar.customize;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.TextInputControl;
import org.exoplatform.gwtframework.ui.client.command.ui.SetToolbarItemsEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsSavedHandler;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.toolbar.customize.ToolbarItem.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CustomizeToolbarPresenter implements ApplicationSettingsSavedHandler
{

   public interface Display
   {

      void closeForm();

      ListGridItem<CommandItemEx> getCommandItemListGrid();

      ListGridItem<ToolbarItem> getToolbarItemsListGrid();

      void toolbarItemsListGridSelectItem(ToolbarItem item);

      HasClickHandlers getAddCommandButton();

      HasClickHandlers getAddDelimiterButton();

      HasClickHandlers getDeleteCommandButton();

      HasClickHandlers getMoveUpButton();

      HasClickHandlers getMoveDownButton();

      HasClickHandlers getApplyButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getDefaultsButton();

      void enableAddCommandButton();

      void disableAddCommandButton();

      void enableAddDelimiterButton();

      void disableAddDelimiterButton();

      void enableDeleteCommandButton();

      void disableDeleteCommandButton();

      void enableMoveUpButton();

      void disableMoveUpButton();

      void enableMoveDownButton();

      void disableMoveDownButton();

   }

   private HandlerManager eventBus;

   private Display display;

   /**
    * Used to remove handlers when they are no longer needed.
    */
   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();

   private CommandItemEx selectedCommandItem;

   private ToolbarItem selectedToolbarItem;

   private ArrayList<ToolbarItem> toolbarItems = new ArrayList<ToolbarItem>();

   private ApplicationSettings applicationSettings;

   private List<Control> controls;

   public CustomizeToolbarPresenter(HandlerManager eventBus, ApplicationSettings applicationSettings,
      List<Control> controls)
   {
      this.eventBus = eventBus;
      this.applicationSettings = applicationSettings;
      this.controls = controls;
   }

   /**
    * Remove handlers, that are no longer needed.
    */
   public void destroy()
   {
      //TODO: such method is not very convenient.
      //If gwt mvp framework will be used , it will be good to use
      //ResettableEventBus class
      for (HandlerRegistration h : handlerRegistrations.values())
      {
         h.removeHandler();
      }
      handlerRegistrations.clear();
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

      display.getAddCommandButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            addCommand();
         }
      });

      display.getAddDelimiterButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            addDelimiter();
         }
      });

      display.getDeleteCommandButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            deleteCommand();
         }
      });

      display.getMoveUpButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            moveUp();
         }
      });

      display.getMoveDownButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            moveDown();
         }
      });

      display.getApplyButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            updateToolbar();
         }
      });

      display.getDefaultsButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            restoreDefaults();
         }
      });

      display.getCommandItemListGrid().addSelectionHandler(new SelectionHandler<CommandItemEx>()
      {
         public void onSelection(SelectionEvent<CommandItemEx> event)
         {
            commandItemSelected(event.getSelectedItem());
         }
      });

      display.getToolbarItemsListGrid().addSelectionHandler(new SelectionHandler<ToolbarItem>()
      {
         public void onSelection(SelectionEvent<ToolbarItem> event)
         {
            toolbarItemSelected(event.getSelectedItem());
         }
      });

      display.disableAddCommandButton();
      display.disableAddDelimiterButton();
      display.disableDeleteCommandButton();
      display.disableMoveUpButton();
      display.disableMoveDownButton();

      fillCommandListGrid();

      List<String> toolbarItems = applicationSettings.getValueAsList("toolbar-items");
      if (toolbarItems == null)
      {
         toolbarItems = new ArrayList<String>();
         toolbarItems.add("");
         applicationSettings.setValue("toolbar-items", toolbarItems, Store.REGISTRY);
      }

      fillToolbarListGrid(toolbarItems);
   }

   private void fillCommandListGrid()
   {
      HashMap<String, List<Control>> groups = new LinkedHashMap<String, List<Control>>();

      for (Control command : controls)
      {
         if (command instanceof SimpleControl)
         {
            if (((SimpleControl)command).getEvent() != null)
            {
               addCommand(groups, command);
            }
         }
         else if (command instanceof PopupMenuControl)
         {
            addCommand(groups, command);
         }
         else if (command instanceof TextInputControl)
         {
            addCommand(groups, command);
         }
      }

      List<CommandItemEx> commandList = new ArrayList<CommandItemEx>();
      Iterator<String> keyIter = groups.keySet().iterator();
      while (keyIter.hasNext())
      {
         String groupName = keyIter.next();
         commandList.add(new CommandItemEx(groupName));
         List<Control> commands = groups.get(groupName);
         for (Control command : commands)
         {
            commandList.add(new CommandItemEx(command));
         }
      }

      display.getCommandItemListGrid().setValue(commandList);
   }

   private void addCommand(HashMap<String, List<Control>> groups, Control command)
   {

      String groupName = command.getId();
      if (groupName.indexOf("/") >= 0)
      {
         groupName = groupName.substring(0, groupName.lastIndexOf("/"));
      }
      else
      {
         groupName = "Other";
      }

      List<Control> commands = groups.get(groupName);
      if (commands == null)
      {
         commands = new ArrayList<Control>();
         groups.put(groupName, commands);
      }

      commands.add(command);
   }

   private Control getCommandById(String id)
   {
      for (Control command : controls)
      {
         if (id.equals(command.getId()))
         {
            return command;
         }
      }

      return null;
   }

   private void fillToolbarListGrid(List<String> items)
   {
      toolbarItems.clear();

      for (String toolbarItem : items)
      {
         if ("".equals(toolbarItem))
         {
            ToolbarItem spacer = new ToolbarItem(Type.SPACER);
            toolbarItems.add(spacer);
         }
         else if (toolbarItem.startsWith("---"))
         {
            ToolbarItem delimiter = new ToolbarItem(Type.DELIMITER);
            toolbarItems.add(delimiter);
         }
         else
         {
            Control command = getCommandById(toolbarItem);
            ToolbarItem item = new ToolbarItem(Type.COMMAND, toolbarItem, command);
            toolbarItems.add(item);
         }

      }

      display.getToolbarItemsListGrid().setValue(toolbarItems);
   }

   private void commandItemSelected(CommandItemEx commandItem)
   {
      if (commandItem == selectedCommandItem)
      {
         return;
      }
      selectedCommandItem = commandItem;

      checkAddCommandButton();
   }

   private void toolbarItemSelected(ToolbarItem toolbarItem)
   {
      if (toolbarItem == selectedToolbarItem)
      {
         return;
      }
      selectedToolbarItem = toolbarItem;

      checkAddCommandButton();

      checkDeleteToolbarItemButton();

      display.enableAddDelimiterButton();

      checkMoveCommands();
   }

   private void checkAddCommandButton()
   {
      if (selectedToolbarItem == null)
      {
         display.disableAddCommandButton();
         return;
      }

      if (selectedCommandItem == null)
      {
         return;
      }

      if (selectedCommandItem.isGroup())
      {
         display.disableAddCommandButton();
      }
      else
      {
         display.enableAddCommandButton();
      }
   }

   private void checkDeleteToolbarItemButton()
   {
      if (selectedToolbarItem == null)
      {
         display.disableDeleteCommandButton();
         return;
      }

      if (selectedToolbarItem.getType() == Type.SPACER)
      {
         display.disableDeleteCommandButton();
      }
      else
      {
         display.enableDeleteCommandButton();
      }
   }

   private void checkMoveCommands()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);
      if (index == 0)
      {
         display.disableMoveUpButton();
      }
      else
      {
         display.enableMoveUpButton();
      }

      if (index < toolbarItems.size() - 1)
      {
         display.enableMoveDownButton();
      }
      else
      {
         display.disableMoveDownButton();
      }
   }

   private void addCommand()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);

      ToolbarItem item =
         new ToolbarItem(Type.COMMAND, selectedCommandItem.getCommand().getId(), selectedCommandItem.getCommand());
      toolbarItems.add(index + 1, item);

      display.getToolbarItemsListGrid().setValue(toolbarItems);

      selectedToolbarItem = item;
      display.toolbarItemsListGridSelectItem(item);

      checkMoveCommands();
   }

   private void addDelimiter()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);

      ToolbarItem delimiter = new ToolbarItem(Type.DELIMITER);
      toolbarItems.add(index + 1, delimiter);

      display.getToolbarItemsListGrid().setValue(toolbarItems);
      selectedToolbarItem = delimiter;
      display.toolbarItemsListGridSelectItem(delimiter);

      checkMoveCommands();
   }

   private void deleteCommand()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);
      toolbarItems.remove(selectedToolbarItem);
      display.getToolbarItemsListGrid().setValue(toolbarItems);

      if (index > toolbarItems.size() - 1)
      {
         index = toolbarItems.size() - 1;
      }

      selectedToolbarItem = toolbarItems.get(index);
      display.toolbarItemsListGridSelectItem(selectedToolbarItem);

      checkAddCommandButton();
      checkDeleteToolbarItemButton();
      checkMoveCommands();
   }

   private void moveUp()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);
      toolbarItems.remove(selectedToolbarItem);
      toolbarItems.add(index - 1, selectedToolbarItem);

      display.getToolbarItemsListGrid().setValue(toolbarItems);

      checkMoveCommands();

      display.toolbarItemsListGridSelectItem(selectedToolbarItem);
   }

   private void moveDown()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);
      toolbarItems.remove(selectedToolbarItem);
      toolbarItems.add(index + 1, selectedToolbarItem);

      display.getToolbarItemsListGrid().setValue(toolbarItems);

      checkMoveCommands();

      display.toolbarItemsListGridSelectItem(selectedToolbarItem);
   }

   private List<String> itemsToUpdate;

   private void updateToolbar()
   {
      itemsToUpdate = applicationSettings.getValueAsList("toolbar-items");
      itemsToUpdate.clear();

      for (ToolbarItem toolbarItem : toolbarItems)
      {
         if (toolbarItem.getType() == Type.COMMAND)
         {
            itemsToUpdate.add(toolbarItem.getCommand().getId());
         }
         else if (toolbarItem.getType() == Type.SPACER)
         {
            itemsToUpdate.add("");
         }
         else
         {
            itemsToUpdate.add("---");
         }
      }
      handlerRegistrations.put(ApplicationSettingsSavedEvent.TYPE, eventBus.addHandler(ApplicationSettingsSavedEvent.TYPE, this));

      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.REGISTRY));
   }

   private void restoreDefaults()
   {
      List<String> toolbarDefaultItems = applicationSettings.getValueAsList("toolbar-default-items");
      if (toolbarDefaultItems == null)
      {
         toolbarDefaultItems = new ArrayList<String>();
         toolbarDefaultItems.add("");
      }

      fillToolbarListGrid(toolbarDefaultItems);
      selectedToolbarItem = null;
      display.disableAddCommandButton();
      display.disableAddDelimiterButton();
      display.disableDeleteCommandButton();
      display.disableMoveUpButton();
      display.disableMoveDownButton();
   }

   public void onApplicationSettingsSaved(ApplicationSettingsSavedEvent event)
   {
      eventBus.fireEvent(new SetToolbarItemsEvent("exoIDEToolbar", itemsToUpdate, controls));
      display.closeForm();
   }

}
