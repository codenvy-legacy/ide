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
package org.exoplatform.ide.extension.cloudbees.client.initialize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.initialize.event.DeployApplicationEvent;
import org.exoplatform.ide.extension.cloudbees.client.initialize.event.DeployApplicationHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.java.client.create.CleanProjectEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Presenter for {@link DeployApplicationView}.
 * <p/>
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationNamePresenter.java Jun 27, 2011 11:56:01 AM vereshchaka $
 */
public class DeployApplicationPresenter implements ViewClosedHandler, DeployApplicationHandler
{
   
   interface Display extends IsView
   {
      HasClickHandlers getCreateButton();
      
      HasClickHandlers getCancelButton();
      
      HasValue<String> getDomainField();
      
      HasValue<String> getNameField();
      
      HasValue<String> getApplicationIdField();
      
      void enableCreateButton(boolean enable);
      
      void focusInApplicationNameField();
      
      void setDomainValues(String[] domains);
      
   }
   
   private String[] domains;
   
   private Display display;
   
   private String warUrl;
   
   private String applicationLocation;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   public DeployApplicationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(DeployApplicationEvent.TYPE, this);
   }
   
   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getCreateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doDeployApplication();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            eventBus.fireEvent(new CleanProjectEvent(applicationLocation));
            closeView();
         }
      });
      
      display.getDomainField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            setApplicationId();
         }
      });
      
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue() == null || event.getValue().isEmpty())
            {
               display.enableCreateButton(false);
            }
            else
            {
               display.enableCreateButton(true);
            }
            setApplicationId();
         }
      });
      
      display.setDomainValues(domains);
      display.enableCreateButton(false);
      setApplicationId();
   }
   
   private void doDeployApplication()
   {
      final String applicationId = display.getApplicationIdField().getValue();
      CloudBeesClientService.getInstance().deployWar(applicationId, warUrl, null, 
         new CloudBeesAsyncRequestCallback<Map<String, String>>(eventBus, 
                  new LoggedInHandler()
                  {
                     @Override
                     public void onLoggedIn()
                     {
                        //if login success, than try deploy application again
                        doDeployApplication();
                     }
                  },
                  new LoginCanceledHandler()
                  {
                     @Override
                     public void onLoginCanceled()
                     {
                        //if user clicks Cancel, than clean project
                        eventBus.fireEvent(new CleanProjectEvent(applicationLocation));
                     }
                  }
         )
         {
            @Override
            protected void onSuccess(Map<String, String> result)
            {
               String output = CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationSuccess() + "<br>";
               output += CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationInfo() + "<br>";
               Iterator<Entry<String, String>> it = result.entrySet().iterator();
               while (it.hasNext())
               {
                  Entry<String, String> entry = (Entry<String, String>)it.next();
                  output += entry.getKey() + " : " + entry.getValue() + "<br>";
               }
               eventBus.fireEvent(new OutputEvent(output, Type.INFO));
               eventBus.fireEvent(new CleanProjectEvent(applicationLocation));
               closeView();
            }
         });
   }
   
   /**
    * Set the application id, which has the next form:
    * "domain/aplicationName".
    */
   private void setApplicationId()
   {
      final String domain = display.getDomainField().getValue();
      String name = display.getNameField().getValue();
      if (name == null)
         name = "";
      display.getApplicationIdField().setValue(domain + "/" + name);
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
    * @see org.exoplatform.ide.extension.cloudbees.client.initialize.event.DeployApplicationHandler#onDeployApplication(org.exoplatform.ide.extension.cloudbees.client.initialize.event.DeployApplicationEvent)
    */
   @Override
   public void onDeployApplication(DeployApplicationEvent event)
   {
      applicationLocation = event.getApplicationDir();
      warUrl = event.getWarUrl();
      getDomains();
   }
   
   private void getDomains()
   {
      CloudBeesClientService.getInstance().getDomains(
         new CloudBeesAsyncRequestCallback<List<String>>(eventBus, 
                  new LoggedInHandler()
                  {
                     @Override
                     public void onLoggedIn()
                     {
                        getDomains();
                     }
                  },
                  new LoginCanceledHandler()
                  {
                     @Override
                     public void onLoginCanceled()
                     {
                        eventBus.fireEvent(new CleanProjectEvent(applicationLocation));
                     }
                  })
         {
            @Override
            protected void onSuccess(List<String> result)
            {
               showView(result);
            }
         });
   }
   
   private void showView(List<String> domains)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         this.domains = new String[domains.size()];
         this.domains = domains.toArray(this.domains);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.focusInApplicationNameField();
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

}
