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
package org.exoplatform.ide.client.versioning;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.module.vfs.api.Version;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class ViewVersionsPresenter
{
   interface Display
   {
      HasClickHandlers getOpenVersionButton();

      HasClickHandlers getRestoreButton();

      HasClickHandlers getCloseButton();

      ListGridItem<Version> getVersionsGrid();

      void closeForm();
   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Version selectedVersion;

   public ViewVersionsPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
   }

   private Display display;

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getOpenVersionButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            if (selectedVersion != null)
            {
               eventBus.fireEvent(new OpenFileEvent(selectedVersion, false));
            }
         }
      });

      display.getRestoreButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            Dialogs.getInstance().showInfo("Restore");
         }
      });

      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getVersionsGrid().addSelectionHandler(new SelectionHandler<Version>()
      {

         public void onSelection(SelectionEvent<Version> event)
         {
            if (event.getSelectedItem() != null && !event.getSelectedItem().equals(selectedVersion))
            {
               selectedVersion = event.getSelectedItem();
            }
         }
      });
   }

   /**
    * Destroy presenter.
    */
   public void destroy()
   {
      handlers.removeHandlers();
   }
}
