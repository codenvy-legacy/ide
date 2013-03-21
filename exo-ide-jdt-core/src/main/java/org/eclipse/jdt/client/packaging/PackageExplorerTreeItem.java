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
package org.eclipse.jdt.client.packaging;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TreeItem;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.packaging.model.next.Package;
import org.eclipse.jdt.client.packaging.model.next.SourceDirectory;
import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.IconImageBundle;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PackageExplorerTreeItem extends TreeItem
{

   private static final String PREFIX_ID = "ide.package_explorer.item.";
   
   private Map<String, String> locktokens;

   public PackageExplorerTreeItem(Item item, Map<String, String> locktokens)
   {
      this.locktokens = locktokens;
      
      setUserObject(item);
      render();
   }

   public void render()
   {
      Item item = (Item)getUserObject();
      
      if (item instanceof org.exoplatform.ide.vfs.shared.Project)
      {
         org.exoplatform.ide.vfs.shared.Project project = (org.exoplatform.ide.vfs.shared.Project)item;
         ImageResource imageResource = ProjectResolver.getImageForProject(project.getProjectType());
         createWidget(imageResource, getTitle(project), item);
      }
      else if (item instanceof org.eclipse.jdt.client.packaging.model.next.SourceDirectory)
      {
         org.eclipse.jdt.client.packaging.model.next.SourceDirectory sourceDirectory = (org.eclipse.jdt.client.packaging.model.next.SourceDirectory)item;
         ImageResource icon = JdtClientBundle.INSTANCE.resourceDirectory();
         createWidget(icon, getTitle(sourceDirectory), item);
      }
      else if (item instanceof org.eclipse.jdt.client.packaging.model.next.Package)
      {
         org.eclipse.jdt.client.packaging.model.next.Package _package = (org.eclipse.jdt.client.packaging.model.next.Package)item;
         if (_package.getFiles().isEmpty())
         {
            createWidget(JdtClientBundle.INSTANCE.packageEmptyFolder(), getTitle(_package), item);
         }
         else
         {
            createWidget(JdtClientBundle.INSTANCE.packageFolder(), getTitle(_package), item);
            if (!getState())
            {
               removeItems();
               addItem("");
            }
         }         
      }
      else if (item instanceof FolderModel)
      {
         FolderModel folder = (FolderModel)item;
         ImageResource icon = ImageUtil.getIcon(folder.getMimeType());
         createWidget(icon, getTitle(item), folder);
      }
      else if (item instanceof FileModel)
      {
         FileModel file = (FileModel)item;
         ImageResource icon = ImageUtil.getIcon(file.getMimeType());
         if (icon == IconImageBundle.INSTANCE.defaultFile())
         {
            IDE.fireEvent(new OutputEvent("Can not find icon for " + file.getPath() + ".  Mime type is " + file.getMimeType(), Type.ERROR));
         }
         createWidget(icon, getTitle(item), file);
      }
   }

   protected void createWidget(ImageResource icon, String text, Item item)
   {
      TreeIcon treeNodeIcon = new TreeIcon(icon);

      Grid grid = new Grid(1, 2);
      grid.setWidth("100%");

      // Image i = new Image(icon);
      //TreeIcon i = new TreeIcon(icon);
      treeNodeIcon.setWidth("16px");
      treeNodeIcon.setHeight("16px");
      grid.setWidget(0, 0, treeNodeIcon);
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
      setWidget(grid);

      getElement().setId(PREFIX_ID + Utils.md5(item.getPath()));
   }

   //      else if (item instanceof Dependencies)
   //      {
   //         Dependencies dependencyListItem = (Dependencies)item;
   //         nodeIcon = new TreeIcon(JdtClientBundle.INSTANCE.jarReferences());
   //         nodeName = dependencyListItem.getName();
   //      }
   //      else if (item instanceof Dependency)
   //      {
   //         Dependency dependencyItem = (Dependency)item;
   //         nodeIcon = new TreeIcon(JdtClientBundle.INSTANCE.jarReference());
   //         nodeName = dependencyItem.getName();
   //      }

   //      else
   //      {
   //         nodeIcon = new TreeIcon(JdtClientBundle.INSTANCE.packageExplorer());
   //         nodeName = "undefined";
   //      }
   
   public PackageExplorerTreeItem getChildByItemId(String itemId)
   {
      for (int i = 0; i < getChildCount(); i++)
      {
         PackageExplorerTreeItem childItem = (PackageExplorerTreeItem)getChild(i);
         if (childItem.getUserObject() != null && ((Item)childItem.getUserObject()).getId().equals(itemId))
         {
            return childItem;
         }
      }
      
      return null;
   }
   
   private String getTitle(Item item)
   {
      String itemName = null;
      
      if (item instanceof org.exoplatform.ide.vfs.shared.Project)
      {
         itemName = ((Project)item).getName();
      }
      else if (item instanceof org.eclipse.jdt.client.packaging.model.next.SourceDirectory)
      {
         itemName = ((SourceDirectory)item).getSourceDirectoryName();
      }
      else if (item instanceof org.eclipse.jdt.client.packaging.model.next.Package)
      {
         itemName = ((Package)item).getPackageName();
      }
      else
      {
         itemName = item.getName();
      }
      
      if (itemName == null)
      {
         itemName = "/";
      }
      
      if (locktokens == null)
      {
         return itemName;
      }

      if (item instanceof FileModel && ((FileModel)item).isLocked())
      {
         if (!locktokens.containsKey(item.getId()))
         {
            return "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-11px; margin-top:3px;\"  border=\"0\" suppress=\"TRUE\" src=\""
                  + UIHelper.getGadgetImagesURL() + "navigation/lock.png" + "\" />&nbsp;&nbsp;" + itemName;
         }
      }
      
      return itemName;
   }
   
}
