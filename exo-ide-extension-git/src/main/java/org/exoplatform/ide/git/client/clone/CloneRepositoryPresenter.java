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
package org.exoplatform.ide.git.client.clone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.websocket.MessageBus.Channels;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketEventHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketEventMessage;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.github.GitHubCollaboratorsHandler;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshaller;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;

/**
 * Presenter for Clone Repository View.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 4:31:12 PM anya $
 * 
 */
public class CloneRepositoryPresenter extends GitPresenter implements CloneRepositoryHandler
{
   public interface Display extends IsView
   {
      /**
       * Returns working directory field.
       * 
       * @return {@link HasValue<{@link String}>}
       */
      HasValue<String> getWorkDirValue();

      /**
       * Returns remote URI field.
       * 
       * @return {@link HasValue<{@link String}>}
       */
      HasValue<String> getRemoteUriValue();

      /**
       * Returns remote name field.
       * 
       * @return {@link HasValue<{@link String}>}
       */
      HasValue<String> getRemoteNameValue();

      /**
       * Return list of project types
       * @return {@link HasValue<{@link String}>}
       */
      HasValue<String> getProjectType();

      /**
       * @param projectTypes available type of project
       * @param def selected type by default
       */
      void setProjectType(String[] projectTypes, String def);

      /**
       * Returns clone repository button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCloneButton();

      /**
       * Returns cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

      /**
       * Changes the state of clone button.
       * 
       * @param enable
       */
      void enableCloneButton(boolean enable);

      void focusInRemoteUrlField();
   }

   /**
    * Presenter's display.
    */
   private Display display;

   private static final String DEFAULT_REPO_NAME = "origin";

   private FolderModel PROJECT_ROOT_FOLDER;

   private RequestStatusHandler statusHandler;

   /**
    * @param eventBus
    */
   public CloneRepositoryPresenter()
   {
      IDE.addHandler(CloneRepositoryEvent.TYPE, this);
   }

