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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
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
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

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
      
      /**
       * Get the checkbox, that indicates is user want to enter custom URL.
       * @return
       */
      HasValue<Boolean> getUrlCheckItem();
      
      HasValue<String> getInstancesField();
      
      HasValue<String> getMemoryField();
      
      HasValue<String> getServerField();
      
      HasValue<Boolean> getIsStartAfterCreationCheckItem();
      
      void enableCreateButton(boolean enable);
      
      void focusInNameField();
      
      void setTypeValues(String[] types);
      
      void enableTypeField(boolean enable);
      
      void enableUrlField(boolean enable);
      
      void enableMemoryField(boolean enable);
      
      void setSelectedIndexForTypeSelectItem(int index);
      
      void focusInUrlField();
      
      /**
       * Set the list of servers to ServerSelectField.
       * 
       * @param servers
       */
      void setServerValues(String[] servers);
      
   }
   
   private class AppData
   {
      String server;
      String name;
      String type;
      String url;
      int instances;
      int memory;
      boolean nostart;
      String workDir;
      
      public AppData(String server, String name, String type, String url, int instances, int memory,
         boolean nostart, String workDir)
      {
         this.server = server;
         this.name = name;
         this.type = type;
         this.url = url;
         this.instances = instances;
         this.memory = memory;
         this.nostart = nostart;
         this.workDir = workDir;
      }
   }
   
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
   
   /**
    * Public url to war file of application. 
    */
   private String warUrl;
   
   /**
    * Store application data in format,
    * that convenient to send to server.
    */
   private AppData appData;
   
   public CreateApplicationPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(CreateApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }
   
   public void bindDisplay()
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
            appData = getAppDataFromForm();
            
            validateData(appData);
         }
      });
      
      display.getAutodetectTypeCheckItem().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            display.enableTypeField(!event.getValue());
            display.enableMemoryField(!event.getValue());
            
            if (event.getValue())
            {
               display.setTypeValues(new String[]{""});
               display.getMemoryField().setValue("");
            }
            else
            {
               final String[] frameworkArray = getApplicationTypes(frameworks);
               display.setTypeValues(frameworkArray);
               display.setSelectedIndexForTypeSelectItem(0);
               Framework framework = findFrameworkByName(frameworkArray[0]);
               display.getMemoryField().setValue(String.valueOf(framework.getMemory()));
            }
         }
      });
      
      display.getUrlCheckItem().addValueChangeHandler(new ValueChangeHandler<Boolean>()
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
               updateUrlField();
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
            //if url set automatically, than try to create url using server and name
            if (!display.getUrlCheckItem().getValue())
            {
               updateUrlField();
            }
         }
      });
      
      display.getServerField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            //if url set automatically, than try to create url using server and name
            if (!display.getUrlCheckItem().getValue())
            {
               updateUrlField();
            }
         }
      });
      
      //set the state of fields
      display.enableTypeField(false);
      display.enableUrlField(false);
      display.enableMemoryField(false);
      display.focusInNameField();
      
      //set default values to fields
      display.setTypeValues(new String[]{""});
      display.getInstancesField().setValue("1");
      display.getAutodetectTypeCheckItem().setValue(true);
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
      if (selectedItems == null || selectedItems.size() == 0)
      {
         String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.selectFolderToCreate();
         eventBus.fireEvent(new ExceptionThrownEvent(msg));
         return;
      }
      if (selectedItems.get(0) instanceof Folder)
      {
         checkIsProject((FolderModel)selectedItems.get(0));
      }
      else
      {
         String msg = lb.createApplicationNotFolder(selectedItems.get(0).getName());
         eventBus.fireEvent(new ExceptionThrownEvent(msg));
         return;
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
    * @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent)
    */
   @Override
   public void onApplicationBuilt(ApplicationBuiltEvent event)
   {
      eventBus.removeHandler(event.getAssociatedType(), this);
      if (event.getJobStatus().getArtifactUrl() != null)
      {
         warUrl = event.getJobStatus().getArtifactUrl();
         createApplication(appData);
      }
   }
   
   //----Implementation------------------------
   
   private String getUrlByServerAndName(String serverUrl, String name)
   {
      int index = serverUrl.indexOf(".");
      if (index < 0)
      {
         return name.toLowerCase();
      }
      final String domain = serverUrl.substring(index, serverUrl.length());
      return "http://" + name.toLowerCase() + domain;
   }
   
   /**
    * Update the URL field, using values from server and name field.
    */
   private void updateUrlField()
   {
      final String url =
         getUrlByServerAndName(display.getServerField().getValue(), display.getNameField().getValue());
      display.getUrlField().setValue(url);
   }
   
   private void validateData(final AppData app)
   {
      LoggedInHandler validateHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            validateData(app);
         }
      };
      
      CloudFoundryClientService.getInstance().validateAction("create", app.server, app.name, app.type, app.url, app.workDir, 
         app.instances, app.memory, app.nostart, new CloudFoundryAsyncRequestCallback<String>(eventBus, validateHandler, null, app.server)
         {
            @Override
            protected void onSuccess(String result)
            {
               buildApplication();
               closeView();
            }
         });
   }
   
   private void getFrameworks()
   {
      LoggedInHandler getFrameworksLoggedInHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            getFrameworks();
         }
      };
      
      CloudFoundryClientService.getInstance().getFrameworks(
         new CloudFoundryAsyncRequestCallback<List<Framework>>(eventBus, getFrameworksLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(List<Framework> result)
            {
               openView(result);
            }
         });
   }
   
   private void buildApplication()
   {
      eventBus.addHandler(ApplicationBuiltEvent.TYPE, this);
      eventBus.fireEvent(new BuildApplicationEvent());
   }
   
   private void createApplication(final AppData app)
   {
      LoggedInHandler createAppHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            createApplication(app);
         }
      };
      
      CloudFoundryClientService.getInstance().create(app.server, app.name, app.type, app.url, app.instances, app.memory, 
         app.nostart, app.workDir, warUrl, 
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, createAppHandler, null, app.server)
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
               eventBus.fireEvent(new OutputEvent(lb.applicationCreationFailed(), OutputMessage.Type.INFO));
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
   
   /**
    * Find framework from list by name.
    * @param frameworkName
    * @return
    */
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
   
   private void openView(List<Framework> frameworks)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         this.frameworks = frameworks;
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.focusInNameField();
         getServers();
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent("View Create Cloudfoundry Application must be null"));
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
   
   /**
    * Process values from application create form,
    * and store data in bean in format,
    * that is convenient to send to server
    * 
    * @return {@link AppData}
    */
   private AppData getAppDataFromForm()
   {
      String server = display.getServerField().getValue();
      if (server == null || server.isEmpty())
      {
         //is server is empty, set value to null
         //it is need for client service
         //if null, than service will not send this parameter
         server = null;
      }
      String name = display.getNameField().getValue();
      String type;
      int memory = 0;
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
         }
      }
      
      String url;
      
      if (display.getUrlCheckItem().getValue())
      {
         url = display.getUrlField().getValue();
         if (url == null || url.isEmpty())
         {
            url = null;
         }
      }
      else
      {
         url = null;
      }
      
      int instances = 0;
      try
      {
         instances = Integer.parseInt(display.getInstancesField().getValue());
      }
      catch (NumberFormatException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.errorInstancesFormat()));
      }
      boolean nostart = !display.getIsStartAfterCreationCheckItem().getValue();
      
      return new AppData(server, name, type, url, instances, memory, nostart, selectedItems.get(0).getId());
   }
   
   /**
    * Check is selected item project and can be built.
    */
   private void checkIsProject(final FolderModel folder)
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(folder,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<List<Item>>(
               new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onSuccess(List<Item> result)
               {
                  folder.getChildren().setItems(result);
                  for (Item i : result)
                  {
                     if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName()))
                     {
                        getFrameworks();
                        return;
                     }
                  }
                  eventBus.fireEvent(new ExceptionThrownEvent(lb.createApplicationForbidden(folder.getName())));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  eventBus.fireEvent(new ExceptionThrownEvent(exception,
                     "Service is not deployed.<br>Parent folder not found."));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }
   
   /**
    * Get the list of server and put them to select field.
    */
   private void getServers()
   {
      CloudFoundryClientService.getInstance().getTargets(new AsyncRequestCallback<List<String>>()
      {
         @Override
         protected void onSuccess(List<String> result)
         {
            if (result.isEmpty())
            {
               display.setServerValues(new String[]{CloudFoundryExtension.DEFAULT_SERVER});
               display.getServerField().setValue(CloudFoundryExtension.DEFAULT_SERVER);
            }
            else
            {
               String[] servers = result.toArray(new String[result.size()]);
               display.setServerValues(servers);
               display.getServerField().setValue(servers[0]);
            }
            display.getNameField().setValue(selectedItems.get(0).getName());
            updateUrlField();
         }
      });
   }

}
