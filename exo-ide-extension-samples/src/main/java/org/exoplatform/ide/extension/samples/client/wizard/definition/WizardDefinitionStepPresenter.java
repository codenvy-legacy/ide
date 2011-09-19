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
package org.exoplatform.ide.extension.samples.client.wizard.definition;

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
import org.exoplatform.ide.extension.samples.client.wizard.deployment.ShowWizardDeploymentStepEvent;
import org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedEvent;
import org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedHandler;
import org.exoplatform.ide.extension.samples.client.wizard.location.ShowWizardLocationStepEvent;

/**
 * Presenter for Step2 (Definition) of Wizard for creation Java Project.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SourceWizardPresenter.java Sep 7, 2011 3:00:58 PM vereshchaka $
 */
public class WizardDefinitionStepPresenter implements ShowWizardDefinitionStepHandler, ViewClosedHandler, ProjectCreationFinishedHandler
{
   public interface Display extends IsView
   {
      HasValue<String> getNameField();
      
      HasValue<String> getSelectTypeField();
      
      HasClickHandlers getNextButton();
      
      HasClickHandlers getCancelButton();
      
      HasClickHandlers getBackButton();
      
      void enableNextButton(boolean enabled);
      
      void setTypes(String[] types);
      
      void focusInNameField();
   }
   
   private static final String[] TYPES;
   
   private HandlerManager eventBus;
   
   private Display display;
   
   private ProjectProperties projectProperties;
   
   static
   {
      TYPES = new String[2];
      TYPES[0] = ProjectProperties.ProjectType.SERVLET_JSP;
      TYPES[1] = ProjectProperties.ProjectType.SPRING;
   }
   
   public WizardDefinitionStepPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(ShowWizardDefinitionStepEvent.TYPE, this);
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
            projectProperties.setName(display.getNameField().getValue());
            projectProperties.setType(display.getSelectTypeField().getValue());
            eventBus.fireEvent(new ShowWizardDeploymentStepEvent(projectProperties));
            closeView();
         }
      });
      
      display.getBackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            eventBus.fireEvent(new ShowWizardLocationStepEvent());
            closeView();
         }
      });
      
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            final String name = display.getNameField().getValue();
            if (name == null || name.isEmpty())
            {
               display.enableNextButton(false);
            }
            else
            {
               display.enableNextButton(true);
            }
         }
      });
      
      display.setTypes(TYPES);
      display.focusInNameField();
      
      if (projectProperties != null)
      {
         display.getNameField().setValue(projectProperties.getName());
         display.getSelectTypeField().setValue(projectProperties.getType());
      }
      if (display.getNameField().getClass() == null || display.getNameField().getValue().isEmpty())
      {
         display.enableNextButton(false);
      }
      else
      {
         display.enableNextButton(true);
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
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardSourceHandler#onShowWizardDefinition(org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardDefinitionStepEvent)
    */
   @Override
   public void onShowWizard(ShowWizardDefinitionStepEvent event)
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
         eventBus.fireEvent(new ExceptionThrownEvent("Show Wizard Definition must be null"));
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
   }

}
