/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.toolbar.customize;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.command.Command;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleCommand;
import org.exoplatform.gwtframework.ui.client.component.toolbar.event.UpdateToolbarEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.settings.SettingsService;
import org.exoplatform.ideall.client.toolbar.customize.ToolbarItem.Type;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CustomizeToolbarPresenter
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

   private Handlers handlers;

   private ApplicationContext context;

   private CommandItemEx selectedCommandItem;

   private ToolbarItem selectedToolbarItem;

   private ArrayList<ToolbarItem> toolbarItems = new ArrayList<ToolbarItem>();

   public CustomizeToolbarPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
   }

   public void destroy()
   {
      handlers.removeHandlers();
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
            applyChanges();
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
      fillToolbarListGrid(context.getToolBarItems());
   }

   private void fillCommandListGrid()
   {
      List<CommandItemEx> commands = new ArrayList<CommandItemEx>();

      List<String> groups = new ArrayList<String>();

      for (Command command : context.getCommands())
      {
         if (!(command instanceof SimpleCommand)) {
            continue;
         }
         
         String commandId = command.getId();
         System.out.println("command > " + commandId);
         if (commandId.indexOf("/") >= 0) {
            commandId = commandId.substring(commandId.lastIndexOf("/"));
         }
         
//         if (simpleCommand.getTitle() != null)
//         {
//            String groupName = simpleCommand.getId();
//            if (groupName.indexOf("/") >= 0)
//            {
//               groupName = groupName.substring(0, groupName.indexOf("/"));
//            }
//
//            if (!groups.contains(groupName))
//            {
//               groups.add(groupName);
//               commands.add(new CommandItemEx(groupName, true));
//            }
//
//            if (command.getEvent() != null)
//            {
//               commands.add(new CommandItemEx(command.getTitle(), command));
//            }
//         }
      }

      display.getCommandItemListGrid().setValue(commands);
   }

   private Command getCommandById(String id)
   {
      for (Command command : context.getCommands())
      {
         if (id.equals(command.getId()))
         {
            return command;
         }
      }

      return null;
   }

   private void fillToolbarListGrid(ArrayList<String> items)
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
            Command command = getCommandById(toolbarItem);
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

   private void updateToolbar()
   {
      context.getToolBarItems().clear();

      for (ToolbarItem toolbarItem : toolbarItems)
      {
         if (toolbarItem.getType() == Type.COMMAND)
         {
            context.getToolBarItems().add(toolbarItem.getCommand().getId());
         }
         else if (toolbarItem.getType() == Type.SPACER)
         {
            context.getToolBarItems().add("");
         }
         else
         {
            context.getToolBarItems().add("---");
         }
      }

      eventBus.fireEvent(new UpdateToolbarEvent(context.getToolBarItems(), context.getCommands()));
      SettingsService.getInstance().saveSetting(context);
   }

   private void applyChanges()
   {
      updateToolbar();
      display.closeForm();
   }

   private void restoreDefaults()
   {
      fillToolbarListGrid(context.getToolBarDefaultItems());
      selectedToolbarItem = null;
      display.disableAddCommandButton();
      display.disableAddDelimiterButton();
      display.disableDeleteCommandButton();
      display.disableMoveUpButton();
      display.disableMoveDownButton();
   }

}
