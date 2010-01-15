/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.solution.menu;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.solution.menu.bean.CommandItem;
import org.exoplatform.ideall.client.solution.menu.bean.DelimiterItem;
import org.exoplatform.ideall.client.solution.menu.bean.MenuItem;
import org.exoplatform.ideall.client.solution.menu.event.GWTMenuItemSelectedEvent;
import org.exoplatform.ideall.client.solution.menu.style.PopupMenuStyle;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTPopupMenu extends Composite
{

   public final static String TITLE_PROPERTY = "menuItemTitle";

   public final static String ENABLED_PROPERTY = "menuItemEnabled";

   private SimplePanel absolutePanel;

   private LinkedHashMap<String, MenuItem> menuItems;

   private HandlerManager eventBus;

   protected PopupMenuTable table;

   private AbsolutePanel lockLayer;

   private Timer parentRemoveSubPopupTimer;

   public GWTPopupMenu(HandlerManager eventBus, LinkedHashMap<String, MenuItem> menuItems, AbsolutePanel lockLayer,
      Timer parentRemoveSubPopupTimer)
   {
      this.eventBus = eventBus;

      this.menuItems = menuItems;
      this.lockLayer = lockLayer;

      this.parentRemoveSubPopupTimer = parentRemoveSubPopupTimer;
      if (parentRemoveSubPopupTimer != null)
      {
         parentRemoveSubPopupTimer.cancel();
      }

      absolutePanel = new SimplePanel();
      initWidget(absolutePanel);
      absolutePanel.setStyleName(PopupMenuStyle.MENU_MAIN);

      createPopupMenu();

      showTimer.schedule(1);
   }

   protected Timer showTimer = new Timer()
   {
      @Override
      public void run()
      {
         absolutePanel.setWidth("" + table.getOffsetWidth() + "px");
         absolutePanel.setHeight("" + table.getOffsetHeight() + "px");
         table.setWidth("100%");
         table.setHeight("100%");
      }
   };

   private class PopupMenuTable extends FlexTable
   {

      public PopupMenuTable()
      {
         sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEDOWN);
      }

      @Override
      public void onBrowserEvent(Event event)
      {
         Element td = getEventTargetCell(event);
         if (td == null)
            return;
         Element tr = DOM.getParent(td);

         boolean enabled = Boolean.parseBoolean(DOM.getElementAttribute(tr, ENABLED_PROPERTY));
         if (!enabled)
         {
            return;
         }

         switch (DOM.eventGetType(event))
         {
            case Event.ONMOUSEOVER : {
               onOver(tr);
               break;
            }

            case Event.ONMOUSEDOWN : {
               onSelect(tr);
               break;
            }

         }

      }
   }

   private Element overedTR;

   protected void onOver(Element tr)
   {
      if (parentRemoveSubPopupTimer != null)
      {
         parentRemoveSubPopupTimer.cancel();
      }

      if (tr == overedTR)
      {
         return;
      }
      if (overedTR != null)
      {
         onOut(overedTR);
      }

      overedTR = tr;

      if (selectedTR != tr)
      {
         if (!removeSubPopupTimer.isSheduled())
         {
            removeSubPopupTimer.schedule(500);
         }
         showSubPopupTimer.cancel();
      }
      else
      {
         removeSubPopupTimer.cancel();
      }

      String title = DOM.getElementAttribute(tr, TITLE_PROPERTY);

      CommandItem commandItem = (CommandItem)menuItems.get(title);
      if (commandItem.getChildren().size() != 0)
      {
         showSubPopupTimer.schedule(500);
      }

      Element iconTD = DOM.getChild(tr, 0);
      Element titleTD = DOM.getChild(tr, 1);
      Element hotKeyTD = DOM.getChild(tr, 2);
      Element submenuTD = DOM.getChild(tr, 3);

      iconTD.setClassName(PopupMenuStyle.ICON_FIELD_OVER);
      titleTD.setClassName(PopupMenuStyle.TITLE_FIELD_OVER);
      hotKeyTD.setClassName(PopupMenuStyle.KEY_FIELD_OVER);
      submenuTD.setClassName(PopupMenuStyle.SUBMENU_FIELD_OVER);
   }

   protected void onOut(Element tr)
   {
      Element iconTD = DOM.getChild(tr, 0);
      Element titleTD = DOM.getChild(tr, 1);
      Element hotKeyTD = DOM.getChild(tr, 2);
      Element submenuTD = DOM.getChild(tr, 3);

      iconTD.setClassName(PopupMenuStyle.ICON_FIELD);
      titleTD.setClassName(PopupMenuStyle.TITLE_FIELD);
      hotKeyTD.setClassName(PopupMenuStyle.KEY_FIELD);
      submenuTD.setClassName(PopupMenuStyle.SUBMENU_FIELD);
   }

   private int getIndexOf(String title)
   {
      int index = 0;

      Iterator<String> keyIter = menuItems.keySet().iterator();
      while (keyIter.hasNext())
      {
         String key = keyIter.next();
         if (title.equals(key))
         {
            break;
         }
         index++;
      }

      return index;
   }

   private Element selectedTR;

   private GWTPopupMenu subPopup;

   private RemoveSubPopupTimer removeSubPopupTimer = new RemoveSubPopupTimer();

   protected class RemoveSubPopupTimer extends Timer
   {

      boolean sheduled = false;

      public boolean isSheduled()
      {
         return sheduled;
      }

      public void schedule(int delayMillis)
      {
         super.schedule(delayMillis);
         sheduled = true;
      }

      public void cancel()
      {
         super.cancel();
         sheduled = false;
      }

      @Override
      public void run()
      {
         if (subPopup == null)
         {
            return;
         }

         subPopup.removeFromParent();
         subPopup = null;
         selectedTR = null;
      }
   };

   private Timer showSubPopupTimer = new Timer()
   {
      @Override
      public void run()
      {
         removeSubPopupTimer.cancel();

         if (selectedTR == overedTR)
         {
            return;
         }

         selectedTR = overedTR;
         if (subPopup != null)
         {
            subPopup.removeFromParent();
         }

         showSubPopup(selectedTR);
      }
   };

   protected void onSelect(Element tr)
   {
      removeSubPopupTimer.cancel();

      if (tr == selectedTR)
      {
         return;
      }
      selectedTR = tr;

      if (subPopup != null)
      {
         subPopup.removeFromParent();
      }

      showSubPopup(tr);
   }

   private void showSubPopup(Element tr)
   {
      String title = DOM.getElementAttribute(tr, TITLE_PROPERTY);

      CommandItem commandItem = (CommandItem)menuItems.get(title);
      if (commandItem.getChildren().size() == 0)
      {
         eventBus.fireEvent(new GWTMenuItemSelectedEvent(commandItem.getCommand()));
      }
      else
      {
         int x = getAbsoluteLeft() + getOffsetWidth();
         int index = getIndexOf(title);
         int y = getAbsoluteTop() - GWTMenuBar.MENU_HEIGHT + index * 22;
         subPopup = new GWTPopupMenu(eventBus, commandItem.getChildren(), lockLayer, removeSubPopupTimer);
         lockLayer.add(subPopup, x, y);
      }
   }

   @Override
   protected void onDetach()
   {
      removeSubPopupTimer.cancel();
      showSubPopupTimer.cancel();

      if (subPopup != null)
      {
         subPopup.removeFromParent();
         subPopup = null;
      }
      super.onDetach();
   }

   private void createPopupMenu()
   {
      table = new PopupMenuTable();
      table.setStyleName(PopupMenuStyle.MENU_TABLE);
      table.setCellPadding(0);
      table.setCellSpacing(0);
      DOM.setElementAttribute(table.getElement(), "border", "0");

      Iterator<String> iterator = menuItems.keySet().iterator();
      int i = 0;
      while (iterator.hasNext())
      {
         String title = iterator.next();
         MenuItem menuItem = menuItems.get(title);

         if (menuItem instanceof DelimiterItem)
         {
            table.getFlexCellFormatter().setColSpan(i, 0, 4);
            table.setHTML(i, 0, "<nobr><hr></nobr>");
            table.getCellFormatter().setStyleName(i, 0, PopupMenuStyle.DELIMITER);
         }
         else
         {
            CommandItem commandItem = (CommandItem)menuItem;
            boolean enabled = commandItem.getCommand() == null ? true : commandItem.getCommand().isEnabled();

            if (commandItem.getCommand() != null)
            {
               table.setHTML(i, 0, "<img src=\"" + commandItem.getCommand().getIcon() + "\" />");
            }
            else
            {
               table.setHTML(i, 0, "<nobr></nobr>");
            }

            table.getCellFormatter().setStyleName(i, 0,
               enabled ? PopupMenuStyle.ICON_FIELD : PopupMenuStyle.ICON_FIELD_DISABLED);

            table.setHTML(i, 1, "<nobr>" + commandItem.getTitle() + "</nobr>");
            table.getCellFormatter().setStyleName(i, 1,
               enabled ? PopupMenuStyle.TITLE_FIELD : PopupMenuStyle.TITLE_FIELD_DISABLED);

            table.setHTML(i, 2, "<nobr>&nbsp;</nobr>");
            table.getCellFormatter().setStyleName(i, 2,
               enabled ? PopupMenuStyle.KEY_FIELD : PopupMenuStyle.KEY_FIELD_DISABLED);

            if (commandItem.getChildren().size() == 0)
            {
               table.setHTML(i, 3, "<img src=\"" + Images.imageUrl + "../eXoStyle/blank.gif" + "\" class=\""
                  + PopupMenuStyle.SUBMENU_IMAGE + "\" />");
               table.getCellFormatter().setStyleName(i, 3,
                  enabled ? PopupMenuStyle.SUBMENU_FIELD : PopupMenuStyle.SUBMENU_FIELD_DISABLED);
            }
            else
            {
               table.setHTML(i, 3, "<img src=\"" + Images.imageUrl + "../eXoStyle/popupMenu/submenu.gif\" class=\""
                  + PopupMenuStyle.SUBMENU_IMAGE + "\" />");
               table.getCellFormatter().setStyleName(i, 3,
                  enabled ? PopupMenuStyle.SUBMENU_FIELD : PopupMenuStyle.SUBMENU_FIELD_DISABLED);
            }

            DOM.setElementAttribute(table.getRowFormatter().getElement(i), TITLE_PROPERTY, commandItem.getTitle());
            DOM.setElementAttribute(table.getRowFormatter().getElement(i), ENABLED_PROPERTY, "" + enabled);
         }

         i++;
      }

      absolutePanel.add(table);

   }

}
