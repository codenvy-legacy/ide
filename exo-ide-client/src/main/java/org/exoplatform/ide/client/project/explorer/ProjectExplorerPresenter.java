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

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.SelectionChangeEvent.HasSelectionChangedHandlers;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectExplorerPresenter implements ShowProjectExplorerHandler, ViewClosedHandler, ItemsSelectedHandler
{
   
   public interface Display extends IsView
   {
      
      HasSelectionChangedHandlers getSelectionModel();

      void initialize(ProjectModel project);
      
   }
   
   private Display display;
   
   private ProjectModel selectedProject;
   
   public ProjectExplorerPresenter() {
      IDE.getInstance().addControl(new ShowProjectExplorerControl(), Docking.TOOLBAR, false);
      
      IDE.EVENT_BUS.addHandler(ShowProjectExplorerEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   @Override
   public void onShowProjectExplorer(ShowProjectExplorerEvent event)
   {
      if (display != null) {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }
      
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }
   
   private void bindDisplay() {
      if (selectedProject == null) {
         Window.alert("Yo! Selected project is NULL!!!");
         return;
      }
      
      display.initialize(selectedProject);
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display) {
         display = null;
      }
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1) {
         selectedProject = null;
         return;
      }

      Item item = event.getSelectedItems().get(0);
      if (!(item instanceof ProjectModel)) {
         selectedProject = null;
         return;
      }
      
      selectedProject = (ProjectModel)item;
   }

}
