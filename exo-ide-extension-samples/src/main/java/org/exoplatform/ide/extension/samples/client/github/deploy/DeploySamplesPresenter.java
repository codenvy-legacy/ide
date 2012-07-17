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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketExceptionMessage;
import org.exoplatform.ide.client.framework.websocket.WebSocketMessage;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.clone.CloneRequestStatusHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for deploying samples imported from GitHub.
 * <p/>
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeploySamplesPresenter.java Nov 22, 2011 10:35:16 AM vereshchaka $
 */
public class DeploySamplesPresenter implements ViewClosedHandler, GithubStep<ProjectData>, VfsChangedHandler,
   WebSocketMessageHandler
{

   public interface Display extends IsView
   {
      HasClickHandlers getFinishButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getBackButton();

      HasValue<String> getSelectPaasField();

      void enableFinishButton(boolean enable);

      void setPaasValueMap(String[] values);

      void setPaas(Composite composite);

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

   //-----new----------------
   private Paas paas;

   private List<String> paases;

   private List<Paas> paasList;

   private RequestStatusHandler cloneStatusHandler;

   private ProjectModel project;

   private PaasCallback paasCallback = new PaasCallback()
   {
      @Override
      public void onViewReceived(Composite composite)
      {
         if (composite != null)
         {
            display.setPaas(composite);
         }
         else
         {
            paas = null;
            display.hidePaas();
            display.getSelectPaasField().setValue("None");
         }
      }

      @Override
      public void onValidate(boolean result)
      {
         if (result)
         {
            createEmptyProject();
         }
         // if form isn't valid, then do nothing
         // all validation messages must be shown by paases
      }

      @Override
      public void onProjectCreated(ProjectModel project)
      {
      }

      @Override
      public void projectCreationFailed()
      {
      }

      @Override
      public void onDeploy(boolean result)
      {
      }

   };

   public DeploySamplesPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(WebSocketMessageEvent.TYPE, this);
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
            if (paas != null)
            {
               paas.validate();
            }
            else
            {
               createEmptyProject();
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
            if ("None".equals(value))
            {
               display.hidePaas();
               paas = null;
            }
            else
            {
               for (Paas cpaas : paasList)
               {
                  if (cpaas.getName().equals(value))
                  {
                     paas = cpaas;
                     paas.getView(data.getName(), paasCallback);
                  }
               }
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
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onOpen(java.lang.Object)
    */
   @Override
   public void onOpen(ProjectData value)
   {
      this.data = value;
      project = null;
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
         paases = new ArrayList<String>();
         paases.add("None");
         paases.addAll(getPaasValues());
         display.setPaasValueMap(paases.toArray(new String[paases.size()]));
         paas = null;
         display.getSelectPaasField().setValue("None");
         return;
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Show Deployment Wizard View must be null"));
      }
   }

   private List<String> getPaasValues()
   {
      List<String> paases = new ArrayList<String>();
      this.paasList = IDE.getInstance().getPaases();
      for (Paas paas : this.paasList)
      {
         if (paas.getSupportedProjectTypes().contains(data.getType()))
         {
            paases.add(paas.getName());
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

   private void createEmptyProject()
   {
      FolderModel parent = (FolderModel)vfs.getRoot();
      ProjectModel model = new ProjectModel();
      model.setName(data.getName());
      model.setProjectType(data.getType());
      model.setParent(parent);
      try
      {
         VirtualFileSystem.getInstance().createProject(parent,
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(model))
            {
               @Override
               protected void onSuccess(ProjectModel result)
               {
                  project = result;
                  cloneRepository(data);
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
         IDE.fireEvent(new ExceptionThrownEvent(e, "Exception during creating project"));
      }
   }

   private void cloneRepository(ProjectData repo)
   {
      String remoteUri = repo.getRepositoryUrl();
      if (!remoteUri.endsWith(".git"))
      {
         remoteUri += ".git";
      }

      try
      {
         JobManager.get().showJobSeparated();

         String sessionId = null;
         WebSocket ws = WebSocket.getInstance();
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            sessionId = ws.getSessionId();
            cloneStatusHandler = new CloneRequestStatusHandler(project.getName(), remoteUri);
            cloneStatusHandler.requestInProgress(project.getId());
         }
         final String webSocketSessionId = sessionId;

         GitClientService.getInstance().cloneRepository(vfs.getId(), project, remoteUri, null, webSocketSessionId,
            new AsyncRequestCallback<String>()
            {
               @Override
               protected void onSuccess(String result)
               {
                  if (webSocketSessionId == null)
                  {
                     onRepositoryCloned();
                  }
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
    * Perform actions on project repository was cloned successfully.
    */
   private void onRepositoryCloned()
   {
      IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
      IDE.fireEvent(new ProjectCreatedEvent(project));
      IDE.fireEvent(new RefreshBrowserEvent(project.getParent()));

      if (paas != null)
      {
         // FIXME
         // timer for allowing project to create fully
         // find better solution!!!!!!!!!
         new Timer()
         {
            @Override
            public void run()
            {
               paas.deploy(project);
            }
         }.schedule(2000);
      }
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
    * @see org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler#onWebSocketMessage(org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent)
    */
   @Override
   public void onWebSocketMessage(WebSocketMessageEvent event)
   {
      String message = event.getMessage();

      WebSocketMessage webSocketMessage =
         AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, WebSocketMessage.class, message).as();
      if (!webSocketMessage.getEvent().equals("gitRepoCloned"))
      {
         return;
      }

      if (!project.getId().equals(webSocketMessage.getData().asString()))
      {
         return;
      }

      WebSocketExceptionMessage webSocketException = webSocketMessage.getException();
      if (webSocketException == null)
      {
         cloneStatusHandler.requestFinished(project.getId());
         onRepositoryCloned();
         return;
      }
      handleError(new Exception(webSocketException.getMessage()));
      cloneStatusHandler.requestError(project.getId(), new Exception(webSocketException.getMessage()));
   }
}
