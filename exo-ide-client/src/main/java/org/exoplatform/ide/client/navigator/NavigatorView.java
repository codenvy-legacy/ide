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
package org.exoplatform.ide.client.navigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.ItemTree;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class NavigatorView extends ViewImpl implements
   org.exoplatform.ide.client.navigator.NavigatorPresenter.Display
{

   public static final String ID = "ideWorkspaceView";

   /**
    * Initial width of this view
    */
   private static final int WIDTH = 250;

   /**
    * Initial height of this view
    */
   private static final int HEIGHT = 450;

   private static NavigatorViewUiBinder uiBinder = GWT.create(NavigatorViewUiBinder.class);

   interface NavigatorViewUiBinder extends UiBinder<Widget, NavigatorView>
   {
   }
   
   @UiField
   ItemTree treeGrid;
   
   private static final String TITLE = IDE.NAVIGATION_CONSTANT.workspaceTitle();

   public NavigatorView()
   {
      super(ID, ViewType.NAVIGATION, TITLE, new Image(IDEImageBundle.INSTANCE.workspace()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      treeGrid.setExpandProjects(false);
   }

   @Override
   public TreeGridItem<Item> getBrowserTree()
   {
      return treeGrid;
   }

   @Override
   public List<Item> getSelectedItems()
   {
      return treeGrid.getSelectedItems();
   }

   @Override
   public void selectItem(String path)
   {
      treeGrid.selectItem(path);
   }

   @Override
   public void deselectItem(String path)
   {
      treeGrid.deselectItem(path);
   }

   @Override
   public void updateItemState(FileModel file)
   {
      treeGrid.updateFileState(file);
   }

   @Override
   public void setLockTokens(Map<String, String> locktokens)
   {
      treeGrid.setLocktokens(locktokens);
   }

   @Override
   public void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons)
   {
      treeGrid.addItemsIcons(itemsIcons);
   }

   @Override
   public void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons)
   {
      treeGrid.removeItemIcons(itemsIcons);
   }

}
