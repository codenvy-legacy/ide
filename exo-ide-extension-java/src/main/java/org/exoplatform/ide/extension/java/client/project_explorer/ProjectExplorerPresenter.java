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

package org.exoplatform.ide.extension.java.client.project_explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.client.JavaClientService;
import org.exoplatform.ide.extension.java.shared.ast.AstItem;
import org.exoplatform.ide.extension.java.shared.ast.JavaProject;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectExplorerPresenter implements ShowProjectExplorerHandler, ItemsSelectedHandler, ViewClosedHandler, VfsChangedHandler
{

   public interface Display extends IsView
   {
      
      void initializeASTTree(ASTTreeViewModel viewModel, SelectionModel<?> selectionModel);
      
      HasDoubleClickHandlers getProjectCellTree();

   }
   
   private Display display;
   
   private MultiSelectionModel<AstItem> selectionModel;
   
   private List<Item> selectedItems = new ArrayList<Item>();
   
   private VirtualFileSystemInfo vfsInfo;
   
   private List<JavaProject> projects;

   public ProjectExplorerPresenter()
   {
      IDE.getInstance().addControl(new ShowProjectExplorerControl());
      
      IDE.addHandler(ShowProjectExplorerEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   @Override
   public void onShowProjectExplorer(ShowProjectExplorerEvent event)
   {
      if (display != null) {
         return;
      }

      String vfsId = vfsInfo.getId();
      JavaClientService.getInstance().getProjects(vfsId, new AsyncRequestCallback<List<JavaProject>>()
      {
         @Override
         protected void onSuccess(List<JavaProject> result)
         {
            projects = result;
            
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
         }
      });
      
   }
   
   private SelectionChangeEvent.Handler selectionChangeHandler = new SelectionChangeEvent.Handler()
   {
      @Override
      public void onSelectionChange(SelectionChangeEvent event)
      {
      }
   };
   
   private void bindDisplay() {
      selectionModel = new MultiSelectionModel<AstItem>();
      selectionModel.addSelectionChangeHandler(selectionChangeHandler);

      ASTTreeViewModel viewModel = new ASTTreeViewModel(selectionModel, projects);
      display.initializeASTTree(viewModel, selectionModel);
      
      selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
      {
         @Override
         public void onSelectionChange(SelectionChangeEvent event)
         {
            Set<AstItem> selectedItems = selectionModel.getSelectedSet();
         }
      });
      
      display.getProjectCellTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            Set<AstItem> selectedItems = selectionModel.getSelectedSet();
         }
      });
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
      
      if (event.getSelectedItems().size() != 1) {
         return;
      }
      
      if (event.getSelectedItems().get(0) instanceof FileModel) {
      } else if (event.getSelectedItems().get(0) instanceof FolderModel) {
      } else if (event.getSelectedItems().get(0) instanceof ProjectModel) {
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display) {
         display = null;
      }
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
   }

}
