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
package org.exoplatform.ideall.client.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.application.command.AbstractCommand;
import org.exoplatform.ideall.client.application.command.DummyCommand;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.toolbar.component.ToolbarButton;
import org.exoplatform.ideall.client.toolbar.component.ToolbarDelimiter;
import org.exoplatform.ideall.client.toolbar.component.ToolbarItem;

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

   private List<ToolbarItem> leftItems;

   private List<ToolbarItem> rightItems;

   public GWTToolbarForm(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;

      absolutePanel = new AbsolutePanel();
      initWidget(absolutePanel);
      absolutePanel.setStyleName(Style16.TOOLBAR_PANEL);
      
      buttonsPanel = new AbsolutePanel();
      buttonsPanel.setStyleName(Style16.TOOLBAR_BUTTONS_PANEL);
      absolutePanel.add(buttonsPanel);

      presenter = new GWTToolbarPresenter(eventBus, context);
      presenter.bindDisplay(this);
   }

   public void updateToolBar(List<AbstractCommand> leftDockedItems, List<AbstractCommand> rightDockedItems)
   {
      buttonsPanel.clear();

      leftItems = new ArrayList<ToolbarItem>();
      rightItems = new ArrayList<ToolbarItem>();

      for (AbstractCommand command : leftDockedItems)
      {
         if (command instanceof DummyCommand)
         {
            ToolbarDelimiter delimiter = new ToolbarDelimiter(false);
            buttonsPanel.add(delimiter);
            leftItems.add(delimiter);
         }
         else
         {
            ToolbarButton button = new ToolbarButton(eventBus, command, false, this);
            buttonsPanel.add(button);
            leftItems.add(button);
         }
      }

      SimplePanel spacer = new SimplePanel();
      spacer.setStyleName(Style16.SPACER);
      buttonsPanel.add(spacer);

      for (AbstractCommand command : rightDockedItems)
      {
         if (command instanceof DummyCommand)
         {
            ToolbarDelimiter delimiter = new ToolbarDelimiter(true);
            buttonsPanel.add(delimiter);
            rightItems.add(delimiter);
         }
         else
         {
            ToolbarButton button = new ToolbarButton(eventBus, command, true, this);
            buttonsPanel.add(button);
            rightItems.add(button);
         }
      }

      checkDelimiters();
   }

   public void checkDelimiters(List<ToolbarItem> items)
   {
      boolean isPrevDelimiter = false;
      for (ToolbarItem item : items)
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
