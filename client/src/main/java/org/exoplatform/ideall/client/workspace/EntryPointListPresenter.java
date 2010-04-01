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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.discovery.Scheme;
import org.exoplatform.ideall.client.model.discovery.marshal.EntryPoint;
import org.exoplatform.ideall.client.model.discovery.marshal.EntryPointList;
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
      ListGridItem<EntryPoint> getEntryPoints();

      void closeForm();

      HasClickHandlers getOkButton();

      HasClickHandlers getCancelButton();

      void enableOkButton();

      void disableOkButton();

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private EntryPointList entryPointList;

   private Display display;

   private EntryPoint selectedEntryPoint;

   public EntryPointListPresenter(HandlerManager eventBus, ApplicationContext context, EntryPointList entryPointList)
   {
      this.context = context;
      this.eventBus = eventBus;
      this.entryPointList = entryPointList;

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

      List<EntryPoint> entryPoints = new ArrayList<EntryPoint>();
      for (int i = 0; i < entryPointList.getEntryPoints().length(); i++) {
         EntryPoint entryPoint = entryPointList.getEntryPoints().get(i);
         System.out.println("entry point scheme > " + entryPoint.getScheme());
         if (entryPoint.getScheme().equals(Scheme.WEBDAV)) {
            entryPoints.add(entryPoint);
         }
      }

      display.getEntryPoints().setValue(entryPoints);
      display.getEntryPoints().addSelectionHandler(new SelectionHandler<EntryPoint>()
      {

         public void onSelection(SelectionEvent<EntryPoint> event)
         {
            onEntryPointSelected(event.getSelectedItem());
         }

      });
   }

   protected void onEntryPointSelected(EntryPoint selectedItem)
   {
      if (selectedItem == null)
      {
         display.disableOkButton();
         return;
      }
      
      if (selectedItem == selectedEntryPoint)
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
      eventBus.fireEvent(new SwitchEntryPointEvent(selectedEntryPoint.getHref()));
   }

}
