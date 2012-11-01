/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.appfog.client.url;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.AppfogLocalizationConstant;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for unmaping (unregistering) URLs from application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class UnmapUrlPresenter extends GitPresenter implements UnmapUrlHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      HasValue<String> getMapUrlField();

      HasClickHandlers getMapUrlButton();

      HasClickHandlers getCloseButton();

      ListGridItem<String> getRegisteredUrlsGrid();

      HasUnmapClickHandler getUnmapUrlListGridButton();

      void enableMapUrlButton(boolean enable);
   }

   private AppfogLocalizationConstant localeBundle = AppfogExtension.LOCALIZATION_CONSTANT;

   private Display display;

   private List<String> registeredUrls;

   private String unregisterUrl;

   private String urlToMap;

   private boolean isBindingChanged = false;

   public UnmapUrlPresenter()
   {
      IDE.addHandler(UnmapUrlEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay(List<String> urls)
   {
      registeredUrls = urls;

      display.getMapUrlField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableMapUrlButton((event.getValue() != null && !event.getValue().isEmpty()));
         }
      });

      display.getMapUrlButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            urlToMap = display.getMapUrlField().getValue();
            for (String url : registeredUrls)
            {
               if (url.equals(urlToMap) || ("http://" + url).equals(urlToMap))
               {
                  Dialogs.getInstance().showError(localeBundle.mapUrlAlredyRegistered());
                  return;
               }
            }
            if (urlToMap.startsWith("http://"))
            {
               urlToMap = urlToMap.substring(7);
            }
            mapUrl(urlToMap);
         }
      });

      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getUnmapUrlListGridButton().addUnmapClickHandler(new UnmapHandler()
      {

         @Override
         public void onUnmapUrl(String url)
         {
            askForUnmapUrl(url);
         }
      });

      display.getRegisteredUrlsGrid().setValue(registeredUrls);
   }

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler mapUrlLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         mapUrl(urlToMap);
      }
   };

   private void mapUrl(final String url)
   {
      ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();

      final String server = projectModel.getProperty("af-target").getValue().get(0);
      final String appName = projectModel.getProperty("appfog-application").getValue().get(0);

      try
      {
         AppfogClientService.getInstance().mapUrl(null, null, appName, server, url,
            new AppfogAsyncRequestCallback<String>(null, mapUrlLoggedInHandler, null)
            {
               @Override
               protected void onSuccess(String result)
               {
                  isBindingChanged = true;
                  String registeredUrl = url;
                  if (!url.startsWith("http"))
                  {
                     registeredUrl = "http://" + url;
                  }
                  registeredUrl = "<a href=\"" + registeredUrl + "\" target=\"_blank\">" + registeredUrl + "</a>";
                  String msg = localeBundle.mapUrlRegisteredSuccess(registeredUrl);
                  IDE.fireEvent(new OutputEvent(msg));
                  registeredUrls.add(url);
                  display.getRegisteredUrlsGrid().setValue(registeredUrls);
                  display.getMapUrlField().setValue("");
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void askForUnmapUrl(final String url)
   {
      Dialogs.getInstance().ask(localeBundle.unmapUrlConfirmationDialogTitle(),
         localeBundle.unmapUrlConfirmationDialogMessage(), new BooleanValueReceivedHandler()
      {
         @Override
         public void booleanValueReceived(Boolean value)
         {
            if (value == null || !value)
            {
               return;
            }

            unregisterUrl = url;
            unregisterUrl(unregisterUrl);
         }
      });
   }

   LoggedInHandler unregisterUrlLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         unregisterUrl(unregisterUrl);
      }
   };

   private void unregisterUrl(final String url)
   {
      ProjectModel projectModel = ((ItemContext)selectedItems.get(0)).getProject();

      final String server = projectModel.getProperty("af-target").getValue().get(0);
      final String appName = projectModel.getProperty("appfog-application").getValue().get(0);

      try
      {
         AppfogClientService.getInstance().unmapUrl(null, null, appName, server, url,
            new AppfogAsyncRequestCallback<Object>(null, unregisterUrlLoggedInHandler, null)
            {
               @Override
               protected void onSuccess(Object result)
               {
                  isBindingChanged = true;
                  registeredUrls.remove(url);
                  display.getRegisteredUrlsGrid().setValue(registeredUrls);
                  String unmappedUrl = url;
                  if (!unmappedUrl.startsWith("http"))
                  {
                     unmappedUrl = "http://" + unmappedUrl;
                  }
                  String msg = localeBundle.unmapUrlSuccess(unmappedUrl);
                  IDE.fireEvent(new OutputEvent(msg));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getAppRegisteredUrls();
      }
   };

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationHandler#onRestartApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent)
    */
   @Override
   public void onUnmapUrl(UnmapUrlEvent event)
   {
      isBindingChanged = false;
      if (makeSelectionCheck())
      {
         getAppRegisteredUrls();
      }
   }

   private void getAppRegisteredUrls()
   {
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();

      try
      {
         AutoBean<AppfogApplication> appfogApplication =
            AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

         AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
            new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

         AppfogClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, null, null,
            new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, null, null)
            {
               @Override
               protected void onSuccess(AppfogApplication result)
               {
                  openView(result.getUris());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
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
         if (isBindingChanged)
         {
            String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
            IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
         }
      }
   }

   private void openView(List<String> registeredUrls)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay(registeredUrls);
         IDE.getInstance().openView(display.asView());
         display.enableMapUrlButton(false);
      }
   }

}
