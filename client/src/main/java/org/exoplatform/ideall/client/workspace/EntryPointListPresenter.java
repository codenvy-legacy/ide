/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.workspace;

import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.workspace.event.SwitchEntryPointEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class EntryPointListPresenter
{

   public interface Display
   {
      ListGridItem<String> getEntryPoints();

      void closeForm();

      HasClickHandlers getOkButton();

      HasClickHandlers getCancelButton();

      void enableOkButton();

      void disableOkButton();

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private List<String> entryPoints;

   private Display display;

   private String selectedEntryPoint;

   public EntryPointListPresenter(HandlerManager eventBus, ApplicationContext context, List<String> entryPoints)
   {
      this.context = context;
      this.eventBus = eventBus;
      this.entryPoints = entryPoints;

      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.disableOkButton();

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent arg0)
         {
            display.closeForm();
         }

      });

      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            changeEntryPoint();
         }
      });

      display.getEntryPoints().setValue(entryPoints);
      display.getEntryPoints().addSelectionHandler(new SelectionHandler<String>()
      {

         public void onSelection(SelectionEvent<String> event)
         {
            onEntryPointSelected(event.getSelectedItem());
         }

      });
   }

   protected void onEntryPointSelected(String selectedItem)
   {
      if (selectedItem == null)
      {
         display.disableOkButton();
         return;
      }

      if (selectedItem.equals(selectedEntryPoint))
      {
         return;
      }
      selectedEntryPoint = selectedItem;
      display.enableOkButton();
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void changeEntryPoint()
   {
      display.closeForm();
      eventBus.fireEvent(new SwitchEntryPointEvent(selectedEntryPoint));
   }

}
