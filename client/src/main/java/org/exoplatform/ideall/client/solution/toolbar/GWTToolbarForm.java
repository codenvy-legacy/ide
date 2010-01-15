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
package org.exoplatform.ideall.client.solution.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.solution.toolbar.bean.ToolbarItem;
import org.exoplatform.ideall.client.solution.toolbar.component.ToolbarButton;
import org.exoplatform.ideall.client.solution.toolbar.component.ToolbarControl;
import org.exoplatform.ideall.client.solution.toolbar.component.ToolbarDelimiter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTToolbarForm extends Composite implements GWTToolbarPresenter.Display, GWTToolbar
{

   public static interface Style16
   {

      public final static String TOOLBAR_PANEL = "exo-toolbar16Panel";

      public final static String TOOLBAR_BUTTONS_PANEL = "exo-toolbar16ButtonsPanel";

      public final static String SPACER = "exo-toolbar16SpacerPanel";

   }

   public final static int TOOLBAR_HEIGHT = 32;

   private HandlerManager eventBus;

   private AbsolutePanel absolutePanel;

   private AbsolutePanel buttonsPanel;

   private GWTToolbarPresenter presenter;

   private List<ToolbarControl> leftItems;

   private List<ToolbarControl> rightItems;

   public GWTToolbarForm(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      absolutePanel = new AbsolutePanel();
      initWidget(absolutePanel);
      absolutePanel.setStyleName(Style16.TOOLBAR_PANEL);

      buttonsPanel = new AbsolutePanel();
      buttonsPanel.setStyleName(Style16.TOOLBAR_BUTTONS_PANEL);
      absolutePanel.add(buttonsPanel);

      presenter = new GWTToolbarPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   public void updateToolBar(List<ToolbarItem> leftDockedItems, List<ToolbarItem> rightDockedItems)
   {
      buttonsPanel.clear();

      leftItems = new ArrayList<ToolbarControl>();
      rightItems = new ArrayList<ToolbarControl>();

      for (ToolbarItem toolbarItem : leftDockedItems)
      {
         if (toolbarItem.isDelimiter())
         {
            ToolbarDelimiter delimiter = new ToolbarDelimiter(false);
            buttonsPanel.add(delimiter);
            leftItems.add(delimiter);
         }
         else
         {
            ToolbarButton button = new ToolbarButton(eventBus, toolbarItem.getCommand(), false, this);
            buttonsPanel.add(button);
            leftItems.add(button);
         }
      }

      SimplePanel spacer = new SimplePanel();
      spacer.setStyleName(Style16.SPACER);
      buttonsPanel.add(spacer);

      for (ToolbarItem toolbarItem : rightDockedItems)
      {
         if (toolbarItem.isDelimiter())
         {
            ToolbarDelimiter delimiter = new ToolbarDelimiter(true);
            buttonsPanel.add(delimiter);
            rightItems.add(delimiter);
         }
         else
         {
            ToolbarButton button = new ToolbarButton(eventBus, toolbarItem.getCommand(), true, this);
            buttonsPanel.add(button);
            rightItems.add(button);
         }
      }

      checkDelimiters();
   }

   public void checkDelimiters(List<ToolbarControl> items)
   {
      boolean isPrevDelimiter = false;
      for (ToolbarControl item : items)
      {
         if (item instanceof ToolbarDelimiter)
         {
            ToolbarDelimiter delimiter = (ToolbarDelimiter)item;
            if (isPrevDelimiter)
            {

               if (delimiter.isVisible())
               {
                  delimiter.setHidden();
               }
            }
            else
            {
               if (items.indexOf(item) == items.size() - 1)
               {
                  if (delimiter.isVisible())
                  {
                     delimiter.setHidden();
                  }
               }
               else
               {
                  if (!delimiter.isVisible())
                  {
                     delimiter.setVisible();
                  }
               }

            }

            isPrevDelimiter = true;
         }
         else
         {
            ToolbarButton button = (ToolbarButton)item;
            if (button.isVisible())
            {
               isPrevDelimiter = false;
            }
         }
      }
   }

   public void checkDelimiters()
   {
      checkDelimiters(leftItems);
      checkDelimiters(rightItems);
   }

}
