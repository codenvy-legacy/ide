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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesClientService;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;
import org.exoplatform.ide.extension.samples.client.marshal.RepositoriesUnmarshaller;
import org.exoplatform.ide.extension.samples.shared.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Presenter for importing user's GitHub project to IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubPresenter.java Dec 7, 2011 3:37:11 PM vereshchaka $
 * 
 */
public class ImportFromGithubPresenter implements ShowImportFromGithubHandler, ViewClosedHandler,
   GithubStep<ProjectData>, ApplicationSettingsReceivedHandler
{
   public interface Display extends IsView
   {
      /**
       * Returns login field.
       * 
       * @return {@link HasValue} login field
       */
      HasValue<String> getLoginField();

      /**
       * Returns password field.
       * 
       * @return {@link HasValue} password field
       */
      HasValue<String> getPasswordField();

      /**
       * Returns login result label.
       * 
       * @return {@link HasValue} login result label
       */
      HasValue<String> getLoginResult();

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
       * Returns back button's click handler.
       * 
       * @return {@link HasClickHandlers} button's click handler
       */
      HasClickHandlers getBackButton();

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
       * Give focus to login field.
       */
      void focusInLoginField();

      /**
       * Show/hide the login step.
       * 
       * @param show
       */
      void showLoginStep(boolean show);

      /**
       * Show/hide the import step.
       * 
       * @param show
       */
      void showImportStep(boolean show);
   }

   private final String GITHUB_USER = "GitHubUser";

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
    * Application settings.
    */
   private ApplicationSettings appSettings;

   /**
    * If <code>true</code> then current step is login.
    */
   private boolean isLoginStep = true;

   /**
    * Map of read-only URLs. Key is ssh Git URL - value is read-only Git URL.
    */
   private HashMap<String, String> readonlyUrls = new HashMap<String, String>();

   public ImportFromGithubPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ShowImportFromGithubEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
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

      display.getBackButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            goToLogin();
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

      display.getLoginField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.setNextButtonEnabled(isLoginFieldsFullFilled());
         }
      });

      display.getPasswordField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.setNextButtonEnabled(isLoginFieldsFullFilled());
         }
      });

      final Set<String> types = ProjectResolver.getProjectsTypes();
      display.setProjectTypeValues(types.toArray(new String[types.size()]));

      if (appSettings.containsKey(GITHUB_USER))
      {
         Map<String, String> user = appSettings.getValueAsMap(GITHUB_USER);
         String login = user.keySet().iterator().next();
         display.getLoginField().setValue(login);
         display.getPasswordField().setValue(user.get(login));
      }
   }

   /**
    * Returns login fields full filled state.
    * 
    * @return {@link Boolean}
    */
   private boolean isLoginFieldsFullFilled()
   {
      return (display.getLoginField().getValue() != null && !display.getLoginField().getValue().isEmpty()
         && display.getPasswordField().getValue() != null && !display.getPasswordField().getValue().isEmpty());
   }

   /**
    * Get the list of authorized user's repositories.
    */
   private void getUserRepos()
   {
      try
      {
         SamplesClientService.getInstance().getRepositoriesList(
            new AsyncRequestCallback<List<Repository>>(new RepositoriesUnmarshaller(
               new ArrayList<Repository>()))
            {
               @Override
               protected void onSuccess(List<Repository> result)
               {
                  List<ProjectData> projectDataList = new ArrayList<ProjectData>();
                  readonlyUrls.clear();
                  for (Repository repo : result)
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
                     goToLogin();
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
      openView();
      goToLogin();
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
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      appSettings = event.getApplicationSettings();
   }

   /**
    * Go to login step.
    */
   protected void goToLogin()
   {
      isLoginStep = true;
      display.showLoginStep(true);
      display.focusInLoginField();
      display.setNextButtonEnabled(isLoginFieldsFullFilled());
   }

   /**
    * Go to import state.
    */
   protected void goToImport()
   {
      isLoginStep = false;
      display.showImportStep(true);
      display.setNextButtonEnabled(false);
      getUserRepos();
   }

   /**
    * Move to next step.
    */
   protected void moveToNextStep()
   {
      if (isLoginStep)
      {
         login();
      }
      else
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
   }

   /**
    * Log in GitHub.
    */
   public void login()
   {
      final String login = display.getLoginField().getValue();
      final String password = display.getPasswordField().getValue();

      try
      {
         SamplesClientService.getInstance().loginGitHub(login, password, new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               HashMap<String, String> user = new HashMap<String, String>();
               user.put(display.getLoginField().getValue(), display.getPasswordField().getValue());

               appSettings.setValue(GITHUB_USER, user, Store.SERVER);
               IDE.fireEvent(new SaveApplicationSettingsEvent(appSettings, SaveType.SERVER));
               goToImport();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               display.getLoginResult().setValue(SamplesExtension.LOCALIZATION_CONSTANT.importFromGithubLoginFailed());
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
