/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.projectExplorer;

import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.client.event.FileEvent;
import org.exoplatform.ide.client.event.FileEvent.FileOperation;
import org.exoplatform.ide.client.presenter.Presenter;
import org.exoplatform.ide.client.services.FileSystemServiceAsync;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Resource;

/**
 * Tree-like project explorer.
 * TODO : should accept resource model objects.
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 27, 2012  
 */
public class ProjectExplorerPresenter implements Presenter
{

   public interface Display extends IsWidget
   {
      String getSelectedFileName();

      @Deprecated
      HasDoubleClickHandlers getTree();

      void registerListener(Listener listener);

      void setItems(Resource resource);
   }

   public interface Listener
   {
      void onNodeAction(Resource resource);
   }

   Display display;

   EventBus eventBus;

   FileSystemServiceAsync fileSystemService;

   @Inject
   public ProjectExplorerPresenter(Display display, FileSystemServiceAsync fileSystemService, EventBus eventBus)
   {
      this.display = display;
      this.fileSystemService = fileSystemService;
      this.eventBus = eventBus;
      bind();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(HasWidgets container)
   {
      container.clear();
      container.add(display.asWidget());

//      // Set up the callback object.
//      AsyncCallback<File[]> callback = new AsyncCallback<File[]>()
//      {
//         @Override
//         public void onFailure(Throwable caught)
//         {
//         }
//
//         @Override
//         public void onSuccess(File[] result)
//         {
//            updateFileList(result);
//         }
//      };

      //fileSystemService.getFileList(callback);
   }

   public void setContent(Resource resource)
   {
      display.setItems(resource);
   }

   protected void bind()
   {
      display.registerListener(new Listener()
      {
         @Override
         public void onNodeAction(Resource resource)
         {
            openFile(resource);
         }
      });
      //      display.getTree().addDoubleClickHandler(new DoubleClickHandler()
      //      {
      //         public void onDoubleClick(DoubleClickEvent event)
      //         {
      //            String string = display.getSelectedFileName();
      //            openFile(string);
      //         }
      //      });
   }

   protected void openFile(Resource resource)
   {
      if (resource.isFile())
      {
         eventBus.fireEvent(new FileEvent((File)resource, FileOperation.OPEN));
      }
   }

   protected void updateFileList(File[] result)
   {
      //      List<String> names = new ArrayList<String>();
      //      for (File file : result)
      //      {
      //         names.add(file.getName());
      //      }
      //      display.setItems(names);
   }

}
