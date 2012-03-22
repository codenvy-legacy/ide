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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.TreeViewModel;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ItemsCellTree extends Composite implements ItemTree
{

   private static final String RECEIVE_CHILDREN_ERROR_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
      .workspaceReceiveChildrenError();

   public interface CellTreeResource extends CellTree.Resources
   {
      @Source("CellTree.css")
      CellTree.Style cellTreeStyle();
   }

   private CellTreeResource resources = GWT.create(CellTreeResource.class);

   /**
    * The cell used to render categories.
    */
   private class ItemCell extends AbstractCell<Item>
   {

      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, Item value, SafeHtmlBuilder sb)
      {
         if (value != null)
         {
            if (value.getId() == null)
            {
               sb.appendEscaped("ROOT");
               return;
            }

            String imageHTML = ImageHelper.getImageHTML(getItemIcon(value));
            sb.appendHtmlConstant("<div style=\"position:relative; height:16px;\">");

            sb.appendHtmlConstant("<div style=\"width:16px; height:16px;\">");
            sb.appendHtmlConstant(imageHTML);
            sb.appendHtmlConstant("</div>");

            sb.appendHtmlConstant("<div style=\"position:absolute; left:20px; top:0px; height:16px; line-height:16px; cursor:default;\">");
            sb.appendEscaped(value.getName());
            sb.appendHtmlConstant("</div>");

            sb.appendHtmlConstant("</div>");
         }
      }
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

   private class ViewModel implements TreeViewModel
   {

      public ViewModel()
      {
      }

      @Override
      public <T> NodeInfo<?> getNodeInfo(T value)
      {
         /*
          * Root node not exist and may be created.
          */
         if (value == null)
         {
            rootItem = new ProjectModel();
            rootItemDataProvider = new RootItemDataProvider();
            ItemCell rootItemCell = new ItemCell();
            return new DefaultNodeInfo(rootItemDataProvider, rootItemCell, selectionModel, null);
         }

         if (value instanceof ProjectModel)
         {
            ItemDataProvider dataProvider = new ItemDataProvider((ProjectModel)value);
            dataProviders.put(((Item)value).getPath(), dataProvider);
            ItemCell cell = new ItemCell();
            return new DefaultNodeInfo(dataProvider, cell, selectionModel, null);
         }

         if (value instanceof FolderModel)
         {
            ItemDataProvider dataProvider = new ItemDataProvider((FolderModel)value);
            dataProviders.put(((Item)value).getPath(), dataProvider);
            ItemCell cell = new ItemCell();
            return new DefaultNodeInfo(dataProvider, cell, selectionModel, null);
         }

         return null;
      }

      @Override
      public boolean isLeaf(Object value)
      {
         if (value == rootItem || value instanceof ProjectModel || value instanceof FolderModel)
         {
            return false;
         }

         return true;
      }

   }

   private class RootItemDataProvider extends AsyncDataProvider<Item>
   {

      private HasData<Item> display;

      @Override
      protected void onRangeChanged(HasData<Item> display)
      {
         this.display = display;

         List<Item> items = new ArrayList<Item>();
         if (project != null)
         {
            items.add(project);
         }

         updateRowCount(items.size(), true);
         display.setRowData(0, items);

         if (items.size() > 0)
         {
            tree.getRootTreeNode().setChildOpen(0, true);
         }
      }

      public void update()
      {
         onRangeChanged(display);
      }

   }

   private class ItemDataProvider extends AsyncDataProvider<Item>
   {

      private Item item;

      private List<Item> children = new ArrayList<Item>();

      private HasData<Item> display;

      public ItemDataProvider(Item item)
      {
         this.item = item;
      }

      @Override
      protected void onRangeChanged(final HasData<Item> display)
      {
         this.display = display;

         if (!(item instanceof Folder))
         {
            return;
         }

         final Folder folder = (Folder)item;
         try
         {
            VirtualFileSystem.getInstance().getChildren(folder,
               new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
               {
                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(exception, RECEIVE_CHILDREN_ERROR_MSG));
                     IDE.fireEvent(new EnableStandartErrorsHandlingEvent());
                  }

                  @Override
                  protected void onSuccess(List<Item> result)
                  {
                     List<Item> items = sortItems(folder, result);
                     updateRowCount(items.size(), true);
                     display.setRowData(0, items);
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }

      public void update()
      {
         onRangeChanged(display);
      }

   }

   /**
    * Comparator for comparing items in received directory.
    */
   private Comparator<Item> comparator = new Comparator<Item>()
   {
      public int compare(Item item1, Item item2)
      {
         if (item1 instanceof Folder && item2 instanceof FileModel)
         {
            return -1;
         }
         else if (item1 instanceof File && item2 instanceof Folder)
         {
            return 1;
         }
         return item1.getName().compareTo(item2.getName());
      }
   };

   private List<Item> sortItems(Folder folder, List<Item> result)
   {
      List<Item> items = new ArrayList<Item>();
      for (Item item : result)
      {
         if (item instanceof ItemContext)
         {
            ItemContext contect = (ItemContext)item;
            contect.setParent(new FolderModel(folder));
            contect.setProject(project);
         }

         if (!DirectoryFilter.get().matchWithPattern(item.getName()))
         {
            items.add(item);
         }
      }

      Collections.sort(items, comparator);

      if (folder instanceof FolderModel)
      {
         ((FolderModel)folder).getChildren().getItems().clear();
         ((FolderModel)folder).getChildren().getItems().addAll(items);
      }
      else if (folder instanceof ProjectModel)
      {
         ((ProjectModel)folder).getChildren().getItems().clear();
         ((ProjectModel)folder).getChildren().getItems().addAll(items);
      }

      return items;
   }

   private ViewModel viewModel;

   private class MyCellTree extends CellTree
   {

      public MyCellTree(TreeViewModel viewModel, Item rootValue)
      {
         super(viewModel, rootValue);
      }

   }

   /**
    * Main widget.
    */
   private CellTree tree;

   private ItemsCellTree instance;

   /**
    * Root Item
    */
   private ProjectModel rootItem;

   private RootItemDataProvider rootItemDataProvider;

   private ProjectModel project;

   private MultiSelectionModel<Item> selectionModel;

   private Map<String, ItemDataProvider> dataProviders = new HashMap<String, ItemDataProvider>();

   /**
    * Creates a new instance of {@link ItemsCellTree}
    */
   public ItemsCellTree()
   {
      instance = this;

      selectionModel = new MultiSelectionModel<Item>();
      selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
      {
         public void onSelectionChange(SelectionChangeEvent event)
         {
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  SelectionEvent.fire(instance, null);
               }
            });
         }
      });

      viewModel = new ViewModel();
      tree = new CellTree(viewModel, null, resources);
      initWidget(tree);
      tree.setAnimationEnabled(true);
      tree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

      tree.addDomHandler(new DoubleClickHandler()
      {
         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            Set<Item> selectedItems = selectionModel.getSelectedSet();
            if (selectedItems.size() > 0)
            {
               List<Item> items = new ArrayList<Item>();
               if (items.get(0) instanceof Folder)
               {
                  open((Folder)items.get(0));
               }
            }
         }
      }, DoubleClickEvent.getType());
   }

   public void setProject(ProjectModel project)
   {
      this.project = project;
      rootItemDataProvider.update();
   }

   @Override
   public HandlerRegistration addSelectionHandler(SelectionHandler<Item> handler)
   {
      return instance.addHandler(handler, SelectionEvent.getType());
   }

   @Override
   public Set<Item> getSelectedSet()
   {
      return selectionModel.getSelectedSet();
   }

   @Override
   public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
   {
      return tree.addDomHandler(handler, DoubleClickEvent.getType());
   }

   @Override
   public void refreshFolder(Folder folder)
   {
      String path = folder.getPath();
      String[] parts = path.split("/");
      TreeNode node = tree.getRootTreeNode();
      String curPath = "";
      for (int i = 1; i < parts.length; i++)
      {
         curPath += "/" + parts[i];
         node = findNodeToRefresh(node, curPath, i == parts.length - 1);
         if (node == null)
         {
            break;
         }
      }
   }

   private TreeNode findNodeToRefresh(TreeNode parent, String path, boolean last)
   {
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         Item item = (Item)parent.getChildValue(i);
         if (path.equals(item.getPath()))
         {
            if (last)
            {
               parent.setChildOpen(i, false);
               return parent.setChildOpen(i, true);
            }
            else
            {
               return parent.setChildOpen(i, true);
            }
         }
      }

      return null;
   }

   @Override
   public boolean open(Folder folder)
   {
      String path = folder.getPath();
      String[] parts = path.split("/");
      TreeNode node = tree.getRootTreeNode();
      String curPath = "";
      for (int i = 1; i < parts.length; i++)
      {
         curPath += "/" + parts[i];
         node = expandNode(node, curPath);
         if (node == null)
         {
            return false;
         }
      }

      return true;
   }

   private TreeNode expandNode(TreeNode parent, String path)
   {
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         Item item = (Item)parent.getChildValue(i);
         if (path.equals(item.getPath()))
         {
            return parent.setChildOpen(i, true);
         }
      }

      return null;
   }

   @Override
   public boolean selectItemByPath(String itemPath)
   {
      return false;
   }

}
