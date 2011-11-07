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
package org.exoplatform.ide.extension.samples.client.startpage;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.event.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.samples.load.ShowSamplesEvent;
import org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Presenter for welcome view.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WelcomePresenter.java Aug 25, 2011 12:27:27 PM vereshchaka $
 */
public class StartPagePresenter implements OpenStartPageHandler, ViewClosedHandler
{
   
   public interface Display extends IsView
   {
      HasClickHandlers getSamplesLink();
      
      HasClickHandlers getProjectLink();
      
      HasClickHandlers getConvertLink();
   }
   
   private Display display;
   
   public StartPagePresenter()
   {
      IDE.addHandler(OpenStartPageEvent.TYPE, this);
   }
   
   private void bindDisplay()
   {
      display.getSamplesLink().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ShowSamplesEvent());
         }
      });
      
      display.getProjectLink().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ShowWizardEvent());
         }
      });
      
      display.getConvertLink().addClickHandler(new ClickHandler()
      {
         
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ConvertToProjectEvent());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.OpenStartPageHandler.OpenWelcomeHandler#onOpenStartPage(org.exoplatform.ide.client.OpenStartPageEvent.OpenWelcomeEvent)
    */
   @Override
   public void onOpenStartPage(OpenStartPageEvent event)
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
         IDE.fireEvent(new ExceptionThrownEvent("Start Page View must be null"));
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

}
