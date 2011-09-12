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
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.ProjectProperties;
import org.exoplatform.ide.extension.samples.client.wizard.definition.ShowWizardDefinitionStepEvent;
import org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedEvent;
import org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedHandler;
import org.exoplatform.ide.extension.samples.client.wizard.finish.ShowWizardFinishStepEvent;

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
      
      void setPaasValueMap(String[] values);
   }
   
   private static final String[] PAAS;
   
   private HandlerManager eventBus;
   
   private Display display;
   
   /**
    * Project properties.
    * Got from previous step.
    */
   private ProjectProperties projectProperties;
   
   /**
    * If new project created, than must be null.
    * If button Next is clicked, than
    * variable must store the selected PaaS
    * and display it, when Back button will be clicked
    * to return to this View.
    */
   private String selectedPaaS;
   
   static
   {
      PAAS = new String[2];
      PAAS[0] = ProjectProperties.Paas.CLOUDFOUNDRY;
      PAAS[1] = ProjectProperties.Paas.CLOUDBEES;
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
            selectedPaaS = display.getSelectPaasField().getValue();
            if (projectProperties == null)
            {
               projectProperties = new ProjectProperties();
            }
            projectProperties.setPaas(selectedPaaS);
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
            selectedPaaS = display.getSelectPaasField().getValue();
         }
      });
      
      display.setPaasValueMap(PAAS);
      
      if (selectedPaaS != null)
      {
         display.getSelectPaasField().setValue(selectedPaaS);
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
         projectProperties = event.getProjectProperties();
      }
      openView();
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
    * @see org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedHandler#onProjectCreationFinished(org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedEvent)
    */
   @Override
   public void onProjectCreationFinished(ProjectCreationFinishedEvent event)
   {
      projectProperties = null;
      selectedPaaS = null;
   }

}
