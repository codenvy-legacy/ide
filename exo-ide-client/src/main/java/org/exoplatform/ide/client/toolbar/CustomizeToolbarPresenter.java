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
package org.exoplatform.ide.client.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.TextInputControl;
import org.exoplatform.gwtframework.ui.client.command.ui.SetToolbarItemsEvent;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.toolbar.ToolbarItem.Type;

import com.google.gwt.core.client.GWT;
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

public class CustomizeToolbarPresenter implements ControlsUpdatedHandler, ApplicationSettingsReceivedHandler,
   CustomizeToolbarHandler, ViewClosedHandler
{

   /**
    *
    */
   public interface Display extends IsView
   {

      ListGridItem<CommandItemEx> getCommandsListGrid();

      ListGridItem<ToolbarItem> getToolbarItemsListGrid();

      void selectToolbarItem(ToolbarItem item);

      HasClickHandlers getAddCommandButton();

      HasClickHandlers getAddDelimiterButton();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getMoveUpButton();

      HasClickHandlers getMoveDownButton();

      HasClickHandlers getOkButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getDefaultsButton();

      void setAddCommandButtonEnabled(boolean enabled);

      void setAddDelimiterButtonEnabled(boolean enabled);

      void setDeleteButtonEnabled(boolean enabled);

      void setMoveUpButtonEnabled(boolean enabled);

      void setMoveDownButtonEnabled(boolean enabled);

   }

   /**
    * Save settings failed message.
    */
   private static final String SAVE_SETTINGS_FAILURE = IDE.ERRORS_CONSTANT.customizeToolbarSaveFailure();

   /**
    * Instance of Event Bus.
    */
   private HandlerManager eventBus;

   /**
    * Instance of binded display.
    */
   private Display display;

   /**
    * Currently selected command.
    */
   private CommandItemEx selectedCommandItem;

   /**
    * Currently selected toolbar item.
    */
   private ToolbarItem selectedToolbarItem;

   /**
    * List of toolbar items.
    */
   private ArrayList<ToolbarItem> toolbarItems = new ArrayList<ToolbarItem>();

   /**
    * Application settings.
    */
   private ApplicationSettings applicationSettings;

   /**
    * List of available controls.
    */
   private List<Control> controls = new ArrayList<Control>();

   /**
    * Creates a new instance of CustomizeToolbarPresenter.
    * 
    * @param eventBus Event Bus
    */
   public CustomizeToolbarPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ControlsUpdatedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(CustomizeToolbarEvent.TYPE, this);

      IDE.getInstance().addControl(new CustomizeToolbarCommand());
   }

   @Override
   public void onControlsUpdated(ControlsUpdatedEvent event)
   {
      controls = event.getControls();
   }

   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   @Override
   public void onCustomizeToolBar(CustomizeToolbarEvent event)
   {
      if (display != null)
      {
         return;
      }

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
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

      display.getDeleteButton().addClickHandler(new ClickHandler()
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

      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            updateToolbar();
         }
      });

      display.getDefaultsButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            restoreDefaults();
         }
      });

      display.getCommandsListGrid().addSelectionHandler(new SelectionHandler<CommandItemEx>()
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

      display.setAddCommandButtonEnabled(false);
      display.setAddDelimiterButtonEnabled(false);
      display.setDeleteButtonEnabled(false);
      display.setMoveUpButtonEnabled(false);
      display.setMoveDownButtonEnabled(false);

      fillCommandListGrid();

      List<String> toolbarItems = applicationSettings.getValueAsList("toolbar-items");
      if (toolbarItems == null)
      {
         toolbarItems = new ArrayList<String>();
         toolbarItems.add("");
         applicationSettings.setValue("toolbar-items", toolbarItems, Store.SERVER);
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

      display.getCommandsListGrid().setValue(commandList);
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

      display.setAddDelimiterButtonEnabled(true);

      checkMoveCommands();
   }

   private void checkAddCommandButton()
   {
      if (selectedToolbarItem == null)
      {
         display.setAddCommandButtonEnabled(false);
         return;
      }

      if (selectedCommandItem == null)
      {
         return;
      }

      if (selectedCommandItem.isGroup())
      {
         display.setAddCommandButtonEnabled(false);
      }
      else
      {
         display.setAddCommandButtonEnabled(true);
      }
   }

   private void checkDeleteToolbarItemButton()
   {
      if (selectedToolbarItem == null)
      {
         display.setDeleteButtonEnabled(false);
         return;
      }

      if (selectedToolbarItem.getType() == Type.SPACER)
      {
         display.setDeleteButtonEnabled(false);
      }
      else
      {
         display.setDeleteButtonEnabled(true);
      }
   }

   private void checkMoveCommands()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);
      if (index == 0)
      {
         display.setMoveUpButtonEnabled(false);
      }
      else
      {
         display.setMoveUpButtonEnabled(true);
      }

      if (index < toolbarItems.size() - 1)
      {
         display.setMoveDownButtonEnabled(true);
      }
      else
      {
         display.setMoveDownButtonEnabled(false);
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
      display.selectToolbarItem(item);

      checkMoveCommands();
   }

   private void addDelimiter()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);

      ToolbarItem delimiter = new ToolbarItem(Type.DELIMITER);
      toolbarItems.add(index + 1, delimiter);

      display.getToolbarItemsListGrid().setValue(toolbarItems);
      selectedToolbarItem = delimiter;
      display.selectToolbarItem(delimiter);

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
      display.selectToolbarItem(selectedToolbarItem);

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

      display.selectToolbarItem(selectedToolbarItem);
   }

   private void moveDown()
   {
      int index = toolbarItems.indexOf(selectedToolbarItem);
      toolbarItems.remove(selectedToolbarItem);
      toolbarItems.add(index + 1, selectedToolbarItem);

      display.getToolbarItemsListGrid().setValue(toolbarItems);

      checkMoveCommands();

      display.selectToolbarItem(selectedToolbarItem);
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

      SettingsService.getInstance().saveSettingsToServer(applicationSettings,
         new AsyncRequestCallback<ApplicationSettings>()
         {
            @Override
            protected void onSuccess(ApplicationSettings result)
            {
               eventBus.fireEvent(new SetToolbarItemsEvent("exoIDEToolbar", itemsToUpdate, controls));
               IDE.getInstance().closeView(display.asView().getId());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               eventBus.fireEvent(new ExceptionThrownEvent(SAVE_SETTINGS_FAILURE));
            }
         });
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

      display.setAddCommandButtonEnabled(false);
      display.setAddDelimiterButtonEnabled(false);
      display.setDeleteButtonEnabled(false);
      display.setMoveUpButtonEnabled(false);
      display.setMoveDownButtonEnabled(false);
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
