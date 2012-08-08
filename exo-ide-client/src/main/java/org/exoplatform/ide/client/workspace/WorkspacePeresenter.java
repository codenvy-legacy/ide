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
package org.exoplatform.ide.client.workspace;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.client.editor.EditorPresenter;
import org.exoplatform.ide.client.event.FileEvent;
import org.exoplatform.ide.client.event.FileEvent.FileOperation;
import org.exoplatform.ide.client.event.FileEventHandler;
import org.exoplatform.ide.client.presenter.Presenter;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter;
import org.exoplatform.ide.client.services.FileSystemServiceAsync;

/**
 * Root Presenter that implements Workspace logic. Descendant Presenters are injected via
 * constructor and exposed to coresponding UI containers.
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 24, 2012  
 */
public class WorkspacePeresenter implements Presenter
{

   public interface Display extends IsWidget
   {
      HasWidgets getCenterPanel();

      // TODO : temporary
      void clearCenterPanel();

      HasWidgets getLeftPanel();
   }

   Display display;

   EditorPresenter editorPresenter;

   EventBus eventBus;

   FileSystemServiceAsync fileSystemService;

   ProjectExplorerPresenter projectExpolorerPresenter;

   @Inject
   public WorkspacePeresenter(Display display, ProjectExplorerPresenter projectExpolorerPresenter,
      EditorPresenter editorPresenter, EventBus eventBus, FileSystemServiceAsync fileSystemService)
   {
      super();
      this.display = display;
      this.projectExpolorerPresenter = projectExpolorerPresenter;
      this.editorPresenter = editorPresenter;
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
      // Expose Project Explorer into Tools Panel 
      projectExpolorerPresenter.go(display.getLeftPanel());
      container.add(display.asWidget());
   }

   protected void bind()
   {
      eventBus.addHandler(FileEvent.TYPE, new FileEventHandler()
      {

         public void onFileOperation(final FileEvent event)
         {
            if (event.getOperationType() == FileOperation.OPEN)
            {
               // Set up the callback object.
               AsyncCallback<String> callback = new AsyncCallback<String>()
               {
                  public void onFailure(Throwable caught)
                  {
                     // TODO : Handle failure
                  }

                  public void onSuccess(String result)
                  {
                     openFile(event.getFileName(), result);
                  }
               };

               fileSystemService.getFileContent(event.getFileName(), callback);
            }
            else 
            if (event.getOperationType() == FileOperation.CLOSE)
            {
               // close associated editor. OR it can be closed itself TODO
            }
         }
      });
   }

   protected void openFile(String fileName, String content)
   {
      // Calling display.getCenterPanel.cler() violates the Law of Demeter
      display.clearCenterPanel();
      editorPresenter.setText(content);
      editorPresenter.go(display.getCenterPanel());
   }

}
