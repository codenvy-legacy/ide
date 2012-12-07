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
package org.exoplatform.ide.git.client;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.codenow.CodeNowSpec10;
import org.exoplatform.ide.client.framework.codenow.StartWithInitParamsEvent;
import org.exoplatform.ide.client.framework.codenow.StartWithInitParamsHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocket.ReadyState;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestCallback;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshallerWS;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CodeNowHandler.java Dec 6, 2012 vetal $
 *
 */
public class CodeNowHandler implements VfsChangedHandler, StartWithInitParamsHandler
{

   private VirtualFileSystemInfo vfs;

   public CodeNowHandler()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(StartWithInitParamsEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   @Override
   public void onStartWithInitParams(StartWithInitParamsEvent event)
   {
      if (isValidParam(event.getParameterMap()))
      {
         String giturl = event.getParameterMap().get(CodeNowSpec10.VCS_URL).get(0);

         String prjName = null;
       
         if (event.getParameterMap().get(CodeNowSpec10.PROJECT_NAME) != null
            && !event.getParameterMap().get(CodeNowSpec10.PROJECT_NAME).isEmpty())
         {
            prjName = event.getParameterMap().get(CodeNowSpec10.PROJECT_NAME).get(0);
         }
         else
         {
            prjName = giturl.substring(giturl.lastIndexOf('/') + 1, giturl.lastIndexOf(".git"));
         }

         cloneProject(giturl, prjName);
      }

   }

   /**
    * @param initParam
    */
   private boolean isValidParam(Map<String, List<String>> initParam)
   {
      if (initParam == null || initParam.isEmpty())
      {
         return false;
      }
      if (!initParam.containsKey(CodeNowSpec10.VERSION_PARAMETER)
         || initParam.get(CodeNowSpec10.VERSION_PARAMETER).size() != 1
         || !initParam.get(CodeNowSpec10.VERSION_PARAMETER).get(0).equals(CodeNowSpec10.CURRENT_VERSION))
      {
         return false;
      }
      if (!initParam.containsKey(CodeNowSpec10.VCS) || initParam.get(CodeNowSpec10.VCS).isEmpty()
         || !initParam.get(CodeNowSpec10.VCS).get(0).equalsIgnoreCase(CodeNowSpec10.DEFAULT_VCS))
      {
         return false;
      }
      if (!initParam.containsKey(CodeNowSpec10.VCS_URL) || initParam.get(CodeNowSpec10.VCS_URL) == null
         || initParam.get(CodeNowSpec10.VCS_URL).isEmpty())
      {
         return false;
      }
      return true;
   }

   private void cloneProject(final String giturl, final String prjName)
   {
      try
      {

         VirtualFileSystem.getInstance().getChildren(vfs.getRoot(), ItemType.PROJECT,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  boolean itemExist = false;
                  for (Item item : result)
                  {
                     if (item.getName().equals(prjName))
                     {
                        itemExist = true;
                     }
                     if (item.hasProperty("codenow"))
                     {
                        String codenow = item.getPropertyValue("codenow");
                        if (codenow.equals(giturl))
                        {
                           IDE.fireEvent(new OpenProjectEvent((ProjectModel)item));
                           return;
                        }
                     }
                  }
                  if (itemExist)
                  {
                     doClone(giturl, "origin", prjName + "-" + Random.nextInt(Integer.MAX_VALUE));
                  }
                  else
                  {
                     doClone(giturl, "origin", prjName);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  doClone(giturl, "origin", prjName);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Going to cloning repository.
    * Clone process flow 3 steps:
    * - create new folder with name workDir
    * - clone repository to this folder
    * - convert folder to project.
    *  This need because by default project with out file and folder not empty.
    *  It content ".project" item. Clone is impossible to not empty folder    
    * @param remoteUri - git url
    * @param remoteName - remote name (by default origin)
    * @param workDir - name of target folder 
    */
   public void doClone(final String remoteUri, final String remoteName, final String workDir)
   {
      FolderModel folder = new FolderModel();
      folder.setName(workDir);
      try
      {
         VirtualFileSystem.getInstance().createFolder(vfs.getRoot(),
            new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(folder))
            {
               @Override
               protected void onSuccess(FolderModel result)
               {
                  cloneRepository(remoteUri, remoteName, result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : GitExtension.MESSAGES.cloneFailed();
                  IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES
               .cloneFailed();
         IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
      }
   }

   /**
    * Clone of the repository by sending request over WebSocket or HTTP.
    */
   private void cloneRepository(final String remoteUri, final String remoteName, final FolderModel folder)
   {
      if (WebSocket.getInstance().getReadyState() == ReadyState.OPEN)
         cloneRepositoryWS(remoteUri, remoteName, folder);
      else
         cloneRepositoryREST(remoteUri, remoteName, folder);
   }

   /**
    * Get the necessary parameters values and call the clone repository method (over HTTP).
    */
   private void cloneRepositoryREST(String remoteUri, String remoteName, final FolderModel folder)
   {
      try
      {
         GitClientService.getInstance().cloneRepository(vfs.getId(), folder, remoteUri, remoteName,
            new AsyncRequestCallback<RepoInfo>(new RepoInfoUnmarshaller(new RepoInfo()))
            {
               @Override
               protected void onSuccess(RepoInfo result)
               {
                  onCloneSuccess(folder, result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
               }
            });
      }
      catch (RequestException e)
      {
         handleError(e);
      }
   }

   /**
    * Get the necessary parameters values and clone repository (over WebSocket).
    *
    * @param remoteUri the location of the remote repository
    * @param remoteName remote name instead of "origin"
    * @param folder folder (root of GIT repository)
    */
   private void cloneRepositoryWS(String remoteUri, String remoteName, final FolderModel folder)
   {
      try
      {
         GitClientService.getInstance().cloneRepositoryWS(vfs.getId(), folder, remoteUri, remoteName,
            new RESTfulRequestCallback<RepoInfo>(new RepoInfoUnmarshallerWS(new RepoInfo()))
            {

               @Override
               protected void onSuccess(RepoInfo result)
               {
                  onCloneSuccess(folder, result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
               }
            });
      }
      catch (WebSocketException e)
      {
         handleError(e);
      }
   }

   /**
    * Perform actions when repository was successfully cloned.
    *
    * @param folder {@link FolderModel} to clone
    */
   private void onCloneSuccess(FolderModel folder, RepoInfo repoInfo)
   {
      IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
      //TODO: not good, comment temporary need found other way
      // for inviting collaborators
      // showInvitation(repoInfo.getRemoteUri());

      List<Property> properties = new ArrayList<Property>();
      properties.add(new PropertyImpl("codenow", repoInfo.getRemoteUri()));

      IDE.fireEvent(new ConvertToProjectEvent(folder.getId(), vfs.getId(), properties));
   }

   private void handleError(Throwable e)
   {
      String errorMessage =
         (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.cloneFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

}
