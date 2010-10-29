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
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionEvent;

import java.util.List;

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

      HasClickHandlers getCloseButton();

      ListGridItem<Version> getVersionsGrid();

      void closeForm();

      Version getSelectedVersion();

      void enableOpenVersionButton(boolean enable);

   }

   private HandlerManager eventBus;

   private Handlers handlers;
   
   private List<Version> versionHistory;

   public ViewVersionsPresenter(HandlerManager eventBus, List<Version> versionHistory)
   {
      this.eventBus = eventBus;
      this.versionHistory = versionHistory;
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
            openVersion();
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
            boolean enableButtons = (display.getSelectedVersion() != null);
            display.enableOpenVersionButton(enableButtons);
         }
      });

      display.getVersionsGrid().addDoubleClickHandler(new DoubleClickHandler()
      {

         public void onDoubleClick(DoubleClickEvent event)
         {
            openVersion();
         }
      });

      display.enableOpenVersionButton(false);
   }

   /**
    * Destroy presenter.
    */
   public void destroy()
   {
      handlers.removeHandlers();
   }

   /**
    * Open selected version in editor.
    */
   private void openVersion()
   {
      Version selectedVersion = display.getSelectedVersion();
      if (selectedVersion != null)
      {
         eventBus.fireEvent(new OpenVersionEvent(selectedVersion, versionHistory));
      }
      display.closeForm();
   }
}
