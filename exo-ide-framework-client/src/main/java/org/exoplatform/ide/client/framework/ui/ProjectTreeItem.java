/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectTreeItem extends TreeItem
{
   
   private Item item;
   
   private String prefixId;
   
   public ProjectTreeItem(Item item, String prefixId)
   {
      this.item = item;
      this.prefixId = prefixId;
      setUserObject(item);
      render();      
   }
   
   public void render()
   {
      ImageResource icon = getItemIcon(item);
      String title = getTitle(item);
      Widget widget = createItemWidget(icon, title);
      setWidget(widget);
      
      if (item instanceof FolderModel)
      {
         boolean opened = getState();
         if (!((FolderModel)item).getChildren().getItems().isEmpty() && !opened)
         {
            addItem("");            
         }
      }

      getElement().setId(prefixId + Utils.md5(item.getPath()));      
   }
   
   /**
    * Creates widget for tree node
    * 
    * @param icon
    * @param text
    * @return
    */
   private Widget createItemWidget(ImageResource icon, String text)
   {
      Grid grid = new Grid(1, 2);
      grid.setWidth("100%");

      // Image i = new Image(icon);
      TreeIcon i = new TreeIcon(icon);
      i.setHeight("16px");
      grid.setWidget(0, 0, i);
      // Label l = new Label(text, false);
      HTMLPanel l = new HTMLPanel("div", text);
      l.setStyleName("ide-Tree-label");
      grid.setWidget(0, 1, l);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 1, "100%");
      // grid.getCellFormatter().addStyleName(0, 1, "ide-Tree-label");
      DOM.setStyleAttribute(grid.getElement(), "display", "block");
      return grid;
   }
   
   /**
    * Select icon for item
    * 
    * @param item
    * @return {@link ImageResource} of item icon
    */
   public ImageResource getItemIcon(Item item)
   {
      if (item instanceof ProjectModel)
      {
         return ProjectResolver.getImageForProject(((ProjectModel)item).getProjectType());
      }
      else
         return ImageUtil.getIcon(item.getMimeType());
   }
   
   
   private String getTitle(Item item)
   {
      return item.getName();

//      if (locktokens == null)
//      {
//         return (item.getName() == null || item.getName().isEmpty()) ? "/" : item.getName();
//      }
//
//      String title = "";
//      if (item instanceof FileModel && ((FileModel)item).isLocked())
//      {
//         if (!locktokens.containsKey(item.getId()))
//         {
//            title +=
//               "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-11px; margin-top:3px;\"  border=\"0\" suppress=\"TRUE\" src=\""
//                  + UIHelper.getGadgetImagesURL() + "navigation/lock.png" + "\" />&nbsp;&nbsp;";
//         }
//      }
//      title += item.getName().isEmpty() ? "/" : item.getName();
//
//      return title;
   }

}
