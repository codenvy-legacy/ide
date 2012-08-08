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

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import org.exoplatform.ide.shared.model.File;

import java.util.ArrayList;
import java.util.List;

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

      HasDoubleClickHandlers getTree();

      // TODO : should get the list of object model
      void setItems(List<String> fileNames);
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
   public void go(HasWidgets container)
   {
      container.clear();
      container.add(display.asWidget());

      // Set up the callback object.
      AsyncCallback<File[]> callback = new AsyncCallback<File[]>()
      {
         public void onFailure(Throwable caught)
         {
         }

         public void onSuccess(File[] result)
         {
            updateFileList(result);
         }
      };

      fileSystemService.getFileList(callback);
   }

   protected void bind()
   {
      display.getTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            String string = display.getSelectedFileName();
            openFile(string);
         }
      });
   }

   protected void openFile(String fileName)
   {
      eventBus.fireEvent(new FileEvent(fileName, FileOperation.OPEN));
   }

   protected void updateFileList(File[] result)
   {
      List<String> names = new ArrayList<String>();
      for (File file : result)
      {
         names.add(file.getName());
      }
      display.setItems(names);
   }

}
