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
package org.exoplatform.ideall.client.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.data.event.SearchResultReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.SearchResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class NavigationPresenter implements SearchResultReceivedHandler
{

   interface Display
   {

      void showSearchResult(Folder searchResult);

   }

   protected Display display;

   private HandlerManager eventBus;

   private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

   public NavigationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   public void bindDisplay(Display d)
   {
      this.display = d;
      handlers.add(eventBus.addHandler(SearchResultReceivedEvent.TYPE, this));
   }

   public void destroy()
   {
      for (HandlerRegistration handler : handlers)
      {
         handler.removeHandler();
      }
   }

   public void onSearchResultReceived(SearchResultReceivedEvent event)
   {
      Folder searchResult = event.getFolder();
      display.showSearchResult(searchResult);
   }

}
