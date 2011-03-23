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

package org.exoplatform.ide.client.navigation.ui;

import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.browser.GWTItemTreeGrid;
import org.exoplatform.ide.client.framework.ui.gwt.impl.AbstractView;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.user.client.ui.Image;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WorkspaceViewImplNew extends AbstractView implements org.exoplatform.ide.client.navigation.WorkspacePresenter.Display
{

   private GWTItemTreeGrid treeGrid;

   public WorkspaceViewImplNew()
   {
      super(ID, "navigation", "Workspace", new Image(IDEImageBundle.INSTANCE.workspace()));
      treeGrid = new GWTItemTreeGrid();
      treeGrid.setEmptyMessage("Root folder not found!");
      add(treeGrid);
      treeGrid.setWidth(280);
      treeGrid.setHeight(150);
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

   /**
    * @see org.exoplatform.ide.client.browser.BrowserPresenter.Display#deselectItem(java.lang.String)
    */
   public void deselectItem(String path)
   {
      // TODO Auto-generated method stub

   }

}
