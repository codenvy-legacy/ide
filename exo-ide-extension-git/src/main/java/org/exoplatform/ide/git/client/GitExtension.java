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
package org.exoplatform.ide.git.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.client.framework.event.StartWithInitParamsEvent;
import org.exoplatform.ide.client.framework.event.StartWithInitParamsHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemEvent;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocket.ReadyState;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestCallback;
import org.exoplatform.ide.client.framework.websocket.messages.SubscriptionHandler;
import org.exoplatform.ide.git.client.add.AddToIndexPresenter;
import org.exoplatform.ide.git.client.branch.BranchPresenter;
import org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter;
import org.exoplatform.ide.git.client.commit.CommitPresenter;
import org.exoplatform.ide.git.client.control.AddFilesControl;
import org.exoplatform.ide.git.client.control.BranchesControl;
import org.exoplatform.ide.git.client.control.CloneRepositoryControl;
import org.exoplatform.ide.git.client.control.CommitControl;
import org.exoplatform.ide.git.client.control.DeleteRepositoryControl;
import org.exoplatform.ide.git.client.control.FetchControl;
import org.exoplatform.ide.git.client.control.InitRepositoryControl;
import org.exoplatform.ide.git.client.control.MergeControl;
import org.exoplatform.ide.git.client.control.PullControl;
import org.exoplatform.ide.git.client.control.PushToRemoteControl;
import org.exoplatform.ide.git.client.control.RemoteControl;
import org.exoplatform.ide.git.client.control.RemotesControl;
import org.exoplatform.ide.git.client.control.RemoveFilesControl;
import org.exoplatform.ide.git.client.control.ResetFilesControl;
import org.exoplatform.ide.git.client.control.ResetToCommitControl;
import org.exoplatform.ide.git.client.control.ShowHistoryControl;
import org.exoplatform.ide.git.client.control.ShowProjectGitReadOnlyUrl;
import org.exoplatform.ide.git.client.control.ShowStatusControl;
import org.exoplatform.ide.git.client.delete.DeleteRepositoryCommandHandler;
import org.exoplatform.ide.git.client.fetch.FetchPresenter;
import org.exoplatform.ide.git.client.history.HistoryPresenter;
import org.exoplatform.ide.git.client.init.InitRepositoryPresenter;
import org.exoplatform.ide.git.client.init.ShowProjectGitReadOnlyUrlPresenter;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshallerWS;
import org.exoplatform.ide.git.client.merge.MergePresenter;
import org.exoplatform.ide.git.client.pull.PullPresenter;
import org.exoplatform.ide.git.client.push.PushToRemotePresenter;
import org.exoplatform.ide.git.client.remote.RemotePresenter;
import org.exoplatform.ide.git.client.remove.RemoveFilesPresenter;
import org.exoplatform.ide.git.client.reset.ResetFilesPresenter;
import org.exoplatform.ide.git.client.reset.ResetToCommitPresenter;
import org.exoplatform.ide.git.client.status.StatusCommandHandler;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.List;
import java.util.Map;

/**
 * Git extension to be added to IDE application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 12:53:29 PM anya $
 * 
 */
