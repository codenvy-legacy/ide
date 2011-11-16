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

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.ProjectProperties;
import org.exoplatform.ide.extension.samples.client.wizard.ProjectCreationFinishedEvent;
import org.exoplatform.ide.extension.samples.client.wizard.ProjectCreationFinishedHandler;
import org.exoplatform.ide.extension.samples.client.wizard.WizardContinuable;
import org.exoplatform.ide.extension.samples.client.wizard.WizardReturnable;
import org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardEvent;
import org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Presenter for Step2 (Definition) of Wizard for creation Java Project.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SourceWizardPresenter.java Sep 7, 2011 3:00:58 PM vereshchaka $
 */
public class WizardDefinitionStepPresenter implements ViewClosedHandler, ProjectCreationFinishedHandler,
   WizardReturnable, ShowWizardHandler
{
   public interface Display extends IsView
   {
      HasValue<String> getNameField();
      
      HasValue<String> getSelectTypeField();
      
      HasClickHandlers getNextButton();
      
      HasClickHandlers getCancelButton();
      
      void enableNextButton(boolean enabled);
      
      void setTypes(String[] types);
      
      void focusInNameField();
   }
   
   private static final String[] TYPES;
   
   private Display display;
   
   private ProjectProperties projectProperties;
   
   private WizardContinuable wizardContinuable;
   
   static
   {
      TYPES = new String[2];
      TYPES[0] = ProjectProperties.ProjectType.SERVLET_JSP;
      TYPES[1] = ProjectProperties.ProjectType.SPRING;
   }
   
   public WizardDefinitionStepPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ProjectCreationFinishedEvent.TYPE, this);
      IDE.addHandler(ShowWizardEvent.TYPE, this);
   }
   
   /**
    * @param wizardContinuable the wizardContinuable to set
    */
   public void setWizardContinuable(WizardContinuable wizardContinuable)
   {
      this.wizardContinuable = wizardContinuable;
   }
   
   private void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ProjectCreationFinishedEvent(true));
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
            wizardContinuable.onContinue(projectProperties);
            closeView();
         }
      });
      
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            final String name = display.getNameField().getValue();
            projectProperties.setName(name);
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
      
      display.getSelectTypeField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            projectProperties.setType(display.getSelectTypeField().getValue());
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
         display.setTypes(TYPES);
         
         if (projectProperties != null)
         {
            display.getNameField().setValue(projectProperties.getName());
            display.getSelectTypeField().setValue(projectProperties.getType());
         }
         if (display.getNameField().getValue() == null || display.getNameField().getValue().isEmpty())
         {
            display.enableNextButton(false);
         }
         else
         {
            display.enableNextButton(true);
         }
         display.focusInNameField();
         return;
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Show Wizard Definition must be null"));
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.ProjectCreationFinishedHandler#onProjectCreationFinished(org.exoplatform.ide.extension.samples.client.wizard.ProjectCreationFinishedEvent)
    */
   @Override
   public void onProjectCreationFinished(ProjectCreationFinishedEvent event)
   {
      projectProperties = null;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.WizardReturnable#onReturn()
    */
   @Override
   public void onReturn()
   {
      openView();
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardHandler#onShowWizard(org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardEvent)
    */
   @Override
   public void onShowWizard(ShowWizardEvent event)
   {
      projectProperties = new ProjectProperties();
      openView();
   }

}
