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
package org.exoplatform.ide.extension.cloudfoundry.client.create;

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
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for creating application on CloudFoundry.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateApplicationPresenter.java Jul 8, 2011 11:57:36 AM vereshchaka $
 */
public class CreateApplicationPresenter implements CreateApplicationHandler, ItemsSelectedHandler, ViewClosedHandler,
   ApplicationBuiltHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getCreateButton();
      
      HasClickHandlers getCancelButton();
      
      HasValue<String> getTypeField();
      
      HasValue<Boolean> getChangeTypeCheckItem();
      
      HasValue<String> getNameField();
      
      HasValue<String> getUrlField();
      
      HasValue<Boolean> getCustomUrlCheckItem();
      
      HasValue<String> getInstancesField();
      
      HasValue<String> getMemoryField();
      
      HasValue<Boolean> getIsStartAfterCreationCheckItem();
      
      void enableCreateButton(boolean enable);
      
      void focusInNameField();
      
      void setTypeValues(String[] types);
      
      void enableTypeField(boolean enable);
      
      void enableUrlField(boolean enable);
      
      void enableMemoryField(boolean enable);
      
      void setSelectedIndexForTypeSelectItem(int index);
      
      void focusInUrlField();
      
   }
   
   private static final String DEFAULT_APPLICATION_URL = ".cloudfoundry.com";
   
   private Display display;
   
   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;
   
   private List<Framework> frameworks;
   
   private String warUrl;
   private String name;
   private String type;
   private String url;
   private int instances;
   private int memory;
   private boolean nostart;
   private String workDir;
   
   public CreateApplicationPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(CreateApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }
   
   public void bindDisplay(final List<Framework> frameworks)
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });
      
      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            buildApplication();
         }
      });
      
      display.getChangeTypeCheckItem().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            display.enableTypeField(!event.getValue());
            if (event.getValue())
            {
               String[] values = {""};
               display.setTypeValues(values);
               display.enableMemoryField(false);
               display.getMemoryField().setValue("");
            }
            else
            {
               String[] frameworkArray = getApplicationTypes(frameworks);
               display.setTypeValues(frameworkArray);
               display.enableMemoryField(true);
               display.getMemoryField().setValue("");
               display.setSelectedIndexForTypeSelectItem(0);
               Framework framework = findFrameworkByName(frameworkArray[0]);
               display.getMemoryField().setValue(String.valueOf(framework.getMemory()));
            }
         }
      });
      
      display.getCustomUrlCheckItem().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            display.enableUrlField(event.getValue());
            if (event.getValue())
            {
               display.focusInUrlField();
            }
            else
            {
               display.getUrlField().setValue(display.getNameField().getValue() + ".cloudfoundry.com");
            }
         }
      });

      display.getTypeField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            Framework framework = findFrameworkByName(event.getValue());
            if (framework != null)
            {
               display.getMemoryField().setValue(String.valueOf(framework.getMemory()));
            }
         }
      });
      
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            //if url set automatically, than concatenate name field value and ".cloudfoundry.com"
            if (!display.getCustomUrlCheckItem().getValue())
            {
               display.getUrlField().setValue(display.getNameField().getValue() + ".cloudfoundry.com");
            }
         }
      });
      
      this.frameworks = frameworks;
      String[] values = {""};
      display.setTypeValues(values);
      display.getInstancesField().setValue("1");
      display.enableTypeField(false);
      display.enableUrlField(false);
      display.enableMemoryField(false);
      display.getChangeTypeCheckItem().setValue(true);
      display.focusInNameField();
      String projectName = "";
      if (selectedItems != null && !selectedItems.isEmpty())
      {
         projectName = selectedItems.get(0).getName();
         display.getNameField().setValue(projectName);
      }
      display.getUrlField().setValue(projectName + DEFAULT_APPLICATION_URL);
   }
   
   LoggedInHandler loggedInHandler = new LoggedInHandler()
   {
      
      @Override
      public void onLoggedIn()
      {
         createApplication();
      }
   };
   
   private void buildApplication()
   {

      name = display.getNameField().getValue();
      type = null;
      if (!display.getChangeTypeCheckItem().getValue())
      {
         type = findFrameworkByName(display.getTypeField().getValue()).getType();
         try
         {
            memory = Integer.parseInt(display.getMemoryField().getValue());
         }
         catch (NumberFormatException e)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.errorNumberFormat()));
            return;
         }
      }
      else
      {
         memory = 0;
      }
      url = (display.getCustomUrlCheckItem().getValue()) ? display.getUrlField().getValue() : null;
      instances = Integer.parseInt(display.getInstancesField().getValue());
      nostart = !display.getIsStartAfterCreationCheckItem().getValue();
      workDir = selectedItems.get(0).getWorkDir();
      
      eventBus.addHandler(ApplicationBuiltEvent.TYPE, this);
      eventBus.fireEvent(new BuildApplicationEvent());
      closeView();
   }
   
   private void createApplication()
   {
      CloudFoundryClientService.getInstance().create(name, type, url, instances, memory, nostart, workDir, warUrl,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, loggedInHandler, null)
      {
         @Override
         protected void onSuccess(CloudfoundryApplication result)
         {
            String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationCreatedSuccessfully(result.getName());
            eventBus.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
         }
      });
   }
   
   /**
    * Get the array of application types from list of frameworks.
    * 
    * @param frameworks - list of available frameworks
    * @return an array of types
    */
   private String[] getApplicationTypes(List<Framework> frameworks)
   {
      List<String> frameworkNames = new ArrayList<String>();
      for (Framework framework : frameworks)
      {
         frameworkNames.add(framework.getDisplayName());
      }
      
      return frameworkNames.toArray(new String[frameworkNames.size()]);
   }
   
   private Framework findFrameworkByName(String frameworkName)
   {
      for (Framework framework : frameworks)
      {
         if (frameworkName.equals(framework.getDisplayName()))
         {
            return framework;
         }
      }
      return null;
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationEvent)
    */
   @Override
   public void onCreateApplication(CreateApplicationEvent event)
   {
      CloudFoundryClientService.getInstance().getFrameworks(
         new CloudFoundryAsyncRequestCallback<List<Framework>>(eventBus, null, null)
         {
            @Override
            protected void onSuccess(List<Framework> result)
            {
               showView(result);
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
   
   private void showView(List<Framework> frameworks)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay(frameworks);
         IDE.getInstance().openView(display.asView());
         display.focusInNameField();
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent)
    */
   @Override
   public void onApplicationBuilt(ApplicationBuiltEvent event)
   {
      eventBus.removeHandler(event.getAssociatedType(), this);
      if (event.getJobStatus().getArtifactUrl() != null)
      {
         warUrl = event.getJobStatus().getArtifactUrl();
         createApplication();
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.createApplicationWarIsNull()));
      }
   }

}
