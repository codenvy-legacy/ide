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
package org.exoplatform.ide.extension.samples.client.wizard.source;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.ProjectProperties;
import org.exoplatform.ide.extension.samples.client.load.ShowSamplesEvent;
import org.exoplatform.ide.extension.samples.client.wizard.WizardContinuable;
import org.exoplatform.ide.extension.samples.client.wizard.WizardReturnable;

/**
 * Presenter for Step1 (Source) of Wizard for creation Java Project.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SourceWizardPresenter.java Sep 7, 2011 3:00:58 PM vereshchaka $
 */
public class WizardSourceStepPresenter implements ShowWizardHandler, ViewClosedHandler, WizardReturnable
{
   public interface Display extends IsView
   {
      HasClickHandlers getNextButton();
      
      HasClickHandlers getCancelButton();
      
      HasValue<String> getSelectSourceField();
      
      void setValuesForSelectSourceField(String[] values);
   }
   
   private static final String SOURCE_SCRATCH = "Generate from scratch";
   
   private static final String SOURCE_IMPORT = "Import from GitHub";
   
   private HandlerManager eventBus;
   
   private Display display;
   
   private WizardContinuable wizard;
   
   public WizardSourceStepPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(ShowWizardEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }
   
   /**
    * @param wizard the wizard to set
    */
   public void setWizardContinuable(WizardContinuable wizard)
   {
      this.wizard = wizard;
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
      
      display.getNextButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            if (SOURCE_SCRATCH.equals(display.getSelectSourceField().getValue()))
            {
               wizard.onContinue(new ProjectProperties());
            }
            else if (SOURCE_IMPORT.equals(display.getSelectSourceField().getValue()))
            {
               eventBus.fireEvent(new ShowSamplesEvent());
            }
            closeView();
         }
      });
      
      String[] values = {SOURCE_SCRATCH, SOURCE_IMPORT};
      display.setValuesForSelectSourceField(values);
      
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
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardHandler#onShowWizardDefinition(org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardEvent)
    */
   @Override
   public void onShowWizard(ShowWizardEvent event)
   {
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
         eventBus.fireEvent(new ExceptionThrownEvent("Show Wizard must be null"));
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.WizardReturnable#onReturn()
    */
   @Override
   public void onReturn()
   {
      openView();
   }

}
