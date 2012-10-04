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
package org.exoplatform.ide.part;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.part.PartStackPresenter.Display.TabItem;
import org.exoplatform.ide.presenter.Presenter;

/**
 * 
 * 
 * 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class PartStackPresenter implements Presenter
{
   // parts
   private final JsonArray<PartPresenter> parts = JsonCollections.createArray();

   // view
   private final Display display;

   // state of the presenter
   private PartPresenter activePart;

   public interface FocusRequstHandler
   {
      /** PartStack is being clicked and requests Focus */
      void onRequestFocus();
   }

   /**
    * View interface
    */
   public interface Display extends IsWidget
   {
      /**
       * Tab which can be clicked and closed
       */
      public interface TabItem extends HasCloseHandlers<TabItem>, HasClickHandlers
      {
      }

      /** Add Tab */
      public TabItem addTabButton(Image icon, String title, boolean closable);

      /** Remove Tab */
      public void removeTabButton(int index);

      /** Set Active Tab */
      public void setActiveTabButton(int index);

      /** Get Content Panel */
      public HasWidgets getContentPanel();

      /** Set PartStack focused */
      public void setFocus(boolean focused);

      /** Set display focus request handler   */
      public void setFocusRequstHandler(FocusRequstHandler handler);
   }

   /**
    * @param partStackResources
    */
   public PartStackPresenter(PartStackResources partStackResources)
   {
      display = new PartStackView(partStackResources);
   }

   public void setFocusRequstHandler(FocusRequstHandler handler)
   {
      display.setFocusRequstHandler(handler);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(HasWidgets container)
   {
      container.add(display.asWidget());
   }

   public void setFocus(boolean focused)
   {
      display.setFocus(focused);
   }

   public void addPart(PartPresenter part)
   {
      if (parts.contains(part))
      {
         // part already exists
         return;
      }
      parts.add(part);
      // include close button
      TabItem tabItem = display.addTabButton(null, part.getTitle(), true);
      bindEvents(tabItem, part);
      setActivePart(part);
   }

   public boolean containsPart(PartPresenter part)
   {
      return parts.contains(part);
   }

   public PartPresenter getActivePart()
   {
      return activePart;
   }

   protected void setActivePart(PartPresenter part)
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
            setActivePart(parts.isEmpty() ? null : parts.get(0));
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
            setActivePart(part);
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
