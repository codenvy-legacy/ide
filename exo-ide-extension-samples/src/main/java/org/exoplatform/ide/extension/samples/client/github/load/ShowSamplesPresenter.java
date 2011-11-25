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
package org.exoplatform.ide.extension.samples.client.github.load;

import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesClientService;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.SamplesLocalizationConstant;
import org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep;
import org.exoplatform.ide.extension.samples.shared.Repository;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter to show the list of samples, that stored on github.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubSamplesPresenter.java Aug 30, 2011 12:12:39 PM vereshchaka $
 *
 */
public class ShowSamplesPresenter implements ShowSamplesHandler, ViewClosedHandler, ItemsSelectedHandler,
   VfsChangedHandler, GithubStep<ProjectData>
{

   public interface Display extends IsView
   {
      HasClickHandlers getNextButton();

      HasClickHandlers getCancelButton();

      ListGridItem<ProjectData> getSamplesListGrid();

      List<ProjectData> getSelectedItems();
      
      HasValue<String> getProjectNameField();

      void enableNextButton(boolean enable);
   }

   private static SamplesLocalizationConstant lb = SamplesExtension.LOCALIZATION_CONSTANT;

   private Display display;

   List<Item> selectedItems;

   private VirtualFileSystemInfo vfs;

   private List<ProjectData> selectedProjects;

   private ProjectData selectedProjectData;

   private GithubStep<ProjectData> nextStep;

   public ShowSamplesPresenter()
   {
      IDE.addHandler(ShowSamplesEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   private void bindDisplay()
   {
      display.getNextButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            if (selectedProjects == null || selectedProjects.isEmpty())
            {
               Dialogs.getInstance().showError(lb.showSamplesErrorSelectRepository());
               return;
            }
            selectedProjectData = selectedProjects.get(0);
            String name = display.getProjectNameField().getValue();
            if (name != null && !name.isEmpty())
            {
               selectedProjectData.setName(name);
            }
            nextStep.onOpen(selectedProjectData);
            closeView();
            //            createEmptyProject();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getSamplesListGrid().addSelectionHandler(new SelectionHandler<ProjectData>()
      {
         @Override
         public void onSelection(SelectionEvent<ProjectData> event)
         {
            selectedProjects = display.getSelectedItems();
            if (selectedProjects == null || selectedProjects.isEmpty())
            {
               display.enableNextButton(false);
            }
            else
            {
               display.enableNextButton(true);
            }
         }
      });
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
    * @see org.exoplatform.ide.client.ShowSamplesHandler.samples.GithubSamplesShowHandler#onShowSamples(org.exoplatform.ide.client.ShowSamplesEvent.samples.ShowGithubSamplesEvent)
    */
   @Override
   public void onShowSamples(ShowSamplesEvent event)
   {
      SamplesClientService.getInstance().getRepositoriesList(new AsyncRequestCallback<List<Repository>>()
      {
         @Override
         protected void onSuccess(List<Repository> result)
         {
            openView();
            List<ProjectData> projectDataList = new ArrayList<ProjectData>();
            for (Repository repo : result)
            {
               String[] arr = parseDescription(repo.getDescription());
               projectDataList.add(new ProjectData(repo.getName(), arr[1], arr[0], repo.getUrl()));
            }
            display.getSamplesListGrid().setValue(projectDataList);
            display.enableNextButton(false);
         }
      });
   }

   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         display = d;
         bindDisplay();
         return;
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Show Samples View must be null"));
      }
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }

   private void createEmptyProject()
   {
      FolderModel parent = (FolderModel)vfs.getRoot();
      ProjectModel model = new ProjectModel();
      model.setName(selectedProjectData.getName());
      model.setProjectType(selectedProjectData.getType());
      model.setParent(parent);
      try
      {
         VirtualFileSystem.getInstance().createProject(
            parent,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<ProjectModel>(
               new ProjectUnmarshaller(model))
            {

               @Override
               protected void onSuccess(ProjectModel result)
               {
                  cloneRepository(selectedProjectData, result);
                  closeView();
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
         e.printStackTrace();
      }
   }

   private void cloneRepository(ProjectData repo, final ProjectModel project)
   {
      String remoteUri = repo.getRepositoryUrl();
      if (!remoteUri.endsWith(".git"))
      {
         remoteUri += ".git";
      }

      try
      {
         GitClientService.getInstance().cloneRepository(vfs.getId(), project, remoteUri, null,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
                  IDE.fireEvent(new ProjectCreatedEvent(project));
                  IDE.fireEvent(new RefreshBrowserEvent(project.getParent()));
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
         e.printStackTrace();
         handleError(e);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   private void handleError(Throwable t)
   {
      String errorMessage =
         (t.getMessage() != null && t.getMessage().length() > 0) ? t.getMessage() : GitExtension.MESSAGES.cloneFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

   /**
    * Parse description of repository on GitHub, that store in such form:
    * <p/>
    * <code>Type: &lt;project type&gt; Desc: &lt;project description&gt;</code>
    * <p/>
    * Return an array with 2 elements, where element[0] is the type of project
    * and element[1] is the description
    * @param text
    * @return
    */
   private static String[] parseDescription(String text)
   {
      String[] projectData = new String[2];
      String[] res = text.split("^Type: | Desc:");
      if (res.length < 3)
      {
         projectData[0] = ProjectResolver.getProjectsTypes().toArray(new String[1])[0];
         projectData[1] = text;
         Dialogs.getInstance().showError(
            "Can't parse project description: " + text
               + ". <br/> It must be in format: Type: &lt;project type&gt; Desc: &lt;project description&gt;");
      }
      else
      {
         projectData[0] = res[1];
         projectData[1] = res[2];
      }
      return projectData;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onOpen(java.lang.Object)
    */
   @Override
   public void onOpen(ProjectData value)
   {
      //it is the first step
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onReturn()
    */
   @Override
   public void onReturn()
   {
      SamplesClientService.getInstance().getRepositoriesList(new AsyncRequestCallback<List<Repository>>()
      {
         @Override
         protected void onSuccess(List<Repository> result)
         {
            openView();
            List<ProjectData> projectDataList = new ArrayList<ProjectData>();
            for (Repository repo : result)
            {
               String[] arr = parseDescription(repo.getDescription());
               projectDataList.add(new ProjectData(repo.getName(), arr[1], arr[0], repo.getUrl()));
            }
            display.getSamplesListGrid().setValue(projectDataList);
            display.enableNextButton(false);
         }
      });
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
      //has no prev step
   }

}
