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

package org.exoplatform.ide.client.project.explorer;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ItemsCellTree extends Composite
{
   
   private MultiSelectionModel<Item> selectionModel = new MultiSelectionModel<Item>();
   
   private Cell<Item> treeCell = new AbstractCell<Item>()
   {

      private void renderHTML(SafeHtmlBuilder sb, ImageResource imageResource, String text)
      {
//         String image = "<div style=\"left: 19px; position: absolute; line-height: 0px; top: 50%; margin-top: -8px;\">";
//         image += ImageHelper.getImageHTML(imageResource);
//         image += "</div>";
//         sb.appendHtmlConstant(image);
//         sb.appendHtmlConstant("<div style=\"left:40px; position:absolute;\">");
//         sb.appendEscaped(text);
//         sb.appendHtmlConstant("</div>");
         
         sb.appendEscaped(text);
         
      }

      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, Item value, SafeHtmlBuilder sb)
      {
         if (value == null)
         {
            return;
         }

         if (value instanceof ProjectModel)
         {
            renderHTML(sb, IDEImageBundle.INSTANCE.workspace(), ((ProjectModel)value).getName());
         }

         if (value instanceof FolderModel)
         {
            renderHTML(sb, IDEImageBundle.INSTANCE.folder(), ((FolderModel)value).getName());
         }

         if (value instanceof FileModel)
         {
            renderHTML(sb, IDEImageBundle.INSTANCE.ok(), ((FileModel)value).getName());
         }
      }

   };   
   
   private class ItemsTreeViewModel implements TreeViewModel
   {
      
      private Project project;
      
      public ItemsTreeViewModel(Project project) {
         this.project = project;
      }

      @Override
      public <T> NodeInfo<?> getNodeInfo(T value)
      {
         System.out.println("GET NODE INFO FOR > " + value);
         
         if (value == null)
         {
            List<Item> projects = new ArrayList<Item>();
            projects.add(project);
            ListDataProvider<Item> dataProvider = new ListDataProvider<Item>(projects);
            return new DefaultNodeInfo<Item>(dataProvider, treeCell, selectionModel, null);
         }

         if (value instanceof Project)
         {
            FolderDataProvider dataProvider = new FolderDataProvider((Project)value);
            return new DefaultNodeInfo<Item>(dataProvider, treeCell, selectionModel, null);
         }
         
         return null;
      }

      @Override
      public boolean isLeaf(Object value)
      {
         System.out.println("IS LEAF > " + value);
         
         if (value == null) {
            return false;
         }
         
         if (value instanceof Folder) {
            return false;
         }
         
         return true;
      }
      
   }

//   private ScrollPanel scrollPanel;
   
   private CellTree cellTree;
   
   public ItemsCellTree(Project project) {
//      scrollPanel = new ScrollPanel();
//      initWidget(scrollPanel);
      
      TreeViewModel treeViewModel = new ItemsTreeViewModel(project);
      
      Resources resources = GWT.create(CellTreeResource.class);
      cellTree = new CellTree(treeViewModel, null, resources);
      
      //cellTree = new Tree(treeViewModel, null);
      cellTree.setWidth("100%");
      cellTree.setHeight("100%");
      cellTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
      cellTree.setAnimationEnabled(true);
//      scrollPanel.add(cellTree);
      
      initWidget(cellTree);
   }

}
