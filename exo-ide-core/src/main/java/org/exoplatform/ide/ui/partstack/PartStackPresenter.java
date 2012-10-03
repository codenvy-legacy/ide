/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.ide.ui.partstack;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import org.exoplatform.ide.part.PartPresenter;
import org.exoplatform.ide.presenter.Presenter;
import org.exoplatform.ide.ui.partstack.PartStackPresenter.Display.TabItem;

import java.util.ArrayList;

/**
 * 
 * 
 * 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class PartStackPresenter implements Presenter
{

   private ArrayList<PartPresenter> parts = new ArrayList<PartPresenter>();

   private Display display;

   private PartPresenter activePart;

   public interface Display extends IsWidget
   {
      public interface TabItem extends HasCloseHandlers<TabItem>, HasClickHandlers
      {
      }

      TabItem addTabButton(Image icon, String title, boolean closable);

      void removeTabButton(int index);

      void setActiveTabButton(int index);

      HasWidgets getContentPanel();

   }

   public PartStackPresenter()
   {
      display = new PartStackView();
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void go(HasWidgets container)
   {
      container.add(display.asWidget());
   }

   public void addPart(PartPresenter part)
   {
      parts.add(part);
      // include close button
      TabItem tabItem = display.addTabButton(null, part.getTitle(), true);
      bindEvents(tabItem, part);
      if (activePart == null)
      {
         setActive(part);
      }
   }

   protected void setActive(PartPresenter part)
   {
      if (activePart == part)
      {
         return;
      }
      activePart = part;
      HasWidgets contentPanel = display.getContentPanel();
      contentPanel.clear();
      if (part == null)
      {
         display.setActiveTabButton(-1);
      }
      {
         display.setActiveTabButton(parts.indexOf(activePart));
         activePart.go(contentPanel);
      }
   }

   protected void close(PartPresenter part)
   {
      // may cancel close
      if (part.close())
      {
         int partIndex = parts.indexOf(part);
         display.removeTabButton(partIndex);
         parts.remove(part);
         if (activePart == part)
         {
            //select another part
            setActive(parts.isEmpty() ? null : parts.get(0));
         }
      }
   }

   protected void bindEvents(final TabItem item, final PartPresenter part)
   {
      item.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            // make active
            setActive(part);
         }
      });

      item.addCloseHandler(new CloseHandler<PartStackPresenter.Display.TabItem>()
      {
         @Override
         public void onClose(CloseEvent<TabItem> event)
         {
            // close
            close(part);
         }
      });
   }
}
