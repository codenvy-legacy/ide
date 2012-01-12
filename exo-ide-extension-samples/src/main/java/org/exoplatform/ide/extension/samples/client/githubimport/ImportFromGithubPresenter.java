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
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesClientService;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.SamplesLocalizationConstant;
import org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;
import org.exoplatform.ide.extension.samples.shared.Repository;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubPresenter.java Dec 7, 2011 3:37:11 PM vereshchaka $
 *
 */
public class ImportFromGithubPresenter implements ShowImportFromGithubHandler, ViewClosedHandler, VfsChangedHandler,
   GithubStep<ProjectData>, ApplicationSettingsReceivedHandler
{
   public interface Display extends IsView
   {

      HasClickHandlers getCancelButton();

      HasValue<String> getProjectTypeField();

      HasValue<String> getNotifyLabel();

      HasValue<String> getGitHubName();

      HasClickHandlers getGetButton();

      HasClickHandlers getNextButton();

      HasValue<String> getProjectNameField();

      void setProjectTypeValues(String[] values);

      ListGridItem<ProjectData> getSamplesListGrid();

      void setNextButtonEnabled(boolean enabled);

   }

   private static final SamplesLocalizationConstant lb = SamplesExtension.LOCALIZATION_CONSTANT;

   private Display display;

//   private String gitUrl;
//
//   private String projectType;
//
//   private String projectName;
//
   private VirtualFileSystemInfo vfs;

   private GithubStep<ProjectData> nextStep;

   private ProjectData selectedProjectData;

   private ApplicationSettings appSettings;

   public ImportFromGithubPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ShowImportFromGithubEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   private void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getGetButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            appSettings.setValue("GitHubUserName", display.getGitHubName().getValue(), Store.COOKIES);
            IDE.fireEvent(new SaveApplicationSettingsEvent(appSettings, SaveType.COOKIES));
            getUsersRepos();
         }
      });

      display.getSamplesListGrid().addSelectionHandler(new SelectionHandler<ProjectData>()
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
            String name = display.getProjectNameField().getValue();
            if (name != null && !name.isEmpty())
            {
               selectedProjectData.setName(name);
            }
            selectedProjectData.setType(display.getProjectTypeField().getValue());
            nextStep.onOpen(selectedProjectData);
            closeView();
         }
      });
      
      final Set<String> types = ProjectResolver.getProjectsTypes();
      display.setProjectTypeValues(types.toArray(new String[types.size()]));

      if (appSettings.containsKey("GitHubUserName"))
      {
         String userName = appSettings.getValueAsString("GitHubUserName");
         display.getGitHubName().setValue(userName);
         getUsersRepos();
      }
      display.setNextButtonEnabled(false);
   }

   /**
    * 
    */
   private void getUsersRepos()
   {
      SamplesClientService.getInstance().getRepositoriesList(display.getGitHubName().getValue(),
         new AsyncRequestCallback<List<Repository>>()
         {
            @Override
            protected void onSuccess(List<Repository> result)
            {
               List<ProjectData> projectDataList = new ArrayList<ProjectData>();
               for (Repository repo : result)
               {
                  projectDataList.add(new ProjectData(repo.getName(), repo.getDescription(), null, repo.getUrl()));
               }
               display.getSamplesListGrid().setValue(projectDataList);
            }
         });
   }

//   /**
//    * For public repos, the URL can be a read-only URL like 
//    * <code>git://github.com/user/repo.git</code> or an HTTP read-only URL like 
//    * <code>http://github.com/user/repo.git</code>.
//    * <p/>
//    * 
//    * For private repos, you must use a private ssh url like 
//    * <code>git@github.com:user/repo.git</code>.
//    * <p/>
//    * 
//    * From here http://help.github.com/remotes/
//    * 
//    * @param gitUrl
//    * @return
//    */
//   private boolean isPublicUrl(String gitUrl)
//   {
//      if (gitUrl == null || gitUrl.isEmpty())
//         return false;
//
//      if (gitUrl.startsWith("git@"))
//         return false;
//
//      if ((gitUrl.startsWith("git:") || gitUrl.startsWith("https:")) && gitUrl.endsWith(".git"))
//         return true;
//
//      return false;
//   }

//   private void createProject()
//   {
//      FolderModel parent = (FolderModel)vfs.getRoot();
//      ProjectModel model = new ProjectModel();
//      model.setName(projectName);
//      model.setProjectType(projectType);
//      model.setParent(parent);
//      try
//      {
//         VirtualFileSystem.getInstance().createProject(
//            parent,
//            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<ProjectModel>(
//               new ProjectUnmarshaller(model))
//            {
//
//               @Override
//               protected void onSuccess(ProjectModel result)
//               {
//                  cloneRepository(result);
//               }
//
//               @Override
//               protected void onFailure(Throwable exception)
//               {
//                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Exception during creating project"));
//               }
//            });
//      }
//      catch (RequestException e)
//      {
//         IDE.fireEvent(new ExceptionThrownEvent(exception, "Exception during creating project"));
//      }
//   }

//   private void cloneRepository(final ProjectModel project)
//   {
//      try
//      {
//         GitClientService.getInstance().cloneRepository(vfs.getId(), project, gitUrl, null,
//            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String>()
//            {
//
//               @Override
//               protected void onSuccess(String result)
//               {
//                  IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
//                  IDE.fireEvent(new ProjectCreatedEvent(project));
//                  IDE.fireEvent(new RefreshBrowserEvent(project.getParent()));
//               }
//
//               @Override
//               protected void onFailure(Throwable exception)
//               {
//                  handleError(exception);
//               }
//            });
//      }
//      catch (RequestException e)
//      {
//         handleError(e);
//      }
//   }

//   private String getRepoNameByUrl(String gitUrl)
//   {
//      String name = gitUrl.substring(gitUrl.lastIndexOf("/") + 1, gitUrl.lastIndexOf("."));
//      return name;
//   }

//   private void handleError(Throwable t)
//   {
//      String errorMessage =
//         (t.getMessage() != null && t.getMessage().length() > 0) ? t.getMessage() : GitExtension.MESSAGES.cloneFailed();
//      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
//   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
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
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         display = d;
      }
      IDE.getInstance().openView((View)display);
      bindDisplay();

      //      display.enableImportButton(false);
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
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
   }

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
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Show Samples View must be null"));
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
}