   /**
    * @param d
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getCloneButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doClone(display.getRemoteUriValue().getValue(),//
               display.getRemoteNameValue().getValue(),//
               display.getWorkDirValue().getValue(),//
               display.getProjectType().getValue());
         }
      });

      display.getRemoteUriValue().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String remoteUrl = event.getValue();
            boolean enable = (remoteUrl != null && remoteUrl.length() > 0);
            if (remoteUrl.endsWith("/"))
            {
               remoteUrl = remoteUrl.substring(0, remoteUrl.length() - 1);
            }
            if (remoteUrl.endsWith(".git"))
            {
               remoteUrl = remoteUrl.substring(0, remoteUrl.length() - 4);
               String[] split = remoteUrl.split("/");
               display.getWorkDirValue().setValue(split[split.length - 1]);
            }
            display.enableCloneButton(enable);
         }
      });

      display.setProjectType(
         ProjectResolver.getProjectsTypes().toArray(new String[ProjectResolver.getProjectsTypes().size()]),
         ProjectResolver.UNDEFINED);

   }

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryHandler#onCloneRepository(org.exoplatform.ide.git.client.clone.CloneRepositoryEvent)
    */
   @Override
   public void onCloneRepository(CloneRepositoryEvent event)
   {
      Display d = GWT.create(Display.class);
      IDE.getInstance().openView(d.asView());
      bindDisplay(d);
      display.focusInRemoteUrlField();
      display.getRemoteNameValue().setValue(DEFAULT_REPO_NAME);
      display.enableCloneButton(false);
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
    * @param projectType - type of project
    */
   private void doClone(final String remoteUri, final String remoteName, //
      final String workDir, final String projectType)
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
                  PROJECT_ROOT_FOLDER = result;
                  cloneRepository(remoteUri, remoteName, result, projectType);
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
    * Get the necessary parameters values and call the clone repository method.
    */
   private void cloneRepository(String remoteUri, String remoteName, final FolderModel folder, final String projectType)
   {
      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
      RepoInfo repoInfo = new RepoInfo();
      RepoInfoUnmarshaller unmarshaller = new RepoInfoUnmarshaller(repoInfo);
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = WebSocket.getInstance();
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            statusHandler = new CloneRequestStatusHandler(project.getName(), remoteUri);
            statusHandler.requestInProgress(project.getId());
            ws.messageBus().subscribe(Channels.GIT_REPO_CLONED, repoClonedHandler);
         }
         final boolean useWebSocket = useWebSocketForCallback;

         GitClientService.getInstance().cloneRepository(vfs.getId(), folder, remoteUri, remoteName, useWebSocket,
            new AsyncRequestCallback<RepoInfo>(unmarshaller)
            {

               @Override
               protected void onSuccess(final RepoInfo result)
               {
                  if (!useWebSocket)
                  {
                     IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
                     convertFolderToProject(folder, projectType);
                     //TODO: not good, comment temporary need found other may 
                     // for inviting collaborators 
                     // showInvitation(result.getRemoteUri());
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
                  if (useWebSocket)
                  {
                     ws.messageBus().unsubscribe(Channels.GIT_REPO_CLONED, repoClonedHandler);
                     ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
                     statusHandler.requestError(project.getId(), exception);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         handleError(e);
      }
      catch (WebSocketException e)
      {
         handleError(e);
      }
      IDE.getInstance().closeView(display.asView().getId());
   }

   private void handleError(Throwable e)
   {
      String errorMessage =
         (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.cloneFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

   /**
    * Convert folder to project after cloning
    * @param folder
    * @param projectType
    */
   protected void convertFolderToProject(FolderModel folder, String projectType)
   {
      folder.getProperties().add(new Property("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
      folder.getProperties().add(new Property("vfs:projectType", projectType));
      ProjectModel project = new ProjectModel();
      ItemWrapper item = new ItemWrapper(project);
      ItemUnmarshaller unmarshaller = new ItemUnmarshaller(item);
      try
      {
         VirtualFileSystem.getInstance().updateItem(folder, null, new AsyncRequestCallback<ItemWrapper>(unmarshaller)
         {

            @Override
            protected void onSuccess(ItemWrapper result)
            {
               IDE.fireEvent(new ProjectCreatedEvent((ProjectModel)result.getItem()));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Show dialog window with proposal for invite commiters.
    * In case clone repository from GitHub show Collaborators list (see GitHub REST API http://developer.github.com/v3/repos/collaborators/).
    * Else on server side we get unique list of commiters: name and email.  
    * 
    * @param remoteUri
    */
   protected void showInvitation(String remoteUri)
   {
      String[] userRepo = parseGitHubUrl(remoteUri);
      if (userRepo != null)
      {
         GitHubCollaboratorsHandler collaboratorsHandler = new GitHubCollaboratorsHandler();
         collaboratorsHandler.showCollaborators(userRepo[0], userRepo[1]);
      }

   }

   /**
    * Parse GitHub url. Need extract "user" and "repository" name.
    * If given Url its GitHub url return array of string first element will be user name, second repository name
    * else return null.
    * GitHub url formats:
    * - https://github.com/user/repo.git
    * - git@github.com:user/repo.git
    * - git://github.com/user/repo.git
    * 
    * @param gitUrl
    * @return array of string 
    */
   private String[] parseGitHubUrl(String gitUrl)
   {
      if (gitUrl.endsWith("/"))
      {
         gitUrl = gitUrl.substring(0, gitUrl.length() - 1);
      }
      if (gitUrl.endsWith(".git"))
      {
         gitUrl = gitUrl.substring(0, gitUrl.length() - 4);
      }
      if (gitUrl.startsWith("git@github.com:"))
      {
         gitUrl = gitUrl.split("git@github.com:")[1];
         return gitUrl.split("/");
      }
      else if (gitUrl.startsWith("git://github.com/"))
      {
         gitUrl = gitUrl.split("git://github.com/")[1];
         return gitUrl.split("/");
      }
      else if (gitUrl.startsWith("https://github.com/"))
      {
         gitUrl = gitUrl.split("git://github.com/")[1];
         return gitUrl.split("/");
      }
      return null;
   }

   @Override
   protected boolean makeSelectionCheck()
   {
      return true;
   }

   /**
    * Performs actions after the Git-repository was cloned.
    */
   private WebSocketEventHandler repoClonedHandler = new WebSocketEventHandler()
   {
      @Override
      public void onMessage(WebSocketEventMessage event)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.GIT_REPO_CLONED, this);

         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
         statusHandler.requestFinished(project.getId());

         IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
         convertFolderToProject(PROJECT_ROOT_FOLDER, display.getProjectType().getValue());
         //TODO: not good, comment temporary need found other may 
         // for inviting collaborators 
         // showInvitation(result.getRemoteUri());
      }

      @Override
      public void onError(Exception exception)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.GIT_REPO_CLONED, this);

         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
         statusHandler.requestError(project.getId(), exception);
         handleError(exception);
      }
   };
}
