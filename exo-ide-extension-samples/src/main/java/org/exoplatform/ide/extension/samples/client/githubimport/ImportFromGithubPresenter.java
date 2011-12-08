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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.SamplesLocalizationConstant;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.Set;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubPresenter.java Dec 7, 2011 3:37:11 PM vereshchaka $
 *
 */
public class ImportFromGithubPresenter implements ShowImportFromGithubHandler, ViewClosedHandler, VfsChangedHandler
{
   public interface Display extends IsView
   {
      HasClickHandlers getImportButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getUrlField();

      HasValue<String> getProjectTypeField();
      
      HasValue<String> getNotifyLabel();

      void setProjectTypeValues(String[] values);

      void enableImportButton(boolean enabled);
   }
   
   private static final SamplesLocalizationConstant lb = SamplesExtension.LOCALIZATION_CONSTANT;

   private Display display;
   
   private String gitUrl;
   
   private String projectType;
   
   private String projectName;
   
   private VirtualFileSystemInfo vfs;

   public ImportFromGithubPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ShowImportFromGithubEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
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

      display.getImportButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            gitUrl = display.getUrlField().getValue();
            projectType = display.getProjectTypeField().getValue();
            projectName = getRepoNameByUrl(gitUrl);
            createProject();
            closeView();
         }
      });

      display.getUrlField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String value = event.getValue();
            if (value == null || value.isEmpty())
            {
               display.enableImportButton(false);
               display.getNotifyLabel().setValue("");
               return;
            }
            
            if (!isPublicUrl(value))
            {
               display.enableImportButton(false);
               display.getNotifyLabel().setValue(lb.importFromGithubUrlNotPublicError());
               return;
            }
            display.enableImportButton(true);
            display.getNotifyLabel().setValue("");
         }
      });
   }
   
   /**
    * For public repos, the URL can be a read-only URL like 
    * <code>git://github.com/user/repo.git</code> or an HTTP read-only URL like 
    * <code>http://github.com/user/repo.git</code>.
    * <p/>
    * 
    * For private repos, you must use a private ssh url like 
    * <code>git@github.com:user/repo.git</code>.
    * <p/>
    * 
    * From here http://help.github.com/remotes/
    * 
    * @param gitUrl
    * @return
    */
   private boolean isPublicUrl(String gitUrl)
   {
      if (gitUrl == null || gitUrl.isEmpty())
         return false;
      
      if (gitUrl.startsWith("git@"))
         return false;
      
      if ((gitUrl.startsWith("git:") || gitUrl.startsWith("https:")) && gitUrl.endsWith(".git"))
         return true;
      
      return false;
   }
   
   private void createProject()
   {
      FolderModel parent = (FolderModel)vfs.getRoot();
      ProjectModel model = new ProjectModel();
      model.setName(projectName);
      model.setProjectType(projectType);
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
                  cloneRepository(result);
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
   
   private void cloneRepository(final ProjectModel project)
   {
      try
      {
         GitClientService.getInstance().cloneRepository(vfs.getId(), project, gitUrl, null,
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
   
   private String getRepoNameByUrl(String gitUrl)
   {
      String name = gitUrl.substring(gitUrl.lastIndexOf("/") + 1, gitUrl.lastIndexOf("."));
      return name;
   }
   
   private void handleError(Throwable t)
   {
      String errorMessage =
         (t.getMessage() != null && t.getMessage().length() > 0) ? t.getMessage() : GitExtension.MESSAGES.cloneFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

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

      final Set<String> types = ProjectResolver.getProjectsTypes();
      display.setProjectTypeValues(types.toArray(new String[types.size()]));
      display.enableImportButton(false);
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }
}
