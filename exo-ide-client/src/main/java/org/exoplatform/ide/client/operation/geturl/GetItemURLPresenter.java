/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.geturl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GetItemURLPresenter implements GetItemURLHandler, ItemsSelectedHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {

      HasClickHandlers getOkButton();

      HasValue<String> getURLField();

   }

   private Display display;

   private List<Item> selectedItems = new ArrayList<Item>();

   public GetItemURLPresenter()
   {
      IDE.getInstance().addControl(new GetItemURLControl());

      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(GetItemURLEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
      updateURLField();
   }

   @Override
   public void onGetItemURL(GetItemURLEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
   }

   private void updateURLField()
   {
      if (display != null && !selectedItems.isEmpty())
      {
         //         System.out.println("> selected items > " + selectedItems);
         Item item = selectedItems.get(0);

         //         System.out.println("item > " + item);
         //         Iterator<String> iter = item.getLinkRelations().iterator();
         //         while (iter.hasNext()) {
         //            String rel = iter.next();
         //            System.out.println("relation > " + rel);
         //         }

         Link link = item.getLinkByRelation(Link.REL_CONTENT_BY_PATH);
         if (link != null)
         {
            //String url = item.getLinkByRelation(Link.REL_CONTENT_BY_PATH).getHref();
            String url = link.getHref();
            display.getURLField().setValue(url);
         }
      }
   }

   public void bindDisplay()
   {
      display.getOkButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      updateURLField();
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
