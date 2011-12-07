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
package org.exoplatform.ide.client.project.deploy;

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
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployProjectToPaasPresenter.java Dec 1, 2011 4:45:52 PM vereshchaka $
 *
 */
public class DeployProjectToPaasPresenter implements DeployProjectToPaasHandler, ViewClosedHandler, VfsChangedHandler
{
   public interface Display extends IsView
   {
      HasClickHandlers getCancelButton();

      HasClickHandlers getBackButton();

      HasClickHandlers getFinishButton();

      HasValue<String> getSelectPaasField();

      void setPaasValueMap(String[] values);

      void setPaas(Composite composite);

      void hidePaas();
   }
   
   private Display display;

   private List<String> paases;

   private List<Paas> paasList;

   private VirtualFileSystemInfo vfsInfo;

   private String projectName;

   private String templateName;
   
   private String projectType;
   
   /**
    * Current paas;
    */
   private Paas paas;
   
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
            createProject();
         }
         //if form isn't valid, then do nothing
         //all validation messages must be shown by paases
      }

      @Override
      public void onDeploy(boolean result)
      {
         // TODO Auto-generated method stub
         
      }
   };

   public DeployProjectToPaasPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(DeployProjectToPaasEvent.TYPE, this);
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
                     paas.getView(projectName, paasCallback);
                  }
               }
            }
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
               createProject();
            }
         }
      });
      
      display.getBackButton().addClickHandler(new ClickHandler()
      {
         
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new CreateProjectFromTemplateEvent());
            closeView();
         }
      });
   }

   private void createProject()
   {
      final IDELoader loader = new IDELoader();
      try
      {
         String parentId = vfsInfo.getRoot().getId();

         loader.show();
         TemplateService.getInstance().createProjectFromTemplate(
            vfsInfo.getId(),
            parentId,
            projectName,
            templateName,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<ProjectModel>(
               new ProjectUnmarshaller(new ProjectModel()))
            {
               @Override
               protected void onSuccess(ProjectModel result)
               {
                  loader.hide();
                  if (paas != null)
                  {
                     paas.deploy(result);
                  }
                  
                  IDE.getInstance().closeView(display.asView().getId());
                  IDE.fireEvent(new ProjectCreatedEvent(result));
                  
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         loader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasHandler#onDeployProjectToPaas(org.exoplatform.ide.client.project.deploy.DeployProjectToPaasEvent)
    */
   @Override
   public void onDeployProjectToPaas(DeployProjectToPaasEvent event)
   {
      projectName = event.getProjectName();
      templateName = event.getTemplateName();
      projectType = event.getProjectType();
      openView();
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
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
      paases = new ArrayList<String>();
      paases.add("None");
      paases.addAll(getPaasValues());
      display.setPaasValueMap(paases.toArray(new String[paases.size()]));
      paas = null;
      display.getSelectPaasField().setValue("None");

   }

   private List<String> getPaasValues()
   {
      List<String> paases = new ArrayList<String>();
      this.paasList = IDE.getInstance().getPaases();
      for (Paas paas : this.paasList)
      {
         if (paas.getSupportedProjectTypes().contains(projectType))
         {
            paases.add(paas.getName());
         }
         
      }
      return paases;
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
   }
   
}
