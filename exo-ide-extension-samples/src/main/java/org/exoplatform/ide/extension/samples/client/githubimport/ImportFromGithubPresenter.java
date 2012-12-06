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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestCallback;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;
import org.exoplatform.ide.extension.samples.client.marshal.RepositoriesUnmarshaller;
import org.exoplatform.ide.extension.samples.client.oauth.OAuthLoginEvent;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.github.GitHubClientService;
import org.exoplatform.ide.git.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Presenter for importing user's GitHub project to IDE.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubPresenter.java Dec 7, 2011 3:37:11 PM vereshchaka $
 *
 */
public class ImportFromGithubPresenter implements ShowImportFromGithubHandler, ViewClosedHandler,
   UserInfoReceivedHandler, VfsChangedHandler
{
   public interface Display extends IsView
   {
      /**
       * Returns project's name field.
       *
       * @return {@link HasValue} project's name field
       */
      HasValue<String> getProjectNameField();

      /**
       * Returns read only mode of the Git repository field.
       *
       * @return {@link HasValue} read only mode of the Git repository
       */
      HasValue<Boolean> getReadOnlyModeField();

      /**
       * Returns next button's click handler.
       *
       * @return {@link HasClickHandlers} button's click handler
       */
      HasClickHandlers getFinishButton();

      /**
       * Returns cancel button's click handler.
       *
       * @return {@link HasClickHandlers} button's click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Returns repositories list grid.
       *
       * @return {@link ListGridItem} repositories list grid
       */
      ListGridItem<ProjectData> getRepositoriesGrid();

      /**
       * Set the enabled state of the next button.
       *
       * @param enabled enabled state of the next button
       */
      void setFinishButtonEnabled(boolean enabled);
   }

   private UserInfo userInfo;

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * Map of read-only URLs. Key is ssh Git URL - value is read-only Git URL.
    */
   private HashMap<String, String> readonlyUrls = new HashMap<String, String>();

   private ProjectData data;

   private VirtualFileSystemInfo vfs;

   public ImportFromGithubPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ShowImportFromGithubEvent.TYPE, this);
      IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   private void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getRepositoriesGrid().addSelectionHandler(new SelectionHandler<ProjectData>()
      {

         @Override
         public void onSelection(SelectionEvent<ProjectData> event)
         {
            if (event.getSelectedItem() != null)
            {
               data = event.getSelectedItem();
               display.getProjectNameField().setValue(event.getSelectedItem().getName());
               display.setFinishButtonEnabled(true);
            }
         }
      });

      display.getFinishButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            if (data != null && !display.getProjectNameField().getValue().isEmpty())
            {
               createFolder();
            }
         }
      });
   }

   /**
    * Get the list of authorized user's repositories.
    */
   private void getUserRepos()
   {
      try
      {
         GitHubClientService.getInstance().getRepositoriesList(
            new AsyncRequestCallback<List<GitHubRepository>>(new RepositoriesUnmarshaller(
               new ArrayList<GitHubRepository>()))
            {
               @Override
               protected void onSuccess(List<GitHubRepository> result)
               {
                  List<ProjectData> projectDataList = new ArrayList<ProjectData>();
                  readonlyUrls.clear();
                  for (GitHubRepository repo : result)
                  {
                     projectDataList.add(new ProjectData(repo.getName(), repo.getDescription(), null, null, repo
                        .getSshUrl()));
                     readonlyUrls.put(repo.getSshUrl(), repo.getGitUrl());
                  }
                  display.getRepositoriesGrid().setValue(projectDataList);

                  if (projectDataList.size() != 0)
                  {
                     display.getRepositoriesGrid().selectItem(projectDataList.get(0));
                     data = projectDataList.get(0);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  if (exception instanceof UnauthorizedException)
                  {
                     processUnauthorized();
                  }
                  else
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ShowImportFromGithubHandler#onShowImportFromGithub(org.exoplatform.ide.extension.samples.client.githubimport.ShowImportFromGithubEvent)
    */
   @Override
   public void onShowImportFromGithub(ShowImportFromGithubEvent event)
   {
      if (userInfo != null)
      {
         getToken(userInfo.getName());
         return;
      }
      else
      {
         Dialogs.getInstance().showError(SamplesExtension.LOCALIZATION_CONSTANT.userNotFound());
      }
   }

   /**
    * Open view.
    */
   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         display = d;
         bindDisplay();
         display.getReadOnlyModeField().setValue(true);
         return;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent)
    */
   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      this.userInfo = event.getUserInfo();
   }

   private void getToken(String user)
   {
      try
      {
         GitHubClientService.getInstance().getUserToken(user,
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {

               @Override
               protected void onSuccess(StringBuilder result)
               {
                  if (result.toString() == null || result.toString().isEmpty())
                  {
                     processUnauthorized();
                  }
                  else
                  {
                     openView();
                     display.setFinishButtonEnabled(false);
                     getUserRepos();
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  processUnauthorized();
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void processUnauthorized()
   {
      IDE.fireEvent(new OAuthLoginEvent());
   }

   private void createFolder()
   {
      FolderModel parent = (FolderModel)vfs.getRoot();
      FolderModel model = new FolderModel();
      model.setName(display.getProjectNameField().getValue());
      model.setParent(parent);
      try
      {
         VirtualFileSystem.getInstance().createFolder(parent,
            new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(model))
            {
               @Override
               protected void onSuccess(FolderModel result)
               {
                  cloneFolder(data, result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Exception during creating project"));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e, "Exception during creating project"));
      }
   }

   private void cloneFolder(ProjectData repo, final FolderModel folder)
   {
      String remoteUri = repo.getRepositoryUrl();
      if (!remoteUri.endsWith(".git"))
      {
         remoteUri += ".git";
      }
      JobManager.get().showJobSeparated();

      if (WebSocket.getInstance().getReadyState() == WebSocket.ReadyState.OPEN)
      {
         cloneFolderWS(folder, remoteUri);
      }
      else
      {
         cloneFolderREST(folder, remoteUri);
      }
   }

   private void cloneFolderREST(final FolderModel folder, String remoteUri)
   {
      try
      {
         GitClientService.getInstance().cloneRepository(vfs.getId(), folder, remoteUri, null,
            new AsyncRequestCallback<RepoInfo>()
            {
               @Override
               protected void onSuccess(RepoInfo result)
               {
                  onRepositoryCloned(folder);
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
      finally
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   /**
    * Get the necessary parameters values and call the clone repository method (over WebSocket).
    */
   private void cloneFolderWS(final FolderModel folder, String remoteUri)
   {
      try
      {
         GitClientService.getInstance().cloneRepositoryWS(vfs.getId(), folder, remoteUri, null,
            new RESTfulRequestCallback<RepoInfo>()
            {
               @Override
               protected void onSuccess(RepoInfo result)
               {
                  onRepositoryCloned(folder);
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
      finally
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   private void onRepositoryCloned(FolderModel folder)
   {
      IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), OutputMessage.Type.INFO));
      IDE.fireEvent(new ConvertToProjectEvent(folder.getId(), vfs.getId()));
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   private void handleError(Throwable t)
   {
      String errorMessage =
         (t.getMessage() != null && t.getMessage().length() > 0) ? t.getMessage() : GitExtension.MESSAGES.cloneFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.ERROR));
   }
}