public class GitExtension extends Extension implements InitializeServicesHandler, StartWithInitParamsHandler,
   ProjectOpenedHandler
{

   public static final GitLocalizationConstant MESSAGES = GWT.create(GitLocalizationConstant.class);

   public static final GitAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(GitAutoBeanFactory.class);

   private ProjectModel project;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.addHandler(StartWithInitParamsEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);

      // Add controls:
      IDE.getInstance().addControl(new InitRepositoryControl());
      IDE.getInstance().addControl(new CloneRepositoryControl());
      IDE.getInstance().addControl(new DeleteRepositoryControl());
      IDE.getInstance().addControl(new AddFilesControl());
      IDE.getInstance().addControl(new ResetFilesControl());
      IDE.getInstance().addControl(new ResetToCommitControl());
      IDE.getInstance().addControl(new RemoveFilesControl());
      IDE.getInstance().addControl(new CommitControl());
      IDE.getInstance().addControl(new BranchesControl());
      IDE.getInstance().addControl(new MergeControl());
      IDE.getInstance().addControl(new PushToRemoteControl());
      IDE.getInstance().addControl(new FetchControl());
      IDE.getInstance().addControl(new PullControl());
      IDE.getInstance().addControl(new RemoteControl());
      IDE.getInstance().addControl(new RemotesControl());

      IDE.getInstance().addControl(new ShowHistoryControl());
      IDE.getInstance().addControl(new ShowStatusControl());
      IDE.getInstance().addControl(new ShowProjectGitReadOnlyUrl());
      IDE.getInstance().addControlsFormatter(new GitControlsFormatter());

      // Create presenters:
      new CloneRepositoryPresenter();
      new InitRepositoryPresenter();
      new StatusCommandHandler();
      new AddToIndexPresenter();
      new RemoveFilesPresenter();
      new ResetFilesPresenter();
      new ResetToCommitPresenter();
      new RemotePresenter();

      new CommitPresenter();
      new PushToRemotePresenter();
      new BranchPresenter();
      new FetchPresenter();
      new PullPresenter();
      new HistoryPresenter();
      new DeleteRepositoryCommandHandler();
      new MergePresenter();

      new ShowProjectGitReadOnlyUrlPresenter();

   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      project = event.getProject();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new GitClientServiceImpl(event.getApplicationConfiguration().getContext(), event.getLoader());
   }

   @Override
   public void onStartWithInitParams(StartWithInitParamsEvent event)
   {
      initParam = event.getParameterMap();
      if (initParam != null && !initParam.isEmpty())
      {
         if (!initParam.containsKey("v"))
            return;
         if (initParam.get("v").size() != 1 || !initParam.get("v").get(0).equals("codenow1.0"))
            return;
         if (!initParam.containsKey("storageURL") || !initParam.containsKey("storageType"))
            return;
         List<String> giturls = initParam.get("storageURL");
         if (giturls != null && !giturls.isEmpty())
         {
            String giturl = giturls.get(0);
            doClone(giturl, "origin", initParam.get("projectName").get(0) + "-" + Random.nextInt(),
               ProjectType.JAR.value());
         }
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
    * @param projectType - type of project
    */
   public void doClone(final String remoteUri, final String remoteName, //
      final String workDir, final String projectType)
   {
      FolderModel folder = new FolderModel();
      folder.setName(workDir);
      try
      {
         VirtualFileSystem.getInstance().createFolder(VirtualFileSystem.getInstance().getInfo().getRoot(),
            new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(folder))
            {
               @Override
               protected void onSuccess(FolderModel result)
               {
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
    * Clone of the repository by sending request over WebSocket or HTTP.
    */
   private void cloneRepository(String remoteUri, String remoteName, final FolderModel folder, final String projectType)
   {
      if (WebSocket.getInstance().getReadyState() == ReadyState.OPEN)
         cloneRepositoryWS(remoteUri, remoteName, folder, projectType);
      else
         cloneRepositoryREST(remoteUri, remoteName, folder, projectType);
   }

   /**
    * Get the necessary parameters values and call the clone repository method (over HTTP).
    */
   private void cloneRepositoryREST(String remoteUri, String remoteName, final FolderModel folder,
      final String projectType)
   {
      try
      {
         GitClientService.getInstance().cloneRepository(VirtualFileSystem.getInstance().getInfo().getId(), folder,
            remoteUri, remoteName, new AsyncRequestCallback<RepoInfo>(new RepoInfoUnmarshaller(new RepoInfo()))
            {
               @Override
               protected void onSuccess(RepoInfo result)
               {
                  onCloneSuccess(folder, projectType);
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
    * @param projectType type of project which will be created from cloned repository
    */
   private void cloneRepositoryWS(String remoteUri, String remoteName, final FolderModel folder,
      final String projectType)
   {
      try
      {
         GitClientService.getInstance().cloneRepositoryWS(VirtualFileSystem.getInstance().getInfo().getId(), folder,
            remoteUri, remoteName, new RESTfulRequestCallback<RepoInfo>(new RepoInfoUnmarshallerWS(new RepoInfo()))
            {

               @Override
               protected void onSuccess(RepoInfo result)
               {
                  onCloneSuccess(folder, projectType);
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
    * Handler for processing received application name which will be stopped soon.
    */
   private SubscriptionHandler<Object> handler = new SubscriptionHandler<Object>()
   {
      @Override
      public void onSuccess(Object result)
      {
         WebSocket.getInstance().unsubscribe("/ide/codenow/file-appear-listener", this);
         if (initParam != null && !initParam.isEmpty())
         {

            if (!initParam.containsKey("v"))
               return;
            if (initParam.get("v").size() != 1 || !initParam.get("v").get(0).equals("codenow1.0"))
               return;

            int curx = 0, cury = 0;
            if (initParam.containsKey("curx"))
            {
               List<String> list = initParam.get("curx");
               if (!list.isEmpty())
               {
                  try
                  {
                     curx = Integer.parseInt(list.get(0));
                  }
                  catch (NumberFormatException ignore)
                  {
                     //Nothing todo
                  }
               }
            }

            if (initParam.containsKey("cury"))
            {
               List<String> list = initParam.get("cury");
               if (!list.isEmpty())
               {
                  try
                  {
                     cury = Integer.parseInt(list.get(0));
                  }
                  catch (NumberFormatException ignor)
                  {
                     //Nothing todo
                  }
               }
            }

            final CursorPosition cursorPosition = new CursorPosition(cury, curx);

            List<String> openFilePaths = initParam.get("openFilePath");
            if (openFilePaths != null && !openFilePaths.isEmpty())
            {
               String openFilePath = openFilePaths.get(0);
               String filePath = project.getPath() + openFilePath;
               try
               {
                  VirtualFileSystem.getInstance().getItemByPath(filePath,
                     new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(new FileModel())))
                     {

                        @Override
                        protected void onSuccess(ItemWrapper result)
                        {
                           result.getItem();
                           FileModel fileModel = new FileModel((File)result.getItem());
                           fileModel.setProject(project);
                           IDE.fireEvent(new GoToItemEvent(fileModel, cursorPosition, true));
                        }

                        @Override
                        protected void onFailure(Throwable exception)
                        {
                           Dialogs.getInstance().showError(exception.getMessage());
                        }
                     });
               }
               catch (RequestException e)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(e));
               }

            }
         }
      };

      @Override
      public void onFailure(Throwable exception)
      {
         WebSocket.getInstance().unsubscribe("/ide/codenow/file-appear-listener", this);
      }
   };

   private Map<String, List<String>> initParam;

   /**
    * Perform actions when repository was successfully cloned.
    * 
    * @param folder {@link FolderModel} to clone
    * @param projectType type of the project which will be created
    */
   private void onCloneSuccess(FolderModel folder, String projectType)
   {
      IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
      WebSocket.getInstance().subscribe("/ide/codenow/file-appear-listener", handler);
      List<String> openFilePaths = initParam.get("openFilePath");
      if (openFilePaths != null && !openFilePaths.isEmpty())
      {
         String openFilePath = openFilePaths.get(0);
         StringBuilder builder = new StringBuilder();
         try
         {
            AsyncRequest.build(
               RequestBuilder.GET,
               Utils.getRestContext() + "/ide/codenow/file-appear-listener/"
                  + VirtualFileSystem.getInstance().getInfo().getId() + "?" + folder.getPath() + openFilePath).send(
               new AsyncRequestCallback<StringBuilder>( new StringUnmarshaller(builder))
               {
                  @Override
                  protected void onSuccess(StringBuilder result)
                  {
                     // TODO Auto-generated method stub
                     
                  }
                  
                  protected void onFailure(Throwable exception) {
                     
                  };
               });
         }
         catch (RequestException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

      }
      convertFolderToProject(folder, projectType);
      //TODO: not good, comment temporary need found other way 
      // for inviting collaborators
      // showInvitation(result.getRemoteUri());
   }

   private void handleError(Throwable e)
   {
      String errorMessage =
         (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.cloneFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

   /**
    * Convert folder to project after cloning.
    * 
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

}
