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
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;
import org.exoplatform.ide.extension.samples.client.marshal.RepositoriesUnmarshaller;
import org.exoplatform.ide.extension.samples.client.oauth.OAuthLoginEvent;
import org.exoplatform.ide.git.client.github.GitHubClientService;
import org.exoplatform.ide.git.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.git.shared.GitHubRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Presenter for importing user's GitHub project to IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubPresenter.java Dec 7, 2011 3:37:11 PM vereshchaka $
 * 
 */
public class ImportFromGithubPresenter implements ShowImportFromGithubHandler, ViewClosedHandler,
   GithubStep<ProjectData>, UserInfoReceivedHandler
{
   public interface Display extends IsView
   {
      /**
       * Returns project's type field.
       * 
       * @return {@link HasValue} project's type field
       */
      HasValue<String> getProjectTypeField();

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
      HasClickHandlers getNextButton();

      /**
       * Returns cancel button's click handler.
       * 
       * @return {@link HasClickHandlers} button's click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Set the project's types.
       * 
       * @param values project's types
       */
      void setProjectTypeValues(String[] values);

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
      void setNextButtonEnabled(boolean enabled);

      /**
       * Show/hide the import step.
       * 
       * @param show
       */
      void showImportStep(boolean show);
   }

   private UserInfo userInfo;

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * Next step.
    */
   private GithubStep<ProjectData> nextStep;

   /**
    * Selected project (Git repository).
    */
   private ProjectData selectedProjectData;

   /**
    * Map of read-only URLs. Key is ssh Git URL - value is read-only Git URL.
    */
   private HashMap<String, String> readonlyUrls = new HashMap<String, String>();

   public ImportFromGithubPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ShowImportFromGithubEvent.TYPE, this);
      IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
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
               selectedProjectData = event.getSelectedItem();
               display.getProjectNameField().setValue(event.getSelectedItem().getName());
               display.setNextButtonEnabled(true);
            }
         }
      });

      display.getNextButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            moveToNextStep();
         }
      });

      final Set<String> types = ProjectResolver.getProjectsTypes();
      display.setProjectTypeValues(types.toArray(new String[types.size()]));
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
                     projectDataList.add(new ProjectData(repo.getName(), repo.getDescription(), null, repo.getSshUrl()));
                     readonlyUrls.put(repo.getSshUrl(), repo.getGitUrl());
                  }
                  display.getRepositoriesGrid().setValue(projectDataList);
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
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onOpen(java.lang.Object)
    */
   @Override
   public void onOpen(ProjectData value)
   {
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onReturn()
    */
   @Override
   public void onReturn()
   {
      openView();
      goToImport();
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
         return;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#setNextStep(org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep)
    */
   @Override
   public void setNextStep(GithubStep<ProjectData> step)
   {
      nextStep = step;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#setPreviousStep(org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep)
    */
   @Override
   public void setPreviousStep(GithubStep<ProjectData> step)
   {
   }

   /**
    * Go to import state.
    */
   protected void goToImport()
   {
      display.showImportStep(true);
      display.setNextButtonEnabled(false);
      getUserRepos();
   }

   /**
    * Move to next step.
    */
   protected void moveToNextStep()
   {
      String name = display.getProjectNameField().getValue();
      if (name != null && !name.isEmpty())
      {
         selectedProjectData.setName(name);
      }
      selectedProjectData.setType(display.getProjectTypeField().getValue());
      if (display.getReadOnlyModeField().getValue())
      {
         String readonlyUrl = readonlyUrls.get(selectedProjectData.getRepositoryUrl());
         selectedProjectData.setRepositoryUrl(readonlyUrl);
      }

      nextStep.onOpen(selectedProjectData);
      IDE.getInstance().closeView(display.asView().getId());
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
                     goToImport();
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
}
