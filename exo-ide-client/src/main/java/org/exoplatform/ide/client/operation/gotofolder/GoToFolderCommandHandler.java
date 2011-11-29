//TODO: need rework according new VFS
/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.gotofolder;

import org.exoplatform.ide.client.Alert;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

//TODO IDE-1234
public class GoToFolderCommandHandler implements GoToFolderHandler, 
   VfsChangedHandler, EditorActiveFileChangedHandler
{

   private String pathToOpen;

   private ArrayList<String> pathes;

   private String entryPoint;

   private FileModel activeFile;
   
   private static final String RECEIVE_CHILDREN_FAILURE = IDE.ERRORS_CONSTANT.goToFolderReceiveChildrenFailure();

   public GoToFolderCommandHandler()
   {
      IDE.getInstance().addControl(new GoToFolderControl());      

      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
//      IDE.addHandler(GoToFolderEvent.TYPE, this);
   }

   public void onVfsChanged(VfsChangedEvent event)
   {
      entryPoint = (event.getVfsInfo() != null) ? event.getVfsInfo().getId() : null;
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * Go To Folder command handler
    * 
    */
   public void onGoToFolder(GoToFolderEvent event)
   {
      Alert.alert("GO TO FOLDER!!!!!!!!!!!");
      
      //TODO
//      if (activeFile == null)
//      {
//         return;
//      }
//
//      String workingPath = activeFile.getHref();
//
//      String entryPoint = this.entryPoint.substring(0, this.entryPoint.lastIndexOf("/"));
//      entryPoint = entryPoint.substring(0, entryPoint.lastIndexOf("/"));
//      workingPath = workingPath.substring(entryPoint.length(), workingPath.length());
//
//      while (workingPath.startsWith("/"))
//      {
//         workingPath = workingPath.substring(1);
//      }
//
//      workingPath = workingPath.substring(0, workingPath.lastIndexOf("/"));
//      String[] p = workingPath.split("/");
//      pathes = new ArrayList<String>();
//      pathToOpen = entryPoint + "/" + p[0] + "/";
//      if (p.length > 1)
//      {
//         for (int i = 1; i < p.length; i++)
//         {
//            pathes.add(p[i]);
//         }
//      }
//
//      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(pathToOpen)));
//      openNextFolder(new Folder(pathToOpen));
   }

//TODO   
//   public void openNextFolder(Folder folderToOpen)
//   {
//      /*
//       * Folder content received handler.
//       * Get subfolder content here
//       */
//      VirtualFileSystem.getInstance().getChildren(folderToOpen, new AsyncRequestCallback<Folder>()
//      {
//         
//         @Override
//         protected void onSuccess(Folder result)
//         {
//           
//            new Timer()
//            {
//               @Override
//               public void run()
//               {
//                  if (pathes.size() > 0)
//                  {
//                     String name = pathes.get(0);
//                     pathes.remove(0);
//                     pathToOpen += name + "/";
//                     eventBus.fireEvent(new RefreshBrowserEvent(new Folder(pathToOpen)));
//                     openNextFolder(new Folder(pathToOpen));
//                  }
//                  else
//                  {
//                     // try to select file.........
//                     eventBus.fireEvent(new SelectItemEvent(activeFile.getHref()));
//                  }
//               }
//            }.schedule(100);
//         }
//         
//         @Override
//         protected void onFailure(Throwable exception)
//         {
//            exception.printStackTrace();
//            IDE.fireEvent(new ExceptionThrownEvent(exception, RECEIVE_CHILDREN_FAILURE));
//         }
//      });
//   }

}
