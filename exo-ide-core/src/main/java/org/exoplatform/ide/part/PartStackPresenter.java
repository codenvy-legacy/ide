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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.core.event.EditorDirtyStateChangedEvent;
import org.exoplatform.ide.editor.EditorPartPresenter;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.part.PartStackView.TabItem;
import org.exoplatform.ide.presenter.Presenter;

/**
 * Implements "Tab-like" UI Component, that accepts PartPresenters as child elements.
 * It's designed to remove child from DOM, when it is hidden. So keeping DOM as small
 * as possible.
 * 
 * PartStack support "focus" (please don't mix with GWT Widget's Focus feature).
 * Focused PartStack will highlight active Part, notifying user what component is 
 * currently active.
 * 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class PartStackPresenter implements Presenter, PartStackView.ActionDelegate
{
   /** list of parts */
   private final JsonArray<PartPresenter> parts = JsonCollections.createArray();

   /** view implementation */
   private final PartStackView view;

   /** current active part */
   private PartPresenter activePart;

   private PartStackEventHandler partStackHandler;

   /** Handles PartStack actions */
   public interface PartStackEventHandler
   {
      /** PartStack is being clicked and requests Focus */
      void onActivePartChanged(PartPresenter part);

      /** PartStack is being clicked and requests Focus */
      void onRequestFocus(PartStackPresenter partStack);
   }

   private PropertyListener propertyListener = new PropertyListener()
   {

      @Override
      public void propertyChanged(PartPresenter source, int propId)
      {
         if (PartPresenter.PROP_TITLE == propId)
         {
            updatePartTab(source);
         }
         else if (EditorPartPresenter.PROP_DIRTY == propId)
         {
            eventBus.fireEvent(new EditorDirtyStateChangedEvent((EditorPartPresenter)source));
         }
      }
   };

   private final EventBus eventBus;

   /**
    * 
    * Creates PartStack with given instance of display and resources (CSS and Images)
    * 
    * @param partStackResources
    */
   @Inject
   public PartStackPresenter(PartStackView view, PartStackUIResources partStackResources, EventBus eventBus)
   {
      this.view = view;
      this.eventBus = eventBus;
      view.setDelegate(this);
   }

   /**
    * Update part tab, it's may be title, icon or tooltip
    * @param part
    */
   private void updatePartTab(PartPresenter part)
   {
      if (!parts.contains(part))
         throw new IllegalArgumentException("This part stack not contains: " + part.getTitle());
      int index = parts.indexOf(part);
      view.updateTabItem(index, part.getTitleImage(), part.getTitle(), part.getTitleToolTip());
   }

   /**
    * Set Handler that will listen to event when Active part changed
    * 
    * @param partStackHandler
    */
   public void setPartStackEventHandler(PartStackEventHandler partStackHandler)
   {
      this.partStackHandler = partStackHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }

   /**
    * Change the focused state of the PartStack to desired value 
    * 
    * @param focused
    */
   public void setFocus(boolean focused)
   {
      view.setFocus(focused);
   }

   /**
    * Add part to the PartStack. Newly added part will be activated. If the Part
    * has already been added to this PartStack, then it will be activated only. 
    * 
    * @param part
    */
   public void addPart(PartPresenter part)
   {
      if (parts.contains(part))
      {
         // part already exists
         // activate it
         setActivePart(part);
         // and return
         return;
      }
      parts.add(part);
      part.addPropertyListener(propertyListener);
      // include close button
      ImageResource titleImage = part.getTitleImage();
      TabItem tabItem =
         view.addTabButton(titleImage == null ? null : new Image(titleImage), part.getTitle(),
            part.getTitleToolTip(), true);
      bindEvents(tabItem, part);
      setActivePart(part);
   }

   /**
    * Ask if PartStack contains given Part. 
    * 
    * @param part
    * @return
    */
   public boolean containsPart(PartPresenter part)
   {
      return parts.contains(part);
   }

   /**
    * Number of parts in the PartStack
    * 
    * @return
    */
   public int getNumberOfParts()
   {
      return parts.size();
   }

   /**
    * Get active Part. Active is the part that is currently displayed on the screen
    * 
    * @return
    */
   public PartPresenter getActivePart()
   {
      return activePart;
   }

   /**
    * Activate given part (force show it on the screen). If part wasn't previously added
    * to the PartStack or has been removed, that method has no effect.
    * 
    * @param part
    */
   public void setActivePart(PartPresenter part)
   {
      if (activePart == part)
      {
         return;
      }
      activePart = part;
      AcceptsOneWidget contentPanel = view.getContentPanel();

      if (part == null)
      {
         view.setActiveTabButton(-1);
      }
      else
      {
         view.setActiveTabButton(parts.indexOf(activePart));
         activePart.go(contentPanel);
      }
      // notify handler, that part changed
      if (partStackHandler != null)
      {
         partStackHandler.onActivePartChanged(activePart);
      }
   }

   /**
    * Close Part
    * 
    * @param part
    */
   protected void close(PartPresenter part)
   {
      // may cancel close
      if (part.onClose())
      {
         int partIndex = parts.indexOf(part);
         view.removeTabButton(partIndex);
         parts.remove(part);
         part.removePropertyListener(propertyListener);
         if (activePart == part)
         {
            //select another part
            setActivePart(parts.isEmpty() ? null : parts.get(0));
         }
      }
   }

   /**
    * Bind Activate and Close events to the Tab 
    * 
    * @param item
    * @param part
    */
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

      item.addCloseHandler(new CloseHandler<PartStackView.TabItem>()
      {
         @Override
         public void onClose(CloseEvent<TabItem> event)
         {
            // close
            close(part);
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onRequestFocus()
   {
      // notify partStackHandler
      if (partStackHandler != null)
      {
         partStackHandler.onRequestFocus(PartStackPresenter.this);
      }
   }
}
