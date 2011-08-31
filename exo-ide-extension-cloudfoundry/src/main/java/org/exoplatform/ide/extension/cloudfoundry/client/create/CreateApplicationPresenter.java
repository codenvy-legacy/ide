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
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

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
      
      HasValue<Boolean> getAutodetectTypeCheckItem();
      
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
   
   private static final CloudFoundryLocalizationConstant lb = CloudFoundryExtension.LOCALIZATION_CONSTANT;
   
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
            validate();
         }
      });
      
      display.getAutodetectTypeCheckItem().addValueChangeHandler(new ValueChangeHandler<Boolean>()
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
               display.getUrlField().setValue(display.getNameField().getValue().toLowerCase() + ".cloudfoundry.com");
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
               String name = display.getNameField().getValue().toLowerCase();
               display.getUrlField().setValue(name + ".cloudfoundry.com");
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
      display.getAutodetectTypeCheckItem().setValue(true);
      display.focusInNameField();
      String projectName = "";
      if (selectedItems != null && !selectedItems.isEmpty())
      {
         projectName = selectedItems.get(0).getName();
         display.getNameField().setValue(projectName);
      }
      display.getUrlField().setValue(projectName.toLowerCase() + DEFAULT_APPLICATION_URL);
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
      if (selectedItems.size() == 0)
      {
         workDir = null;
         return;
      }

      workDir = selectedItems.get(0).getId();
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationEvent)
    */
   @Override
   public void onCreateApplication(CreateApplicationEvent event)
   {
      if (workDir == null)
      {
         String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.selectFolderToCreate();
         eventBus.fireEvent(new ExceptionThrownEvent(msg));
         return;
      }
      isBuildApplication(workDir);
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
   }
   
   //----Implementation------------------------
   
   LoggedInHandler createAppHandler = new LoggedInHandler()
   {
      
      @Override
      public void onLoggedIn()
      {
         createApplication();
      }
   };
   
   private void validate()
   {
      name = display.getNameField().getValue();
      if (display.getAutodetectTypeCheckItem().getValue())
      {
         type = null;
         memory = 0;
      }
      else
      {
         Framework framework = findFrameworkByName(display.getTypeField().getValue());
         type = framework.getType();
         try
         {
            memory = Integer.parseInt(display.getMemoryField().getValue());
         }
         catch (NumberFormatException e)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.errorMemoryFormat()));
            return;
         }
      }
      url = display.getUrlField().getValue();
      try
      {
         instances = Integer.parseInt(display.getInstancesField().getValue());
      }
      catch (NumberFormatException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.errorInstancesFormat()));
         return;
      }
      nostart = !display.getIsStartAfterCreationCheckItem().getValue();
      
      validateData();
   }
   
   private LoggedInHandler validateHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         validateData();
      }
   };
   
   private void validateData()
   {
      CloudFoundryClientService.getInstance().validateAction("create", name, type, url, workDir, instances, memory,
         nostart, new CloudFoundryAsyncRequestCallback<String>(eventBus, validateHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               buildApplication();
               closeView();
            }
         });
   }
   
   /**
    * Check, is work directory contains <code>pom.xml</code> file,
    * 
    * @param workDir
    */
   private void isBuildApplication(String workDir)
   {
      if (!(selectedItems.get(0) instanceof Folder))
      {
         String msg = lb.createApplicationNotFolder(selectedItems.get(0).getName());
         eventBus.fireEvent(new ExceptionThrownEvent(msg));
         return;
      }
      
      final String folderName = selectedItems.get(0).getName();
      if (!workDir.endsWith("/"))
      {
         workDir += "/";
      }
      CloudFoundryClientService.getInstance().checkFileExists(workDir + "pom.xml",
         new AsyncRequestCallback<String>(eventBus)
         {
            @Override
            protected void onSuccess(String result)
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

            @Override
            protected void onFailure(Throwable exception)
            {
               if (exception instanceof ServerException)
               {
                  ServerException serverException = (ServerException)exception;
                  if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus())
                  {
                     eventBus.fireEvent(new ExceptionThrownEvent(lb.createApplicationForbidden(folderName)));
                     return;
                  }
                  else
                  {
                     super.onFailure(exception);
                  }
               }
               super.onFailure(exception);
            }
         });
   }
   
   private void buildApplication()
   {
      eventBus.addHandler(ApplicationBuiltEvent.TYPE, this);
      eventBus.fireEvent(new BuildApplicationEvent());
   }
   
   private void createApplication()
   {
      CloudFoundryClientService.getInstance().create(name, type, url, instances, memory, nostart, workDir, warUrl,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, createAppHandler, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               String msg = lb.applicationCreatedSuccessfully(result.getName());
               if ("STARTED".equals(result.getState()))
               {
                  if (result.getUris().isEmpty())
                  {
                     msg += "<br>" + lb.applicationStartedWithNoUrls();
                  }
                  else
                  {
                     msg += "<br>" + lb.applicationStartedOnUrls(result.getName(), getAppUrlsAsString(result));
                  }
               }
               eventBus.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
            }
            
            @Override
            protected void onFailure(Throwable exception)
            {
               super.onFailure(exception);
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

   private String getAppUrlsAsString(CloudfoundryApplication application)
   {
      String appUris = "";
      for (String uri : application.getUris())
      {
         if (!uri.startsWith("http"))
         {
            uri = "http://" + uri;
         }
         appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
      }
      if (!appUris.isEmpty())
      {
         //crop unnecessary symbols
         appUris = appUris.substring(2);
      }
      return appUris;
   }

}
