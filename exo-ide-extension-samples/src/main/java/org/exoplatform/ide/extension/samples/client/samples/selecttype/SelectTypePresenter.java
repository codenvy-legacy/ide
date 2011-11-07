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
package org.exoplatform.ide.extension.samples.client.samples.selecttype;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.samples.SamplesContinuable;
import org.exoplatform.ide.extension.samples.client.samples.SamplesReturnable;
import org.exoplatform.ide.extension.samples.shared.Repository;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.Set;

/**
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SelectTypePresenter.java Nov 3, 2011 5:27:27 PM vereshchaka $
 */
public class SelectTypePresenter implements ViewClosedHandler, SamplesContinuable, SamplesReturnable, VfsChangedHandler
{
   
   public interface Display extends IsView
   {
      HasValue<String> getProjectType();
      
      HasClickHandlers getCancelButton();

      HasClickHandlers getFinishButton();

      HasClickHandlers getBackButton();

      /**
       * @param projectsTypes
       */
      void setProjectTypes(Set<String> projectsTypes);
   }
   
   private Display display;
   
   private SamplesReturnable samplesReturnable;
   
   /**
    * Repository, that was selected on previous step.
    * In this step it will be cloned.
    */
   private Repository repository;
   
   private VirtualFileSystemInfo vfs;
   
   public SelectTypePresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }
   
   private void bindDisplay()
   {
      display.getFinishButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            createEmptyProject();
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

      display.getBackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            samplesReturnable.onReturn();
            closeView();
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
   
   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         display = d;
         bindDisplay();
         display.setProjectTypes(ProjectResolver.getProjectsTypes());
         return;
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Select Type View must be null"));
      }
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.samples.SamplesContinuable#onContinue()
    */
   @Override
   public void onContinue(Repository repository)
   {
      this.repository = repository;
      openView();
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.samples.SamplesReturnable#onReturn()
    */
   @Override
   public void onReturn()
   {
      openView();
   }
   
   public void setSamplesReturnable(SamplesReturnable samplesReturnable)
   {
      this.samplesReturnable = samplesReturnable;
   }
   
   private void createEmptyProject()
   {
      FolderModel parent = (FolderModel)vfs.getRoot();
      ProjectModel model = new ProjectModel();
      model.setName(repository.getName());
      model.setProjectType(display.getProjectType().getValue());
      model.setParent(parent);
      try
      {
         VirtualFileSystem.getInstance().createProject(parent, new AsyncRequestCallback<ProjectModel>(
            new ProjectUnmarshaller(model))
         {
            @Override
            protected void onSuccess(ProjectModel result)
            {
               cloneRepository(repository, result);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception,
                  "Exception during creating project"));
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }
   
   private void cloneRepository(Repository repo, final ProjectModel project)
   {
      String remoteUri = repo.getUrl();
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
                  IDE.getInstance().closeView(display.asView().getId());
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

}
