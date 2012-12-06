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
package org.exoplatform.ide.extension.samples.client.github.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.LinkedHashMap;

/**
 * Presenter for deploying samples imported from GitHub.
 * <p/>
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeploySamplesPresenter.java Nov 22, 2011 10:35:16 AM vereshchaka $
 */
public class DeploySamplesPresenter implements ViewClosedHandler, GithubStep<ProjectData>, VfsChangedHandler
{

   public interface Display extends IsView
   {
      HasClickHandlers getFinishButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getBackButton();

      HasValue<String> getSelectPaasField();

      void enableFinishButton(boolean enable);

      void setPaaSValues(LinkedHashMap<String, String> values);

      void setPaaSView(Composite composite);

      void hidePaas();
   }

   /**
    * Default CloudFoundry target.
    */
   public static final String DEFAULT_CLOUDFOUNDRY_TARGET = "http://api.cloudfoundry.com";

   public static final String DEFAULT_URL_PREFIX = "<name>.";

   private Display display;

   private GithubStep<ProjectData> prevStep;

   /**
    * project data received from previous step
    */
   private ProjectData data;

   private VirtualFileSystemInfo vfs;

   private PaaS selectedPaaS;

   private FolderModel PROJECT_ROOT_FOLDER;

   private DeployResultHandler deployResultHandler = new DeployResultHandler()
   {
      @Override
      public void onProjectCreated(ProjectModel project)
      {
      }

      @Override
      public void onDeployFinished(boolean success)
      {
         if (success && display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      }
   };

   public DeploySamplesPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
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

      display.getFinishButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            if (selectedPaaS != null && !selectedPaaS.getPaaSActions().validate())
            {
               Dialogs.getInstance().showError("Please, fill all required fields.");
            }
            else
            {
               createFolder();
            }
         }
      });

      display.getBackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            prevStep.onReturn();
            closeView();
         }
      });

      display.getSelectPaasField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String value = event.getValue();
            if ("none".equals(value))
            {
               display.hidePaas();
               selectedPaaS = null;
            }
            else
            {
               for (PaaS paas : IDE.getInstance().getPaaSes())
               {
                  if (paas.getId().equals(value))
                  {
                     selectedPaaS = paas;
                     Composite view =
                        selectedPaaS.getPaaSActions().getDeployView(data.getName(),
                           ProjectType.fromValue(data.getType()));
                     openView(view);
                  }
               }
            }
         }
      });
   }

   private void openView(Composite view)
   {
      if (view != null)
      {
         display.setPaaSView(view);
      }
      else
      {
         selectedPaaS = null;
         display.hidePaas();
         display.getSelectPaasField().setValue("none");
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
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onOpen(java.lang.Object)
    */
   @Override
   public void onOpen(ProjectData value)
   {
      this.data = value;
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
         display.setPaaSValues(getPaasValues(value));
         selectedPaaS = null;
         display.getSelectPaasField().setValue("none");
         return;
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Show Deployment Wizard View must be null"));
      }
   }

   private LinkedHashMap<String, String> getPaasValues(ProjectData project)
   {
      LinkedHashMap<String, String> paases = new LinkedHashMap<String, String>();
      paases.put("none", "None");
      if (project.getTargets().isEmpty())
      {
         return paases;
      }

      for (String target : project.getTargets())
      {
         for (PaaS paas : IDE.getInstance().getPaaSes())
         {
            if (paas.getId().equals(target))
            {
               paases.put(paas.getId(), paas.getTitle());
               break;
            }
         }
      }
      return paases;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onReturn()
    */
   @Override
   public void onReturn()
   {
      // the last step
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#setNextStep(org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep)
    */
   @Override
   public void setNextStep(GithubStep<ProjectData> step)
   {
      // has no step, it is the last step.
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#setPreviousStep(org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep)
    */
   @Override
   public void setPreviousStep(GithubStep<ProjectData> step)
   {
      this.prevStep = step;
   }

   private void closeView()
   {
      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   // ---------------projects creation------------------------
   private void createFolder()
   {
      FolderModel parent = (FolderModel)vfs.getRoot();
      FolderModel model = new FolderModel();
      model.setName(data.getName());
      model.setParent(parent);
      try
      {
         VirtualFileSystem.getInstance().createFolder(parent,
            new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(model))
            {
               @Override
               protected void onSuccess(FolderModel result)
               {
                  PROJECT_ROOT_FOLDER = result;
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

   /**
    * Clone of the repository by sending request over WebSocket or HTTP.
    */
   private void cloneFolder(ProjectData repo, final FolderModel folder)
   {
      String remoteUri = repo.getRepositoryUrl();
      if (!remoteUri.endsWith(".git"))
      {
         remoteUri += ".git";
      }
      JobManager.get().showJobSeparated();

      // TODO temporary disabled using WebSocket
//      if (WebSocket.getInstance().getReadyState() == ReadyState.OPEN)
//         cloneFolderWS(repo, folder, remoteUri);
//      else
         cloneFolderREST(repo, folder, remoteUri);
   }

   /**
    * Get the necessary parameters values and call the clone repository method (over HTTP).
    */
   private void cloneFolderREST(ProjectData repo, final FolderModel folder, String remoteUri)
   {
      try
      {
         GitClientService.getInstance().cloneRepository(vfs.getId(), folder, remoteUri, null,
            new AsyncRequestCallback<RepoInfo>()
            {
               @Override
               protected void onSuccess(RepoInfo result)
               {
                  onRepositoryCloned();
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
    * Get the necessary parameters values and call the clone repository method (over WebSocket).
    */
   private void cloneFolderWS(ProjectData repo, final FolderModel folder, String remoteUri)
   {
      try
      {
         GitClientService.getInstance().cloneRepositoryWS(vfs.getId(), folder, remoteUri, null,
            new RequestCallback<RepoInfo>()
            {
               @Override
               protected void onSuccess(RepoInfo result)
               {
                  onRepositoryCloned();
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

   private void convertToProject(FolderModel folderModel)
   {
      String projectType = data.getType();
      folderModel.getProperties().add(new Property("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
      folderModel.getProperties().add(new Property("vfs:projectType", projectType));

      if (!data.getTargets().isEmpty())
      {
         folderModel.getProperties().add(new Property(ProjectProperties.TARGET.value(), data.getTargets()));
      }

      ItemWrapper item = new ItemWrapper(new ProjectModel());
      ItemUnmarshaller unmarshaller = new ItemUnmarshaller(item);
      try
      {
         VirtualFileSystem.getInstance().updateItem(folderModel, null,
            new AsyncRequestCallback<ItemWrapper>(unmarshaller)
            {

               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  if (selectedPaaS != null)
                  {
                     selectedPaaS.getPaaSActions().deploy((ProjectModel)result.getItem(), deployResultHandler);
                  }
                  IDE.getInstance().closeView(display.asView().getId());
                  IDE.fireEvent(new ProjectCreatedEvent((ProjectModel)result.getItem()));
                  IDE.fireEvent(new RefreshBrowserEvent(vfs.getRoot()));
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
    * Perform actions on project repository was cloned successfully.
    */
   private void onRepositoryCloned()
   {
      IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
      convertToProject(PROJECT_ROOT_FOLDER);
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

}
