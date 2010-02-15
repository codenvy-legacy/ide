/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.search;

import org.exoplatform.gwtframework.ui.dialogs.Dialogs;
import org.exoplatform.ideall.client.model.data.DataService;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SearchPresenter
{

   public interface Display
   {

      HasClickHandlers getSearchButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getSearchContent();

      void closeForm();

   }

   private Display display;

   private HandlerManager eventBus;

   private String path;

   public SearchPresenter(HandlerManager eventBus, String path)
   {
      this.eventBus = eventBus;
      this.path = path;
   }

   public void bindDisplay(Display d)
   {
      display = d;
      display.getSearchButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            onStartSearch();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

   }

   public void onStartSearch()
   {
      String content = display.getSearchContent().getValue();

      if (content.equals(""))
      {
         Dialogs.showError("You must enter text!");
         return;
      }

      String[] names = path.split("/");
      if (names.length >= 2)
      {
         path = "/" + names[1] + "/" + names[2];
      }

      DataService.getInstance().search(content, path);
      display.closeForm();
   }

}
