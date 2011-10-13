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
package org.exoplatform.ide.extension.samples.client.wizard.deployment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.ProjectProperties;
import org.exoplatform.ide.extension.samples.client.SamplesClientService;
import org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.login.LoggedInHandler;
import org.exoplatform.ide.extension.samples.client.wizard.definition.ShowWizardDefinitionStepEvent;
import org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedEvent;
import org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedHandler;
import org.exoplatform.ide.extension.samples.client.wizard.finish.ShowWizardFinishStepEvent;

import java.util.List;

/**
 * Presenter for Step3 (Deployment) of Wizard for creation Java Project.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WizardDeploymentStepPresenter.java Sep 9, 2011 3:01:50 PM vereshchaka $
 *
 */
public class WizardDeploymentStepPresenter implements ShowWizardDeploymentStepHandler, ViewClosedHandler, 
ProjectCreationFinishedHandler
{
   public interface Display extends IsView
   {
      HasClickHandlers getNextButton();
      
      HasClickHandlers getCancelButton();
      
      HasClickHandlers getBackButton();
      
      HasValue<String> getSelectPaasField();
      
      HasValue<String> getSelectCloudBeesDomainField();
      
      HasValue<String> getCloudFoundryNameField();
      
      HasValue<String> getCloudFoundryUrlField();
      
      HasValue<String> getCloudFoundryTargetField();
      
      HasValue<String> getCloudBeesNameField();
      
      HasValue<String> getCloudBeesIdField();
      
      void setCloudFoundryAvailableTargets(String[] targets);
      
      void setPaasValueMap(String[] values, String selected);
      
      void setVisibleCloudBeesPanel(boolean visible);
      
      void setVisibleCloudFoundryPanel(boolean visible);
      
      void setCloudBeesDomainsValueMap(String[] values);
      
      void enableNextButton(boolean enable);
   }
   
   /**
    * Default CloudFoundry target.
    */
   public static final String DEFAULT_CLOUDFOUNDRY_TARGET = "http://api.cloudfoundry.com";
   
   private static final String[] PAAS;
   
   private HandlerManager eventBus;
   
   private Display display;
   
   /**
    * Project properties.
    * Got from previous step.
    */
   private ProjectProperties projectProperties;
   
   static
   {
      PAAS = new String[3];
      PAAS[0] = ProjectProperties.Paas.NONE;
      PAAS[1] = ProjectProperties.Paas.CLOUDFOUNDRY;
      PAAS[2] = ProjectProperties.Paas.CLOUDBEES;
   }
   
   public WizardDeploymentStepPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(ShowWizardDeploymentStepEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ProjectCreationFinishedEvent.TYPE, this);
   }
   
   private void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            eventBus.fireEvent(new ProjectCreationFinishedEvent(true));
            closeView();
         }
      });
      
      display.getNextButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            //if one of field are changed, the new value must be saved in projectProperties.
            //That's why, when Nex button is clicked, the actual state are send to next step.
            if (projectProperties.getPaas().equals(ProjectProperties.Paas.CLOUDFOUNDRY))
            {
               validateCloudFoundryParams();
               return;
            }
            eventBus.fireEvent(new ShowWizardFinishStepEvent(projectProperties));
            closeView();
         }
      });
      
      display.getBackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            eventBus.fireEvent(new ShowWizardDefinitionStepEvent(null));
            closeView();
         }
      });
      
      display.getSelectPaasField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            final String selectedPaaS = display.getSelectPaasField().getValue();
            projectProperties.setPaas(selectedPaaS);
            if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(selectedPaaS))
            {
               display.setVisibleCloudBeesPanel(false);
               display.setVisibleCloudFoundryPanel(true);
               fillCloudFoundryFields();
            }
            else if (ProjectProperties.Paas.CLOUDBEES.equals(selectedPaaS))
            {
               display.setVisibleCloudFoundryPanel(false);
               display.setVisibleCloudBeesPanel(true);
               getListOfCloudBeesDomains();
            }
            else
            {
               display.setVisibleCloudFoundryPanel(false);
               display.setVisibleCloudBeesPanel(false);
               display.enableNextButton(true);
            }
         }
      });
      
      display.getCloudBeesNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDBEES.equals(display.getSelectPaasField().getValue()))
            {
               String name = display.getCloudBeesNameField().getValue();
               String cloudBeesId = name + "/" + display.getSelectCloudBeesDomainField().getValue();
               display.getCloudBeesIdField().setValue(cloudBeesId);
               display.enableNextButton(name != null && !name.isEmpty());
               projectProperties.getProperties().put("cb-name", name);
            }
         }
      });
      
      display.getSelectCloudBeesDomainField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDBEES.equals(display.getSelectPaasField().getValue()))
            {
               String name = display.getCloudBeesNameField().getValue();
               String cloudBeesId = name + "/" + display.getSelectCloudBeesDomainField().getValue();
               display.getCloudBeesIdField().setValue(cloudBeesId);
               projectProperties.getProperties().put("domain", display.getSelectCloudBeesDomainField().getValue());
            }
         }
      });
      
      display.getCloudFoundryNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(display.getSelectPaasField().getValue()))
            {
               String name = display.getCloudFoundryNameField().getValue();
               String url = display.getCloudFoundryUrlField().getValue();
               if (name == null || name.isEmpty() || url == null || url.isEmpty())
               {
                  display.enableNextButton(false);
               }
               else
               {
                  display.enableNextButton(true);
               }
               projectProperties.getProperties().put("cf-name", name);
            }
         }
      });
      
      display.getCloudFoundryUrlField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(display.getSelectPaasField().getValue()))
            {
               String name = display.getCloudFoundryNameField().getValue();
               String url = display.getCloudFoundryUrlField().getValue();
               if (name == null || name.isEmpty() || url == null || url.isEmpty())
               {
                  display.enableNextButton(false);
               }
               else
               {
                  display.enableNextButton(true);
               }
               projectProperties.getProperties().put("url", url);
            }
         }
      });
      
      //fill PaaS fields if projectProperties have saved PaaS
      if (projectProperties.getPaas() == null)
      {
         display.setPaasValueMap(PAAS, PAAS[0]);
      }
      else
      {
         //note: event will be send, that value was selected in PaaS select item.
         //That's why, added before handler will handle the event
         //and fill necessary fields
         display.setPaasValueMap(PAAS, projectProperties.getPaas());
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
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardSourceHandler#onShowWizardDefinition(org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardEvent)
    */
   @Override
   public void onShowDeploymentWizard(ShowWizardDeploymentStepEvent event)
   {
      if (event.getProjectProperties() != null)
      {
         //update project properties, if new values are received
         //from previous step
         projectProperties = event.getProjectProperties();
         
         //if no project properties are received, than
         //the saved will be used.
         //If Back button was pressed, then project properties are null
      }
      openView();
   }
   
   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedHandler#onProjectCreationFinished(org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedEvent)
    */
   @Override
   public void onProjectCreationFinished(ProjectCreationFinishedEvent event)
   {
      projectProperties = null;
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
         eventBus.fireEvent(new ExceptionThrownEvent("Show Deployment Wizard View must be null"));
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * Fill cloudfoundry name and domain fields with values, that user
    * entered before (they are stored in Map in projectProperties variable).
    * <p/>
    * If no values are stored, than get the deploy name from the name of project
    * and leave url field empty.
    */
   private void fillCloudFoundryFields()
   {
      String name = projectProperties.getProperties().get("cf-name");
      if (name == null)
      {
         name = projectProperties.getName();
         projectProperties.getProperties().put("cf-name", name);
      }
      final String url =
         projectProperties.getProperties().get("url") != null ? projectProperties.getProperties().get("url") : "";
      
      display.getCloudFoundryNameField().setValue(name);
      display.getCloudFoundryUrlField().setValue(url);
      
      if (name == null || name.isEmpty() || url == null || url.isEmpty())
      {
         display.enableNextButton(false);
      }
      else
      {
         display.enableNextButton(true);
      }
      getCloudFoundryTargets();
   }
   
   /**
    * Fill cloudbees name and domain fields with values, that user
    * entered before (they are stored in Map in projectProperties variable).
    * <p/>
    * If no values are stored, than get the deploy name from the name of project
    * and do nothing with domain field.
    */
   private void fillCloudBeesFields()
   {
      final String deployName =
         projectProperties.getProperties().get("cb-name") != null ? projectProperties.getProperties().get("cb-name")
            : projectProperties.getName();
      display.getCloudBeesNameField().setValue(deployName);
      
      if (projectProperties.getProperties().get("domain") != null)
      {
         display.getSelectCloudBeesDomainField().setValue(projectProperties.getProperties().get("domain"));
      }
      
      String id = deployName + "/" + display.getSelectCloudBeesDomainField().getValue();
      display.getCloudBeesIdField().setValue(id);
      
      display.enableNextButton(deployName != null && !deployName.isEmpty());
   }
   
   /**
    * Get the list of domains of CloudBees from server.
    * <p/>
    * Put the received values to domains select field.
    * <p/>
    * Fill other fields, if we have values (may be user entered them before)
    */
   private void getListOfCloudBeesDomains()
   {
      SamplesClientService.getInstance().getDomains(
         new CloudBeesAsyncRequestCallback<List<String>>(eventBus, domainsLoggedInHandler)
         {
            @Override
            protected void onSuccess(List<String> result)
            {
               String[] domains = new String[result.size()];
               result.toArray(domains);
               display.setCloudBeesDomainsValueMap(domains);
               fillCloudBeesFields();
            }
         });
   }
   
   private LoggedInHandler domainsLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getListOfCloudBeesDomains();
      }
   };
   
   private LoggedInHandler validationLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         validateCloudFoundryParams();
      }
   };
   
   /**
    * Call the server validation of CloudFoundry params (name of application).
    */
   private void validateCloudFoundryParams()
   {
      SamplesClientService.getInstance().validateCloudfoundryAction(projectProperties.getProperties().get("cf-name"), 
         null, new CloudFoundryAsyncRequestCallback<String>(eventBus, validationLoggedInHandler)
         {
            @Override
            protected void onSuccess(String result)
            {
               eventBus.fireEvent(new ShowWizardFinishStepEvent(projectProperties));
               closeView();               
            }
         });
   }
   
   /**
    * Get the list of targes and put them to select field.
    */
   private void getCloudFoundryTargets()
   {
      SamplesClientService.getInstance().getCloudFoundryTargets(new AsyncRequestCallback<List<String>>()
      {
         @Override
         protected void onSuccess(List<String> result)
         {
            if (result.isEmpty())
            {
               display.setCloudFoundryAvailableTargets(new String[]{DEFAULT_CLOUDFOUNDRY_TARGET});
               display.getCloudFoundryTargetField().setValue(DEFAULT_CLOUDFOUNDRY_TARGET);
            }
            else
            {
               String[] servers = result.toArray(new String[result.size()]);
               display.setCloudFoundryAvailableTargets(servers);
               display.getCloudFoundryTargetField().setValue(servers[0]);
            }
         }
      });
   }

}
