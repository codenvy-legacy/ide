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
package org.exoplatform.ide.client.browser;

import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;

public class BrowserFormNew extends View implements BrowserPanel, BrowserPresenter.Display
{

   public static final String TITLE = "Workspace";

   private GWTItemTreeGrid treeGrid;

   private BrowserPresenter presenter;

   public BrowserFormNew(HandlerManager eventBus)
   {
      super(ID,eventBus);
      treeGrid = new GWTItemTreeGrid();
      treeGrid.setEmptyMessage("Root folder not found!");
      addMember(treeGrid);

      presenter = new BrowserPresenter(eventBus);
      presenter.bindDisplay(this);

      addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            event.cancel();
         }
      });
   }

   @Override
   public void destroy()
   {
      super.destroy();
      presenter.destroy();
   }

   public TreeGridItem<Item> getBrowserTree()
   {
      return treeGrid;
   }

   public void selectItem(String path)
   {
      //treeGrid.selectItem(path);
   }

   public List<Item> getSelectedItems()
   {
      return treeGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.browser.BrowserPresenter.Display#updateItemState(org.exoplatform.ide.client.framework.vfs.File)
    */
   public void updateItemState(File file)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.client.browser.BrowserPresenter.Display#setLockTokens(java.util.Map)
    */
   public void setLockTokens(Map<String, String> locktokens)
   {
      // TODO Auto-generated method stub

   }

}
